package com.chuxin.androidpush.sdk.push.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.chuxin.androidpush.sdk.BuildConfig;
import com.chuxin.androidpush.sdk.push.net.Message.Action;
import com.chuxin.androidpush.sdk.push.utils.Constant;
import com.chuxin.androidpush.sdk.push.utils.RkPushLog;
import com.chuxin.androidpush.sdk.push.utils.StoreUtil;
import com.chuxin.androidpush.sdk.push.utils.TeeLog;
import com.chuxin.androidpush.sdk.push.utils.UUID;


import android.content.Context;

public class PushAgent extends Thread {

    enum ConnectionState {
        OFFLINE,
        ONLINE,
    }

    ;

    static private final String TAG = "Push";
    static private String PUSH_SERVER;

    static {
        if (BuildConfig.DEBUG) {
            PUSH_SERVER = "192.168.3.30";
        }else{
            PUSH_SERVER = "pushfamily.hmammon.cn";
            //push.family.rekoo.net
        }
    }

    //	static private final String PUSH_SERVER = "183.60.244.70"; //浣欎含璋冭瘯鏈嶅姟鍣�
//	static private final String PUSH_SERVER = "192.168.2.193";
//	static private final String PUSH_SERVER = "59.108.111.79"; //内网push隐射
    static private final int PUSH_PORT = 2001;

    static private final int SOCKET_TIMEOUT = 60 * 1000; // 60 s
    static private final int SOCKET_READ_TIMEOUT = 5 * 1000;
    static private final int INPUT_BUFFER_SIZE = 4096;
    //    static private final int SOCKET_IDLE_THRESHOLD = 1000 * (60 * 4 + 50);
    static private final int SOCKET_IDLE_THRESHOLD = 1000 * (60 * 2 + 30); //2.5鍒嗛挓寮哄埗ping涓�
    static private final int DEFAULT_RETRY_INTERVAL = 1 * 1000;

    private ConnectionState mConnectionState;

    private Socket mSocket;
    private OutputStream mOutputStream;
    private InputStream mInputStream;

    private Context mBindingContext = null;
    private Thread mPollingThread = null;
    private Packer mPacker = null;
    private long mIdleTime = 0;
    private long mPackageSize = 0;
    private int mRetryInterval = DEFAULT_RETRY_INTERVAL;
    private boolean mQuitFlag = false;

    private long mLastPingTimestamp = 0;
    private long mLastPackageRecvTimestamp = 0;

    private static boolean mIsPolling = false; //polling榛樿鏄湭鎵ц
    private static boolean mPollingThreadRunning = false;

    static private PushAgent sInstance = new PushAgent();

    List<byte[]> mPendingBytes = new ArrayList<byte[]>();

    private PushAgent() {
        mConnectionState = ConnectionState.OFFLINE;
    }

    // singleton 
    static public PushAgent getInstance() {
        return sInstance;
    }

    public Context getBindingContext() {
        return mBindingContext;
    }

//    public Storage getStorage() {
//    	return new Storage(mBindingContext);
//    }

    private void requestPing() {
        // proto : ping
        byte[] bytes = mPacker.ping(UUID.devUUID(mBindingContext));
        if (bytes != null) {
            synchronized (mPendingBytes) {
                mPackageSize += bytes.length;
                RkPushLog.i(TAG, "PING: " + mPackageSize + " " + bytes.length);
                StoreUtil.getInstance().writeIn("PING: " + mPackageSize + " " + bytes.length);
                mPendingBytes.add(bytes);
            }
            mLastPingTimestamp = System.currentTimeMillis();
        } else {
            RkPushLog.e(TAG, "failed to ping()");
        }
    }

    private void requestLogin() {
        // proto : login
        byte[] bytes = mPacker.login(UUID.devUUID(mBindingContext));
        if (bytes != null) {
            synchronized (mPendingBytes) {
                mPackageSize += bytes.length;
                RkPushLog.i(TAG, "LOGIN: " + mPackageSize + " " + bytes.length);
                StoreUtil.getInstance().writeIn("LOGIN: " + mPackageSize + " " + bytes.length);
                mPendingBytes.add(bytes);
            }
            mLastPingTimestamp = System.currentTimeMillis();
        } else {
            RkPushLog.e(TAG, "failed to login()");
        }
    }

    public void requestRegister() {
        byte[] bytes = mPacker.register(UUID.devUUID(mBindingContext));
        if (bytes != null) {
            synchronized (mPendingBytes) {
                mPackageSize += bytes.length;
                RkPushLog.i(TAG, "REGISTER: " + mPackageSize + " " + bytes.length);
                StoreUtil.getInstance().writeIn("REGISTER: " + mPackageSize + " " + bytes.length);
                mPendingBytes.add(bytes);
            }
        } else {
            RkPushLog.e(TAG, "failed to register()");
        }
    }

    public void requestUnregister(String app, String uid) {
        byte[] bytes = mPacker.unregister(UUID.devUUID(mBindingContext), app, uid);
        if (bytes != null) {
            synchronized (mPendingBytes) {
                mPackageSize += bytes.length;
                RkPushLog.i(TAG, "UNREGISTER: " + mPackageSize + " " + bytes.length);
                mPendingBytes.add(bytes);
            }
        } else {
            RkPushLog.e(TAG, "failed to unregister() for [" + app + "]" + uid);
        }
    }

    public void requestRead(String app, int msgid) {
        byte[] bytes = mPacker.read(UUID.devUUID(mBindingContext), app, msgid);
        if (bytes != null) {
            synchronized (mPendingBytes) {
                mPackageSize += bytes.length;
                RkPushLog.i(TAG, "READ: " + mPackageSize + " " + bytes.length + " (" + app + "." + msgid + ")");
                mPendingBytes.add(bytes);
            }
        } else {
            RkPushLog.e(TAG, "failed to read() for [" + app + "]" + msgid);
        }
    }

    // init 
    public void init(Context context) {
        mSocket = null;
        mOutputStream = null;
        mInputStream = null;
        mConnectionState = ConnectionState.OFFLINE;
        mBindingContext = context;
        mPacker = new Packer((byte) 0);
    }

    private void flush() throws IOException {
        RkPushLog.i(TAG, "Enter flush()");
        if (mOutputStream == null) {
            RkPushLog.i(TAG, "Leave flush() due to NULL");
            return;
        }

        RkPushLog.i(TAG, "  ready to send data");
        synchronized (mPendingBytes) {
            RkPushLog.i(TAG, "  mPendingBytes.size()=" + String.valueOf(mPendingBytes.size()));
            if (mPendingBytes.size() > 0) {
                while (mPendingBytes.size() > 0) {
                    byte[] bytes = mPendingBytes.remove(0);
                    RkPushLog.i(TAG, "    byte.size()=" + String.valueOf(bytes.length));
                    try {
                        mOutputStream.write(bytes);
                        mOutputStream.flush();
                        RkPushLog.i(TAG, "    mOutputStream write complete***********");
                        StoreUtil.getInstance().writeIn(">mOutputStream write complete***********");
                        mIdleTime = 0;
                    } catch (IOException e) {
                        RkPushLog.e(TAG, "flush() failed!");
                        e.printStackTrace();
                        mPendingBytes.add(0, bytes);
                        throw e;
                    }
                }
            }
        }

        RkPushLog.i(TAG, "Leave flush()");


    }

    // launch polling
    public void launchPolling() {
        RkPushLog.i(TAG, "Enter launchPolling");
        StoreUtil.getInstance().writeIn("Enter launchPolling");
        mQuitFlag = false;

        if (!isOnline()) {
            RkPushLog.i(TAG, "push is offline, ready to reset connection");
            StoreUtil.getInstance().writeIn("push is offline, ready to reset connection");
            resetConnection();

            if (!isOnline()) {
                RkPushLog.e(Constant.ERROR_CODE_REMOTE_SERVER_DOWN, "Can not connection to remote push server!");
                StoreUtil.getInstance().writeIn("Can not connection to remote push server!");
                return;
            }
        }

        if (mPollingThread != null) {
            if (!mPollingThread.isAlive()) {
                RkPushLog.i(TAG, "launchPolling: kill the thread");
                StoreUtil.getInstance().writeIn("launchPolling: kill the thread");
                try {
                    mPollingThread.interrupt();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mPollingThread = null;
                mPollingThreadRunning = false;
            }
        }

        if ((!mQuitFlag) && (mPollingThread == null)) {
            RkPushLog.i(TAG, " ready start new thread");
            StoreUtil.getInstance().writeIn(" ready start new thread");
            mPollingThread = new Thread() {
                @Override
                public void run() {
                    if (mPollingThreadRunning) {
                        return;
                    }
                    mPollingThreadRunning = true;
                    RkPushLog.i(TAG, "launchPolling: new thread is launched!");
                    StoreUtil.getInstance().writeIn("launchPolling: new thread is launched!");
                    while (!mQuitFlag) {
                        RkPushLog.i(TAG, "launchPolling method:while loop running");
                        StoreUtil.getInstance().writeIn("launchPolling method:while loop running");
                        // do logic
                        polling();

                        // hit error, the connection is break, check if it is time to quit
                        if (mQuitFlag)
                            break;

                        // well, take 1 second sleep
                        try {
                            Thread.sleep(1 * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            break;
                        }

                        // check if need to continue again
                        if (mQuitFlag)
                            break;

                        // reset the network
                        resetConnection();

                        // if network is bad, then stop;
                        if (!isOnline()) {
                            break;
                        }
                    }
                    RkPushLog.i(TAG, "launchPolling: thread finished!");
                    StoreUtil.getInstance().writeIn("launchPolling: thread finished!");
                    mPollingThreadRunning = false;
                }
            };

            mPollingThread.start();
        } else {
            RkPushLog.i(TAG, "launchPolling: the pollingThread has already been running now!");
            StoreUtil.getInstance().writeIn("launchPolling: the pollingThread has already been running now!");
        }
    }

    public boolean isOnline() {
        return (mConnectionState == ConnectionState.ONLINE);
    }

    public void quit() {
        TeeLog.i(TAG, "enter quit.");
        mQuitFlag = true;
        if (mPollingThread != null) {
            Thread tmp = mPollingThread;
            mPollingThread = null;
            mPollingThreadRunning = false;
            tmp.interrupt();
        }

        dropConnection();
    }

    public void resetConnection() {
        RkPushLog.w(TAG, "resetConnection() with PUSH SERVER: " + PUSH_SERVER + ":" + PUSH_PORT);

        dropConnection();

        try {
            SocketAddress server = new InetSocketAddress(PUSH_SERVER, PUSH_PORT);
            mSocket = new Socket();
            mSocket.connect(server, SOCKET_TIMEOUT);
            if (mSocket.isConnected()) {
                mSocket.setTcpNoDelay(true);
                mSocket.setSoTimeout(SOCKET_READ_TIMEOUT);
                mOutputStream = mSocket.getOutputStream();
                mInputStream = mSocket.getInputStream();
                mLastPackageRecvTimestamp = System.currentTimeMillis();
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
            dropConnection();
            RkPushLog.w(TAG, "resetConnection() with PUSH SERVER: " + e.getMessage());
            return;
        } catch (SocketException e) {
            e.printStackTrace();
            dropConnection();
            RkPushLog.w(TAG, "resetConnection() with PUSH SERVER: " + e.getMessage());
            return;
        } catch (IOException e) {
            e.printStackTrace();
            dropConnection();
            RkPushLog.w(TAG, "resetConnection() with PUSH SERVER: " + e.getMessage());
            return;
        }

        mConnectionState = ConnectionState.ONLINE;
        mPendingBytes.clear();
    }

    private void dropConnection() {
        mConnectionState = ConnectionState.OFFLINE;

        RkPushLog.i(TAG, "dropConnection");
        if (mInputStream != null) {
            try {
                mInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mInputStream = null;
        }

        if (mOutputStream != null) {
            try {
                mOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mOutputStream = null;
        }

        if (mSocket != null) {
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mSocket = null;
        }
    }

    private void polling() {
        if (mIsPolling) {
            return;
        }
        mIsPolling = true;

        RkPushLog.i(TAG, "PushAgent --> polling method");
        StoreUtil.getInstance().writeIn("PushAgent --> polling method");
        Buffer buffer = new Buffer(INPUT_BUFFER_SIZE);
        Parser parser = new Parser();

        Message message = null;
        Action action = null;

        requestLogin();
        mIdleTime = 0;

        while (mConnectionState == ConnectionState.ONLINE) {

            long now = System.currentTimeMillis();
            long diff = now - mLastPingTimestamp;

            RkPushLog.d(TAG, "PING_TEST: now=" + String.valueOf(now) + ", last=" + String.valueOf(mLastPingTimestamp) + ", diff=" + String.valueOf(now - mLastPingTimestamp));
            if (diff >= SOCKET_IDLE_THRESHOLD * 2) {
                dropConnection();
                break;
            }

            //备注：目前是2.5分钟发一个ping,如果收不到服务器的握手信息，马上重连
            if ((now - mLastPackageRecvTimestamp) >= (SOCKET_IDLE_THRESHOLD)) { //目前需要2.5分钟才有一个ping，时间有点长，故一个ping检测
                dropConnection();
                break;
            }

            if ((now - mLastPingTimestamp) >= SOCKET_IDLE_THRESHOLD)
                requestPing();

            try {
                RkPushLog.i(TAG, "+++++++++++ready to send msg++++++++++");
                StoreUtil.getInstance().writeIn("ready to send msg++++++++++");
                flush();//鍐欏叆缃戠粶
                RkPushLog.i(TAG, "----------------ready to receive msg---------");
                StoreUtil.getInstance().writeIn("ready to receive msg---------");
                int rc = mInputStream.read(buffer.data,
                        buffer.length,
                        buffer.data.length - buffer.length);

                RkPushLog.i(TAG, "Stream read returns " + rc);
                StoreUtil.getInstance().writeIn("Stream read returns " + rc);

                if (rc == -1) {
                    mRetryInterval += DEFAULT_RETRY_INTERVAL;
//            		TeeLog.w(TAG, "socket closed, connection will be closed");
                    dropConnection();
                    break;
                }

                if (rc > 0) {
                    mLastPingTimestamp = mLastPackageRecvTimestamp = System.currentTimeMillis();
                    mRetryInterval = DEFAULT_RETRY_INTERVAL;

//                    mIdleTime = 0;
                    buffer.length += rc;
                    mPackageSize += rc;
                    RkPushLog.i(TAG, "RECV: " + mPackageSize + ",rc= " + rc);
                    StoreUtil.getInstance().writeIn("RECV: " + mPackageSize + ",rc= " + rc);

                    do {
                        try {
                            message = parser.parse(buffer);
                        } catch (Exception e) {
                            e.printStackTrace();

                            RkPushLog.e(TAG, "parse socket stream error!");
                            StoreUtil.getInstance().writeIn("parse socket stream error!");
                            throw new IOException();
                        }

                        if (message != null) {
                            action = message.genAction();
                            if (action != null)
                                action.execute(); //鏈夋秷鎭氨瑕佸脊鍑簄otification
                        }
                    } while (message != null);
                }

            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                RkPushLog.e(TAG, "socket error, SocketTimeoutException:" + e.toString());
                StoreUtil.getInstance().writeIn("socket error, SocketTimeoutException");

                // socket read hit timeout issue
//                mIdleTime += SOCKET_READ_TIMEOUT;

//                if (mIdleTime >= SOCKET_IDLE_THRESHOLD) {
//                	mIdleTime = 0;
//                	requestPing();
//                }

            } catch (IOException e) {
                RkPushLog.e(TAG, "socket error, connection will be closed!");
                StoreUtil.getInstance().writeIn("socket error, connection will be closed!");

                e.printStackTrace();
                dropConnection();

            } catch (Exception e) {
                RkPushLog.e(TAG, "push other exception:" + e.getMessage());
                StoreUtil.getInstance().writeIn("socket error, connection will be closed!");

                e.printStackTrace();
                dropConnection();
            }
        }//end while
        mIsPolling = false;
    }//end polling method

}

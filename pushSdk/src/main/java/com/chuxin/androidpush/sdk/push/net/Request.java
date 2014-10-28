package com.chuxin.androidpush.sdk.push.net;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;


import org.msgpack.MessagePack;
import org.msgpack.packer.BufferPacker;

import com.chuxin.androidpush.sdk.push.utils.AppInfo;
import com.chuxin.androidpush.sdk.push.utils.Constant;
import com.chuxin.androidpush.sdk.push.utils.TeeLog;
import com.chuxin.androidpush.sdk.push.utils.Utilities;


public class Request {
	
	private Request impl;
	
	public Request ping(final String udid) {
		return impl.ping(udid);
	}

	public Request login(final String udid) {
		return impl.login(udid);
	}
	
	public Request register(final String udid) {
		return impl.register(udid);
	}
	
	public Request unregister(final String udid, final String app, final String appUid) {
		return impl.unregister(udid, app, appUid);
	}

	public Request read(final String udid, final String app, int msgid) {
		return impl.read(udid, app, msgid);
	}
	
	public byte[] toBytes() {
		return impl.toBytes();
	}
	
	protected Request() {}
	
	public Request(int version) {
		impl = new Request_v0();
	}
	
	// privates
	private static int now() {
    	Calendar calendar = Calendar.getInstance();
    	return (int)(calendar.getTimeInMillis() / 1000);		
	}
	private static byte[] tokenize(String s) {
    	MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
	    	return md5.digest(s.getBytes());
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}

	static class Request_v0 extends Request {
		
		public Request read(final String udid, final String app, final int msgid) {
			return new Request() {
				private static final String TAG = "ReadRequest";
				private static final int PROTO_NO = 102;
				private String secret() {
					return "gWc3KZ8oVIFc%$jx";
				}

			    public byte[] toBytes() {
			    	MessagePack msgpack = new MessagePack();
			    	BufferPacker packer = msgpack.createBufferPacker();

			    	int now = now();
			    	byte[] hash = tokenize(secret() + udid + now);
					
			    	try {
						packer.writeArrayBegin(5);
						
			    		packer.write(PROTO_NO);							// 1
				    	packer.write(now);								// 2
				    	packer.write(Utilities.hexToString(hash, 3, 8));// 3
				    	
				    	packer.write(app);								// 4
				    	packer.write(msgid);							// 5

				    	packer.writeArrayEnd();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

			    	return packer.toByteArray();
			    }
			};
		}
		
		public Request unregister(final String udid, final String app, final String appUid) {
			return new Request() {
				private static final String TAG = "UnregisterRequest";
				private static final int PROTO_NO = 101;
				
				private String secret() {
					return "gWc3KZ8oVIFc%$jx";
				}

			    public byte[] toBytes() {
			    	MessagePack msgpack = new MessagePack();
			    	BufferPacker packer = msgpack.createBufferPacker();
			    	
			    	int now = now();
			    	byte[] hash = tokenize(secret() + udid + now);
										
			    	try {
						packer.writeArrayBegin(5);
						
			    		packer.write(PROTO_NO);							// 1
				    	packer.write(now);								// 2
				    	packer.write(Utilities.hexToString(hash, 3, 8));// 3
				    	
				    	packer.write(app);								// 4
				    	packer.write(appUid);							// 5
				    	
				    	packer.writeArrayEnd();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

			    	return packer.toByteArray();
			    }
			};
		}
		
		public Request register(final String udid) {
			return new Request() {
				private static final String TAG = "RegisterRequest";
				private static final int PROTO_NO = 100;
				
				private String secret() {
					return "gWc3KZ8oVIFc%$jx";
				}

			    public byte[] toBytes() {
			    	MessagePack msgpack = new MessagePack();
			    	BufferPacker packer = msgpack.createBufferPacker();
			    	
			    	int now = now();
			    	byte[] hash = tokenize(secret() + udid + now);
					
					int appNum = 0;
					AppInfo[] apps = null;
//					apps = PushAgent.getInstance().getStorage().getAllApps();
//					if (apps == null) {
//						appNum = 0;
//					} else {
//						appNum = apps.length;
//					}
					
			    	try {
						packer.writeArrayBegin(4);
						
			    		packer.write(PROTO_NO);							// 1
				    	packer.write(now);								// 2
				    	packer.write(Utilities.hexToString(hash, 3, 8));// 3


				    		// installed packages
				    		packer.writeArrayBegin(appNum * 2);
					    	for (int i = 0; i < appNum; i++) {
					    		packer.write(apps[i].mName);
					    		packer.write(apps[i].mUUID);
					    	}
					    	packer.writeArrayEnd();
				    	
				    	packer.writeArrayEnd();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

			    	return packer.toByteArray();
			    }
			};
		}
		
		public Request ping(final String udid) {
			return new Request() {
				private static final String TAG = "PingRequest";
				private static final int PROTO_NO = 1;
				
				private String secret() {
					return "gWc3KZ8oVIFc%$jx";
				}

			    public byte[] toBytes() {
			    	MessagePack msgpack = new MessagePack();
			    	BufferPacker packer = msgpack.createBufferPacker();
			    	
			    	int now = now();
			    	byte[] hash = tokenize(secret() + udid + now);
					
			    	try {
						packer.writeArrayBegin(3);
						
			    		packer.write(PROTO_NO);				// 1
				    	packer.write(now);					// 2
				    	packer.write(Utilities.hexToString(hash, 3, 8));
				    	
				    	packer.writeArrayEnd();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

			    	return packer.toByteArray();
			    }
			};
		}

		public Request login(final String udid) {
			return new Request() {
				private static final String TAG = "LoginRequest";
				private static final int PROTO_NO = 0;

				private String secret() {
					return "rmX*4XeLqmn-r09+";
				}

			    public byte[] toBytes() {
			    	MessagePack msgpack = new MessagePack();
			    	BufferPacker packer = msgpack.createBufferPacker();

			    	int now = now();
			    	byte[] hash = tokenize(secret() + udid + now);
					
					AppInfo[] apps = null;
					int appNum = 0;
//					Storage storage = PushAgent.getInstance().getStorage();
//					apps = storage.getAllApps();
//					if (apps == null) {
//						appNum = 0;
//					} else {
//						appNum = apps.length;
//					}
					
			    	try {
						packer.writeArrayBegin(6);
						
			    		packer.write(PROTO_NO);				// 1
			    		packer.write(Constant.PUSH_VERSION);// 2
				    	packer.write(udid);					// 3
				    	packer.write(now);					// 4
				    	packer.write(Utilities.hexToString(hash, 3, 8));	// 5
				    	
				    		// installed packages
					    	packer.writeArrayBegin(appNum * 2);
					    	for (int i = 0; i < appNum; i++) {
					    		packer.write(apps[i].mName);
					    		packer.write(apps[i].mUUID);
					    		
					    		TeeLog.d(TAG, "sending " + apps[i].mName + ":" + apps[i].mUUID);
					    	}
					    	packer.writeArrayEnd();

						packer.writeArrayEnd();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

			    	return packer.toByteArray();
			    }			
			};
		}
	}	
}

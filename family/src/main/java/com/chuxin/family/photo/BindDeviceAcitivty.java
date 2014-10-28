package com.chuxin.family.photo;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chuxin.family.R;
import com.chuxin.family.app.CxRootActivity;
import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.utils.ToastUtil;
import com.hmammon.photointerface.DeviceParseUtils;
import com.hmammon.photointerface.NetApi;
import com.hmammon.photointerface.PIApi;
import com.hmammon.photointerface.ZedLog;
import com.hmammon.photointerface.domain.BindInfo;
import com.hmammon.photointerface.domain.ErrorInfo;
import com.hmammon.photointerface.domain.GetDeviceInfo;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 绑定设备
 * Created by Xcfh on 2014/10/21.
 */
public class BindDeviceAcitivty extends CxRootActivity implements View.OnClickListener, Handler.Callback {

    private TextView tvUserId;
    private EditText etDevId;
    private EditText etNickname;
    private Button btnSubmit;
    private BindInfo bindInfo;
    private ListView lvDevice;
    private Handler handler;
    private DeviceAdapter deviceAdapter;

    private static final int MSG_GET_DEVICE_SUCCESS = 100;

    private static final int MSG_BIND_SUCCESS = 200;

    private static final int MSG_BIND_FAILED = 201;

    private static final int MSG_UNBIND_SUCCESS = 300;

    private static final int MSG_UNBIND_FAILED = 301;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_device);
        tvUserId = (TextView) findViewById(R.id.adb_tv_user_family_id);
        etDevId = (EditText) findViewById(R.id.adb_et_device_id);
        etNickname = (EditText) findViewById(R.id.adb_et_nickname);
        btnSubmit = (Button) findViewById(R.id.adb_btn_submit);
        lvDevice = (ListView) findViewById(R.id.adb_lv_device);
//        DeviceAdapter adpater = new DeviceAdapter();
        handler = new Handler(this);
        btnSubmit.setOnClickListener(this);
        tvUserId.setText("你的小家ID是：" + CxGlobalParams.getInstance().getUserId() + "\n你的家庭ID是：" + CxGlobalParams.getInstance().getPairId());
        getDevice();
    }


    public boolean validate() {
        return !TextUtils.isEmpty(CxGlobalParams.getInstance().getUserId())
                && !TextUtils.isEmpty(CxGlobalParams.getInstance().getPairId())
                && !TextUtils.isEmpty(etDevId.getText().toString())
                && !TextUtils.isEmpty(etNickname.getText().toString());
    }

    public void getDevice() {
        GetDeviceInfo getDeviceInfo = new GetDeviceInfo();
        getDeviceInfo.setFamilyId(CxGlobalParams.getInstance().getPairId());
        PIApi.getInstance(this).getDevice(getDeviceInfo, new NetApi.SendListener() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                Message msg = handler.obtainMessage(MSG_GET_DEVICE_SUCCESS);
                msg.obj = jsonObject;
                handler.sendMessage(msg);
            }

            @Override
            public void onFailed(ErrorInfo errorInfo) {

            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.adb_btn_submit:
                if (validate()) {
                    btnSubmit.setText("正在请求...");
                    btnSubmit.setEnabled(false);
                    bindInfo = new BindInfo();
                    bindInfo.setBindingUid(CxGlobalParams.getInstance().getUserId());
                    SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getDateTimeInstance();
                    simpleDateFormat.applyPattern("yyyy-MM-dd HH:mm:ss");
                    bindInfo.setBindingTime(simpleDateFormat.format(Calendar.getInstance().getTime()));
                    bindInfo.setFamilyId(CxGlobalParams.getInstance().getPairId());
                    bindInfo.setDeviceId(etDevId.getText().toString());
                    bindInfo.setNickname(etNickname.getText().toString());
                    PIApi.getInstance(this).bindDevice(bindInfo, new NetApi.SendListener() {
                        @Override
                        public void onSuccess(JSONObject jsonObject) {
                            handler.sendEmptyMessage(MSG_BIND_SUCCESS);
                        }

                        @Override
                        public void onFailed(ErrorInfo errorInfo) {
                            handler.sendEmptyMessage(MSG_BIND_FAILED);
                        }
                    });
                } else {
                    ToastUtil.getSimpleToast(this, -1, "绑定参数不完整", Toast.LENGTH_SHORT).show();

                }
                break;
        }
    }

//    @Override
//    public void onSuccess(JSONObject jsonObject) {
//        ToastUtil.getSimpleToast(this, -1, "绑定成功", Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onFailed(ErrorInfo errorInfo) {
//        ToastUtil.getSimpleToast(this, -1, "绑定失败", Toast.LENGTH_SHORT).show();
//
//    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_GET_DEVICE_SUCCESS:
                ZedLog.SysoutAnyTime("MSG_GET_DEVICE_SUCCESS");
                deviceAdapter = new DeviceAdapter(new DeviceParseUtils((JSONObject) msg.obj).getAllBindInfoes(), this);
                lvDevice.setAdapter(deviceAdapter);
                lvDevice.setOnItemClickListener(deviceAdapter);
                break;
            case MSG_BIND_SUCCESS:
                ZedLog.SysoutAnyTime("MSG_BIND_SUCCESS");
                btnSubmit.setText("再次绑定");
                btnSubmit.setEnabled(true);
                getDevice();
                ToastUtil.getSimpleToast(BindDeviceAcitivty.this, -1, "绑定成功", Toast.LENGTH_SHORT).show();
                break;
            case MSG_BIND_FAILED:
                ZedLog.SysoutAnyTime("MSG_BIND_FAILED");
                btnSubmit.setText("重试");
                btnSubmit.setEnabled(true);
                ToastUtil.getSimpleToast(BindDeviceAcitivty.this, -1, "绑定失败", Toast.LENGTH_SHORT).show();
                break;
            case MSG_UNBIND_SUCCESS:
                ZedLog.SysoutAnyTime("MSG_UNBIND_SUCCESS");
                deviceAdapter.notifyDataSetChanged();
                lvDevice.invalidate();
                ToastUtil.getSimpleToast(BindDeviceAcitivty.this, -1, "解绑成功", Toast.LENGTH_SHORT).show();
                break;
            case MSG_UNBIND_FAILED:
                ZedLog.SysoutAnyTime("MSG_UNBIND_FAILED");
                ToastUtil.getSimpleToast(BindDeviceAcitivty.this, -1, "解绑失败", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    public class DeviceAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {

        private BindInfo[] bindInfos;
        private LayoutInflater layoutInflater;

        public DeviceAdapter(BindInfo[] bindInfos, Context context) {
            this.bindInfos = bindInfos;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return bindInfos.length;
        }

        @Override
        public BindInfo getItem(int position) {
            return bindInfos[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            PIApi.getInstance(BindDeviceAcitivty.this).unBindDevice(getItem(position).beUnbindInfo(), new NetApi.SendListener() {
                @Override
                public void onSuccess(JSONObject jsonObject) {
                    handler.sendEmptyMessage(MSG_UNBIND_SUCCESS);
                }

                @Override
                public void onFailed(ErrorInfo errorInfo) {
                    handler.sendEmptyMessage(MSG_UNBIND_FAILED);
                }
            });
        }

        class ViewHolder {
            TextView tv;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
                viewHolder.tv = (TextView) convertView.findViewById(android.R.id.text1);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.tv.setText("No." + (position + 1) + "\t设备ID:" + getItem(position).getDeviceId() + "\t设备名:" + getItem(position).getNickname());
            return convertView;
        }
    }
}

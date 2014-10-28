package com.chuxin.family.photo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chuxin.family.app.CxRootActivity;
import com.chuxin.family.gallery.CxGalleryActivity;
import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.utils.ToastUtil;
import com.hmammon.photointerface.DeviceParseUtils;
import com.hmammon.photointerface.NetApi;
import com.hmammon.photointerface.PIApi;
import com.hmammon.photointerface.ZedLog;
import com.hmammon.photointerface.domain.BindInfo;
import com.hmammon.photointerface.domain.ErrorInfo;
import com.hmammon.photointerface.domain.GetDeviceInfo;
import com.hmammon.photointerface.domain.UploadInfo;
import com.hmammon.photointerface.zip.ZipBuilder;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.zip.ZipFile;


/**
 * 选择设备界面
 * Created by Xcfh on 2014/10/24.
 */
public class ChooeseDeviceActivity extends CxRootActivity implements Handler.Callback {

    private ListView listView;
    private Handler handler;
    private BindInfo bindInfo;


    private static final int MSG_GET_DEVICE_SUCCESS = 100;

    private static final int MSG_UPLOAD_SUCCESS = 200;

    private static final int MSG_UPLOAD_FAILED = 201;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler(this);
        listView = new ListView(this);
        listView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        listView.setBackgroundColor(Color.BLUE);
        setContentView(listView);
        getDevice();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            ArrayList<String> filesPath = data.getStringArrayListExtra("data");
            if (filesPath.size() > 0) {
                upload(filesPath);
            }
        }
    }

    public void upload(ArrayList<String> paths) {
        File[] files = new File[paths.size()];
        for (int i = 0; i < files.length; i++) {
            files[i] = new File(paths.get(i).replace("file://", ""));
            ZedLog.SysoutAnyTime("path = " + files[i].getPath());
        }
        File zipFile = ZipBuilder.create(this).add(files).zip();
        UploadInfo uploadInfo = new UploadInfo();
        uploadInfo.setUploadFile(zipFile);
        uploadInfo.setFamilyId(bindInfo.getFamilyId());
        uploadInfo.setDeviceId(bindInfo.getDeviceId());
        uploadInfo.setSharedId(bindInfo.getBindingUid());
        PIApi.getInstance(this).uploadPhoto(uploadInfo, new NetApi.SendListener() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                handler.sendEmptyMessage(MSG_UPLOAD_SUCCESS);
            }

            @Override
            public void onFailed(ErrorInfo errorInfo) {
                handler.sendEmptyMessage(MSG_UPLOAD_FAILED);

            }
        });
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_GET_DEVICE_SUCCESS:
                DeviceAdapter deviceAdapter = new DeviceAdapter(new DeviceParseUtils((JSONObject) msg.obj).getAllBindInfoes(), this);
                listView.setAdapter(deviceAdapter);
                listView.setOnItemClickListener(deviceAdapter);
                break;
            case MSG_UPLOAD_SUCCESS:
                ToastUtil.getSimpleToast(this, -1, "上传成功", Toast.LENGTH_LONG).show();
                break;
            case MSG_UPLOAD_FAILED:
                ToastUtil.getSimpleToast(this, -1, "上传失败", Toast.LENGTH_LONG).show();
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
            bindInfo = getItem(position);
            Intent intent = new Intent(ChooeseDeviceActivity.this, CxGalleryActivity.class);
            startActivityForResult(intent, 1);
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

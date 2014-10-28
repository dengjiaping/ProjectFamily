/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package com.chuxin.family.widgets;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OneKeyShareCallback;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.chuxin.family.app.CxRootActivity;
import com.chuxin.family.global.CxGlobalConst;
import com.chuxin.family.neighbour.CxNeighbourParam;
import com.chuxin.family.net.CxNeighbourApi;
import com.chuxin.family.net.CxZoneApi;
import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.parse.CxNeighbourParser;
import com.chuxin.family.parse.been.CxNeighbourSendInvitation;
import com.chuxin.family.parse.been.CxZoneSendFeed;
import com.chuxin.family.utils.DialogUtil;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.utils.ToastUtil;
import com.chuxin.family.zone.CxZoneParam;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.chuxin.family.R;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * 添加图片时候，点击查看大图片
 * 
 * @author shichao.wang 增加图片的缩放查看
 */
public class CxImagePager extends CxRootActivity {

//    @Override
//    public void overridePendingTransition(int enterAnim, int exitAnim) {
//        super.overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
//    }

    private static final String STATE_POSITION = "STATE_POSITION";

    // public static final String STATE_CHAT = "chat"; // 聊天
    // public static final String STATE_ZONE_MYSELF = "zone_myself"; // 相册我自己
    // public static final String STATE_ZONE_PARTNER = "zone_partner"; // 相册另一半
    // public static final String STATE_ZONE_NEIGHBOR = "neighbor";// 密邻圈
    // public static final String STATE_ZONE_NEIGHBOR_MYSLEF =
    // "neighbor_myself"; // 密邻帖我自己
    // public static final String STATE_ZONE_NEIGHBOR_PARTNER =
    // "neighbor_partner"; // 密邻贴另一半
    public static final int STATE_CHAT = 1; // 聊天

    public static final int STATE_ZONE_MYSELF = 2; // 相册我自己

    public static final int STATE_ZONE_PARTNER = 3; // 相册另一半

    public static final int STATE_ZONE_NEIGHBOR = 4;// 密邻圈

    public static final int STATE_ZONE_NEIGHBOR_MYSLEF = 5; // 密邻帖我自己

    public static final int STATE_ZONE_NEIGHBOR_PARTNER = 6; // 密邻贴另一半
    
    public static final int STATE_KID = 7; // 孩子空间

    DisplayImageOptions options;

    ViewPager pager;

    private TextView mTitle;

    private Button mDisposeBtn;

    private Button mSaveBtn;

    private int mPosition = -1; // 选中的序数

    private ImagePagerAdapter mImagePagerAdapter;

    private List<String> mImagePath;

    private Dialog mSharedlg;

    private String mCurrentImageUrl;

    private List<String> mImageLoacalPath;

    private int mState;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cx_fa_activity_image_detail);
        mImageLoacalPath = new ArrayList<String>();
        try {
            mPosition = this.getIntent().getIntExtra(CxGlobalConst.S_ZONE_SELECTED_ORDER, -1);
            mImagePath = this.getIntent().getStringArrayListExtra("imagespath");
            mState = this.getIntent().getIntExtra(CxGlobalConst.S_STATE, -1);
        } catch (Exception e) {
            CxLog.e("", "" + e.getMessage());
        }

        mTitle = (TextView)findViewById(R.id.cx_fa_activity_title_info);
        mDisposeBtn = (Button)findViewById(R.id.cx_fa_activity_title_back);
        mDisposeBtn.setText(getString(R.string.cx_fa_navi_back));

        mSaveBtn = (Button)findViewById(R.id.cx_fa_activity_title_more);
        mSaveBtn.setText(getString(R.string.cx_fa_navi_ellipsis));
        mSaveBtn.setVisibility(View.VISIBLE);
        mSaveBtn.setOnClickListener(mBtnListener);
        mDisposeBtn.setOnClickListener(mBtnListener);

        pager = (ViewPager)findViewById(R.id.zone_image_viewpager);

        if (-1 == mPosition) {
            mPosition = 0;
        }

        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.chatview_imageloading)
                .showImageOnFail(R.drawable.chatview_imageloading).resetViewBeforeLoading(true)
                .cacheOnDisc(true).imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565).displayer(new FadeInBitmapDisplayer(300))
                .build();
        mImagePagerAdapter = new ImagePagerAdapter();
        pager.setAdapter(mImagePagerAdapter);
        pager.setCurrentItem(mPosition);

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                mPosition = arg0;
                mTitle.setText((mPosition + 1) + "/" + mImagePath.size());
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

        if ((null != mImagePath) && (mPosition < mImagePath.size())) {
            mTitle.setText((mPosition + 1) + "/" + mImagePath.size());
            pager.setCurrentItem(mPosition);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_POSITION, pager.getCurrentItem());
    }

    private class ImagePagerAdapter extends PagerAdapter {

        private LayoutInflater inflater;

        private List<String> mImagePathArrString;

        ImagePagerAdapter() {
            inflater = getLayoutInflater();
            mImagePathArrString = mImagePath;
        }

        public void updateSelf(int position) {
            mImagePathArrString = mImagePath;
            ImagePagerAdapter.this.notifyDataSetChanged();
            CxLog.i("^^^^", "adapter update self====" + position);
            if ((mPosition < mImagePathArrString.size()) && (mPosition >= 0)) {
                pager.setCurrentItem(mPosition);
            } else {
                if (((mPosition - 1) < mImagePathArrString.size()) && ((mPosition - 1) >= 0)) {
                    pager.setCurrentItem(mPosition - 1);
                }
            }

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager)container).removeView((View)object);
        }

        @Override
        public void finishUpdate(View container) {
        }

        @Override
        public int getCount() {
            if (null == mImagePathArrString) {
                return 0;
            }
            return mImagePathArrString.size();
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            View imageLayout = inflater.inflate(R.layout.item_pager_image, view, false);
            CxZoomImage imageView = (CxZoomImage)imageLayout.findViewById(R.id.image);
            final ProgressBar spinner = (ProgressBar)imageLayout.findViewById(R.id.loading);
            CxLog.i("RkImagePager", mImagePathArrString.get(position));
            if (null == (mImagePathArrString.get(position))) { //add by niechao 2014.1.6
				return null;
			}
            if (!mImagePathArrString.get(position).startsWith("http:")) {
                imageLoader.displayImage(
                        "file://" + mImagePathArrString.get(position).replace("file://", ""),
                        imageView);
            } else {
                imageLoader.displayImage(mImagePathArrString.get(position), imageView, options,
                        new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingStarted(String imageUri, View view) {
                                spinner.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onLoadingFailed(String imageUri, View view,
                                    FailReason failReason) {
                                String message = null;
                                switch (failReason.getType()) {
                                    case IO_ERROR:
                                        message = "Input/Output error";
                                        break;
                                    case DECODING_ERROR:
                                        message = "Image can't be decoded";
                                        break;
                                    case NETWORK_DENIED:
                                        message = "Downloads are denied";
                                        break;
                                    case OUT_OF_MEMORY:
                                        message = "Out Of Memory error";
                                        break;
                                    case UNKNOWN:
                                        message = "Unknown error";
                                        break;
                                }
                                // Toast.makeText(RkImagePager.this, message,
                                // Toast.LENGTH_SHORT).show();

                                spinner.setVisibility(View.GONE);
                            }

                            @Override
                            public void onLoadingComplete(String imageUri, View view,
                                    Bitmap loadedImage) {
                                spinner.setVisibility(View.GONE);
                            }
                        });
            }
            ((ViewPager)view).addView(imageLayout, 0);
            return imageLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View container) {
        }
    }

    @Override
    protected void onPause() {
        overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
        super.onPause();
    }

    OnClickListener mBtnListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.cx_fa_activity_title_back:
                    CxImagePager.this.finish();
                    break;
                case R.id.cx_fa_activity_title_more: // 保存照片 or 删除图片
                    mSaveBtn.setClickable(true);
//                 saveToPhone();
                    switch (mState) {
                        case STATE_CHAT:
                        case STATE_KID:
                            showChatStateAlertDialog();
                            break;
                        
                        case STATE_ZONE_MYSELF:
                            showZoneMyselfStateAlertDialog();
                            break;
//                        case STATE_ZONE_PARTNER:
//                        case STATE_ZONE_NEIGHBOR_MYSLEF:
//                            showZonePartnerStateAlertDialog();
//                            break;
                        case STATE_ZONE_NEIGHBOR:
                            showNeighborStateAlertDialog();
                            break;
                        case STATE_ZONE_NEIGHBOR_PARTNER:
                            showNeighborPartnerStateAlertDialog();
                            break;
                    }
                    break;

                default:
                    break;
            }

        }
    };

    /**
     * 分享
     *  @param activity
     * @param imageUrl
     */
    private void share(Activity activity, String imageUrl) {
        ShareSDK.initSDK(activity);
        CxLog.i("RkImagePager", "imageUrl=" + imageUrl);
        String comment = "我从小家里分享了一个东西";
        /*
         * 说明：
         *        1、qzone必须的参数：text，title、titleurl、site、siteurl
         *        2、微信必须的参数：title、text
         * */
        final OnekeyShare oks = new OnekeyShare();
        
        // 分享的标题，若为空字符串，qzone则会分享成大图模式(在空间中只显示图，没有跳转链接，并且放大查看也是在zone中完成)
        String title = "来自小家APP";				
//        if(comment==null || comment.equals("")){
//        	title = "";			// 若没有要分享的文字，就将本次分享设为纯图模式
//        }
        
        // 传给组件的URL只能是本地地址
        if(!TextUtils.isEmpty(imageUrl) && imageUrl.startsWith("http://")){
        	imageUrl =imageLoader.getFilePath( imageUrl );
        }
        
        oks.setNotification(R.drawable.cx_fa_app_icon, activity.getString(R.string.cx_fa_role_app_name));
        oks.setTitle(title);
        oks.setTitleUrl("http://m.family.rekoo.net/dl/family");
        oks.setText(comment);
        //oks.setImageUrl(imageUrl);
        oks.setImagePath(imageUrl);
        oks.setSilent(true);
        oks.setComment(comment);
        oks.setSite("小家APP");
        oks.setSiteUrl("http://m.family.rekoo.net/dl/family");

        // 去除注释，可令编辑页面显示为Dialog模式
        // oks.setDialogMode();

        // 去除注释，则快捷分享的操作结果将通过OneKeyShareCallback回调
        oks.setCallback(new OneKeyShareCallback(activity));

        // 对特殊平台定制需要发送的内容
        // oks.setShareContentCustomizeCallback(new
        // ShareContentCustomizeDemo());

        oks.show(activity);
    }

    private void dumpMessage(String text, List<String> photo) {
        try {
            CxZoneApi.getInstance().requestSendFeed(text, photo, 0, 0, sendCallback); // 此处转的都不是公开的帖子
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 同步相册回调
    JSONCaller sendCallback = new JSONCaller() {

        @Override
        public int call(Object result) {
            if (null == result) {
                // TODO分享失败
                return -1;
            }
            CxZoneSendFeed sendResult = null;
            try {
                sendResult = (CxZoneSendFeed)result;
            } catch (Exception e) {
            }
            if (null == sendResult) {
                // 分享失败
                new Handler(getMainLooper()) {
                    public void handleMessage(Message msg) {
                        ToastUtil.getSimpleToast(CxImagePager.this, -1,
                                getString(R.string.cx_fa_chat_synchronize_fail_text), 0).show();
                    };
                }.sendEmptyMessage(1);
                return -2;
            }
            int rc = sendResult.getRc();
            if (0 != rc) {
                // 提示服务器返回的原因
                return rc;
            }
            // 把数据告知二人空间界面
            // FeedListData feedData = sendResult.getData(); //modify by niechao
            // 2013.7.13
            // RkZoneParam.getInstance().insertFeedData(feedData);
            if (null != sendResult.getData()) {
                CxZoneParam.getInstance().setFeedsData(sendResult.getData());
            }
            CxImagePager.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    // Auto-generated method stub
                    new Handler(getMainLooper()) {
                        public void handleMessage(Message msg) {
                            ToastUtil.getSimpleToast(CxImagePager.this, -1,
                                    getString(R.string.cx_fa_chat_synchronize_success_text), 0)
                                    .show();
                        };
                    }.sendEmptyMessage(1);
                }
            });

            return 0;
        }
    };

    // 同步密邻回调
    JSONCaller sendNeighborCallback = new JSONCaller() {

        @Override
        public int call(Object result) {
            // RkLoadingUtil.getInstance().dismissLoading();

            DialogUtil.getInstance().setLoadingDialogDismiss(null, -1, 2000);
            if (null == result) {
                // 分享失败
                new Handler(getMainLooper()) {
                    public void handleMessage(Message msg) {
                        ToastUtil.getSimpleToast(CxImagePager.this, -1,
                                getString(R.string.cx_fa_chat_synchronize_fail_text), 0).show();
                    };
                }.sendEmptyMessage(1);
                return -1;
            }

            JSONObject jObj = null;
            try {
                jObj = (JSONObject)result;
            } catch (Exception e) {
            }

            if (jObj == null) {
                return -3;
            }

            CxNeighbourParser sendParser = new CxNeighbourParser();
            CxNeighbourSendInvitation sendResult = null;
            try {
                sendResult = sendParser.getSendInvitationResult(jObj);
            } catch (Exception e) {
            }
            if (null == sendResult) {
                // TODO分享失败
                new Handler(getMainLooper()) {
                    public void handleMessage(Message msg) {
                        ToastUtil.getSimpleToast(CxImagePager.this, -1,
                                getString(R.string.cx_fa_chat_synchronize_fail_text), 0).show();
                    };
                }.sendEmptyMessage(1);
                return -2;
            }
            int rc = sendResult.getRc();
            if (0 != rc) {
                // 提示服务器返回的原因
                final String tipWord = sendResult.getMsg();
                new Handler(getMainLooper()) {
                    public void handleMessage(Message msg) {
                        ToastUtil.getSimpleToast(CxImagePager.this, -1,
                                getString(R.string.cx_fa_chat_synchronize_fail_text), 0).show();
                    };
                }.sendEmptyMessage(1);
                // Toast.makeText(RkZoneAddFeed.this, sendResult.getMsg(),
                // Toast.LENGTH_SHORT).show();
                return rc;
            }
            // 清楚图片和文字数据

            // 把数据告知密邻界面
            if (null != sendResult.getData()) {
                CxNeighbourParam.getInstance().setInvitationData(jObj.toString());
            }
            new Handler(getMainLooper()) {
                public void handleMessage(Message msg) {
                    ToastUtil.getSimpleToast(CxImagePager.this, -1,
                            getString(R.string.cx_fa_chat_synchronize_success_text), 0).show();
                };
            }.sendEmptyMessage(1);
            return 0;
        }
    };

    /**
     * 保存图片到手机
     */
    public void saveToPhone() {
        String saveImageUrl = null;
//        DialogUtil.getInstance().setLoadingDialogDismiss(null, -1, 2000);
        String img = getPicFilePath(mImagePath.get(mPosition));
        saveImageUrl = savePicture(img);
        if (null == saveImageUrl) {
            ToastUtil.getSimpleToast(CxImagePager.this, -1,
                    getString(R.string.cx_fa_save_fail_text), 0).show();

        } else {
            ToastUtil.getSimpleToast(CxImagePager.this, -1,
                    getString(R.string.cx_fa_save_success_text), 0).show();
        }
    }

    private String savePicture(String imagePath) {
        // Toast.makeText(this, "imagePath=" + imagePath, Toast.LENGTH_LONG)
        // .show();
        if (null == imagePath) {
            return null;
        }
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return null;
        }
        ContentResolver cr = getContentResolver();
        long dateTaken = System.currentTimeMillis();
        String name = createName(dateTaken) + ".jpg";
        try {
            String uriStr = MediaStore.Images.Media.insertImage(cr,
                    imagePath.replace("file://", ""), name, null);
            if (null != uriStr) {
                fileStr = getFilePathByUri(uriStr);
                msc.connect();
            }

            return uriStr;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    String fileStr = null;

    MediaScannerConnection msc = new MediaScannerConnection(CxImagePager.this,
            new MediaScannerConnection.MediaScannerConnectionClient() {

                @Override
                public void onScanCompleted(String path, Uri uri) {
                    msc.disconnect();
                }

                @Override
                public void onMediaScannerConnected() {
                    msc.scanFile(fileStr, "image/jpeg");
                }
            });

    private static String createName(long dateTaken) {
        return DateFormat.format("yyyy-MM-dd_kk.mm.ss", dateTaken).toString();
    }

    private String getFilePathByUri(String uri) {
        if (null == uri) {
            return null;
        }
        Uri path = Uri.parse(uri);
        Cursor c = getContentResolver().query(path, null, null, null, null);
        String filePath = null;
        if (null == c) {
            return null;
        }
        try {
            if ((c.getCount() != 1) || !c.moveToFirst()) {
                // nothing to do
            } else {
                filePath = c.getString(c.getColumnIndexOrThrow(MediaColumns.DATA));
            }
        } finally {
            c.close();
        }
        return filePath;
    }

    /**
     * 聊天模块中大图显示的功能键
     */
    public void showChatStateAlertDialog() {
        String[] items = new String[] {
                getString(R.string.cx_fa_imagepager_shareto_social),
                getString(R.string.cx_fa_imagepager_shareto_album),
                getString(R.string.cx_fa_imagepager_shareto_neighbor),
                getString(R.string.cx_fa_imagepager_save_to_phone),
                getString(R.string.cx_fa_imagepager_cancle)
        };
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CxImagePager.this);
        alertDialog.setTitle("");
        alertDialog.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            	String img = getPicFilePath( mImagePath.get(mPosition));
                dialog.dismiss();
                switch (which) {
                    case 0:// 分享到社交网络
                           // mSharedlg =
                           // DialogUtil.getInstance().getLoadingDialog(RkImagePager.this);
                           // mSharedlg.show();
                        share(CxImagePager.this, img);
                        break;
                    case 1:// 同步到时光相册
                        CxLog.i("RkImagePager", "mImagePath=" + mImagePath);
                        List<String> photos = new ArrayList<String>();
                        
                        photos.add(img);
                        dumpMessage("", photos);
                        break;
                    case 2:// 同步到密邻圈
                        List<String> images = new ArrayList<String>();
                        
                        images.add(img);
                        try {
                            DialogUtil.getInstance().getLoadingDialogShow(CxImagePager.this, -1);
                            CxNeighbourApi.getInstance().requestSendInvitation("", images, "post",
                                    null,0, 0, sendNeighborCallback);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 3:// 保存到手机
                        saveToPhone();
                        break;
                    case 4:// 取消
                        dialog.dismiss();
                        break;
                    default:
                        break;
                }
            }
        });
        alertDialog.show();
    }

    /**
     * 相册模块自己发帖中大图显示的功能键
     */
    public void showZoneMyselfStateAlertDialog() {
        String[] items = new String[] {
                getString(R.string.cx_fa_imagepager_shareto_social),
                getString(R.string.cx_fa_imagepager_shareto_neighbor),
                getString(R.string.cx_fa_imagepager_save_to_phone),
                getString(R.string.cx_fa_imagepager_cancle)
        };
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CxImagePager.this);
        alertDialog.setTitle("");
        alertDialog.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            	 // 得到图片的本地地址
                String img =getPicFilePath( mImagePath.get(mPosition) );
                dialog.dismiss();
                switch (which) {
                    case 0:// 分享到社交网络
                           // mSharedlg =
                           // DialogUtil.getInstance().getLoadingDialog(RkImagePager.this);
                           // mSharedlg.show();
                        share(CxImagePager.this, img);
                        break;
                    case 1:// 同步到密邻圈
                        List<String> images = new ArrayList<String>();
                       
                        images.add(img);
                        try {
                            DialogUtil.getInstance().getLoadingDialogShow(CxImagePager.this, -1);
                            CxNeighbourApi.getInstance().requestSendInvitation("", images, "post",
                                    null,0, 0, sendNeighborCallback);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 2:// 保存到手机
                        saveToPhone();
                        break;
                    case 3:// 取消
                        dialog.dismiss();
                        break;
                    default:
                        break;
                }
            }
        });
        alertDialog.show();
    }

    
    /**
     * 相册模块另一半发帖中大图显示的功能键 和 密邻圈我自己的发帖
     */
//    public void showZonePartnerStateAlertDialog() {
//        String[] items = new String[] {
//                getString(R.string.cx_fa_imagepager_shareto_social),
//                getString(R.string.cx_fa_imagepager_save_to_phone),
//                getString(R.string.cx_fa_imagepager_cancle)
//        };
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(RkImagePager.this);
//        alertDialog.setTitle("");
//        alertDialog.setItems(items, new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//            	String img = getPicFilePath(mImagePath.get(mPosition)); 
//                dialog.dismiss();
//                switch (which) {
//                    case 0:// 分享到社交网络
//                           // mSharedlg =
//                           // DialogUtil.getInstance().getLoadingDialog(RkImagePager.this);
//                           // mSharedlg.show();
//                        share(RkImagePager.this, "", img);
//                        break;
//                    case 1:// 保存到手机
//                        saveToPhone();
//                        break;
//                    case 2:// 取消
//                        dialog.dismiss();
//                        break;
//                    default:
//                        break;
//                }
//            }
//        });
//        alertDialog.show();
//    }

    /**
     * 密邻圈模块另一半发帖中大图显示的功能键
     */
    public void showNeighborPartnerStateAlertDialog() {
        String[] items = new String[] {
                getString(R.string.cx_fa_imagepager_save_to_phone),
                getString(R.string.cx_fa_imagepager_shareto_album),
                getString(R.string.cx_fa_imagepager_cancle)
        };
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CxImagePager.this);
        alertDialog.setTitle("");
        alertDialog.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            	String img = getPicFilePath(mImagePath.get(mPosition)); 
                dialog.dismiss();
                switch (which) {
                    case 0:// 保存到手机
                        saveToPhone();
                        break;
                    case 1:// 同步到时光相册
                        CxLog.i("RkImagePager", "mImagePath=" + mImagePath);
                        List<String> photos = new ArrayList<String>();
                        
                        photos.add(img);
                        dumpMessage("", photos);
                        break;
                    case 2:// 取消
                        dialog.dismiss();
                        break;
                    default:
                        break;
                }
            }
        });
        alertDialog.show();
    }

    /**
     * 密邻圈模块密邻发帖中大图显示的功能键
     */
    public void showNeighborStateAlertDialog() {
        String[] items = new String[] {
                getString(R.string.cx_fa_imagepager_shareto_social),
                getString(R.string.cx_fa_imagepager_shareto_album),
                getString(R.string.cx_fa_imagepager_save_to_phone),
                getString(R.string.cx_fa_imagepager_cancle)
        };
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CxImagePager.this);
        alertDialog.setTitle("");
        alertDialog.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            	String img = getPicFilePath(mImagePath.get(mPosition)); 
                dialog.dismiss();
                switch (which) {
                    case 0:// 分享到社交网络
                           // mSharedlg =
                           // DialogUtil.getInstance().getLoadingDialog(RkImagePager.this);
                           // mSharedlg.show();
                        share(CxImagePager.this, img);
                        break;
                    case 1:// 同步到时光相册
                        CxLog.i("RkImagePager", "mImagePath=" + mImagePath);
                        List<String> photos = new ArrayList<String>();
                        photos.add(img);
                        dumpMessage("", photos);
                        break;
                    case 2:// 保存到手机
                        saveToPhone();
                        break;
                    case 3:// 取消
                        dialog.dismiss();
                        break;
                    default:
                        break;
                }
            }
        });
        alertDialog.show();
    }
    
    
    /**
     * 得到图片的本地文件地址
     * @param picUrl ： 图片的原有地址，可能是file://开头， 也可能是http://开头
     * @return
     */
    private String getPicFilePath(String picUrl){
    	 if(picUrl.startsWith("http://")){
    		 picUrl = imageLoader.getFilePath(picUrl);
         }else{
        	 picUrl = picUrl.replace("file://", "");
         }
    	 return picUrl;
    }
}

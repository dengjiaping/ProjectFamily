
package com.chuxin.family.more;

import com.chuxin.family.R;
import com.chuxin.family.calendar.CxCalendarParam;
import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.main.CxAuthenNew;
import com.chuxin.family.main.CxMain;
import com.chuxin.family.neighbour.CxNeighbourFragment;
import com.chuxin.family.neighbour.answer.CxAnswerActivity;
import com.chuxin.family.photo.BindDeviceAcitivty;
import com.chuxin.family.photo.ChooeseDeviceActivity;
import com.chuxin.family.settings.CxSettingActivity;
import com.chuxin.family.settings.CxUserSuggest;
import com.chuxin.family.tabloid.CxTabloidActivity;
import com.chuxin.family.utils.DialogUtil;
import com.chuxin.family.utils.ScreenUtil;
import com.chuxin.family.utils.ToastUtil;
import com.chuxin.family.views.reminder.CxReminderList;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * @author shichao.wang
 */
public class CxMoreFragment extends Fragment implements IWXAPIEventHandler {

    private static ImageButton mMenuBtn;

    public static final int UPDATE_HOME_MENU = 5; // 更新home按钮未读消息状态

    public static Handler mRkMoreHandler = null;

    private static final int WXSceneSession = 0;

    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View tempView = inflater.inflate(R.layout.cx_fa_fragment_more, null);

        mMenuBtn = (ImageButton) tempView.findViewById(R.id.cx_fa_more_menu);
        mMenuBtn.setOnClickListener(itemClickListener);
        updateHomeMenu();
        LinearLayout reminderBtn = (LinearLayout) tempView.findViewById(R.id.cx_fa_more_btn_reminder); // 提醒/纪念日
        LinearLayout tabloidBtn = (LinearLayout) tempView.findViewById(R.id.cx_fa_more_btn_daily); // 我家小报
        LinearLayout feedbackBtn = (LinearLayout) tempView.findViewById(R.id.cx_fa_more_btn_feedback); // 意见反馈
        LinearLayout setBtn = (LinearLayout) tempView.findViewById(R.id.cx_fa_more_btn_set); // 设置
        LinearLayout inviteBtn = (LinearLayout) tempView.findViewById(R.id.cx_fa_more_btn_inviteFriend); // 邀请亲友
        LinearLayout smartBtn = (LinearLayout) tempView.findViewById(R.id.cx_fa_more_btn_other); // 谁家最聪明
        LinearLayout bindDevice = (LinearLayout) tempView.findViewById(R.id.more_bind_device);// 绑定设备
        LinearLayout chooeseDevice = (LinearLayout) tempView.findViewById(R.id.more_chooese_device);// 绑定设备


        LinearLayout cx_fa_more_row1 = (LinearLayout) tempView.findViewById(R.id.cx_fa_more_row1); // 第一行


//        ImageView smartImg = (ImageView)tempView.findViewById(R.id.cx_fa_more_answer_img);
//        ImageView memorialImg = (ImageView)tempView.findViewById(R.id.cx_fa_more_answer_img);
//        ImageView tabloidImg = (ImageView)tempView.findViewById(R.id.cx_fa_more_answer_img);
//        ImageView feedbackImg = (ImageView)tempView.findViewById(R.id.cx_fa_more_answer_img);
//        ImageView inviteImg = (ImageView)tempView.findViewById(R.id.cx_fa_more_answer_img);
//        ImageView setImg = (ImageView)tempView.findViewById(R.id.cx_fa_more_answer_img);

        int widthPixels = getResources().getDisplayMetrics().widthPixels;
        int row_w = widthPixels - ScreenUtil.dip2px(getActivity(), 2); // 三个图像减去父对象margin及padding后，可用的宽度
        int w = (Integer) (row_w / 3); // 自动算图的宽度(有一个风险，如果屏幕的宽大于高，这个就会特别难看)
        int h = w;


        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(w, h - 40);
        reminderBtn.setLayoutParams(lp);
        smartBtn.setLayoutParams(lp);
        tabloidBtn.setLayoutParams(lp);
        feedbackBtn.setLayoutParams(lp);
        inviteBtn.setLayoutParams(lp);
        setBtn.setLayoutParams(lp);

        // 如果未结对，则将"我家小报"隐藏
        CxGlobalParams mGlobalParam = CxGlobalParams.getInstance();
        if (1 != mGlobalParam.getPair()) { // 未结对
            // tabloidBtn.setVisibility(View.GONE);
            // reminderBtn.setVisibility(View.GONE);
            // smartBtn.setVisibility(View.GONE);
            cx_fa_more_row1.setVisibility(View.GONE);
        } else {
            // tabloidBtn.setVisibility(View.VISIBLE);
            // reminderBtn.setVisibility(View.VISIBLE);
            // smartBtn.setVisibility(View.VISIBLE);
            cx_fa_more_row1.setVisibility(View.VISIBLE);
        }
        reminderBtn.setOnClickListener(itemClickListener);
        tabloidBtn.setOnClickListener(itemClickListener);
        feedbackBtn.setOnClickListener(itemClickListener);
        setBtn.setOnClickListener(itemClickListener);
        inviteBtn.setOnClickListener(itemClickListener);
        smartBtn.setOnClickListener(itemClickListener);
        bindDevice.setOnClickListener(itemClickListener);
        chooeseDevice.setOnClickListener(itemClickListener);

        ((CxMain) getActivity()).closeMenu();

        mRkMoreHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case UPDATE_HOME_MENU:
                        if (CxMoreFragment.this.isVisible()) {
                            updateHomeMenu();
                        }
                        break;
                }
            }

        };

        return tempView;
    }

    OnClickListener itemClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.cx_fa_more_btn_reminder:
//                    Intent reminderIntent = new Intent(getActivity(), RkReminderList.class);
//                    startActivity(reminderIntent);
                    CxCalendarParam.getInstance().setFragment_type(2);
                    ((CxMain) getActivity()).menuEvent(CxMain.CALENDAR);
                    getActivity().overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
                    break;
                case R.id.cx_fa_more_menu:
                    ((CxMain) getActivity()).toggleMenu();
                    break;
                case R.id.cx_fa_more_btn_daily:
                    Intent intent = new Intent(getActivity(), CxTabloidActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.tran_next_in,
                            R.anim.tran_next_out);
                    break;
                case R.id.cx_fa_more_btn_feedback:
                    Intent toSuggest = new Intent(getActivity(), CxUserSuggest.class);// shichao
                    startActivity(toSuggest);
                    getActivity().overridePendingTransition(R.anim.tran_next_in,
                            R.anim.tran_next_out);
                    break;
                case R.id.cx_fa_more_btn_set:
                    // ((RkMain)getActivity()).changeFragment(RkMain.SETTINGS);

                    Intent toSetting = new Intent(getActivity(), CxSettingActivity.class);// wentong.men
                    startActivity(toSetting);
                    getActivity().overridePendingTransition(R.anim.tran_next_in,
                            R.anim.tran_next_out);

                    break;
                case R.id.cx_fa_more_btn_inviteFriend:
                    // 邀请亲友

                    showInviteDialog();

                    // intent = new Intent(getActivity(),
                    // RkInviteNeighbour.class);
                    // startActivity(intent);
                    // getActivity().overridePendingTransition(R.anim.tran_next_in,
                    // R.anim.tran_next_out);
                    break;
                case R.id.cx_fa_more_btn_other:
//                  ToastUtil.getSimpleToast(getActivity(),-1,getString(R.string.cx_fa_more_click_text), 1).show();
                    Intent toAnswer = new Intent(getActivity(), CxAnswerActivity.class);//shichao
                    startActivity(toAnswer);
                    getActivity().overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
                    break;
                case R.id.more_bind_device:
                    startActivity(new Intent(getActivity(), BindDeviceAcitivty.class));
                    getActivity().overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
                    break;
                case R.id.more_chooese_device:
                    startActivity(new Intent(getActivity(), ChooeseDeviceActivity.class));
                    getActivity().overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
                    break;
                default:
                    break;
            }
        }
    };

    // 更新home按钮状态
    public static void updateHomeMenu() {
        if (CxGlobalParams.getInstance().getGroup() > 0
                || CxGlobalParams.getInstance().getSpaceTips() > 0
                || CxGlobalParams.getInstance().getKid_tips() > 0) {
            mMenuBtn.setBackgroundResource(R.drawable.navi_home_new_btn);
        } else {
            // mMainMenuBtn.setImageResource(R.drawable.cx_fa_menu_btn);
            mMenuBtn.setBackgroundResource(R.drawable.cx_fa_menu_btn);
        }
    }

    private void showInviteDialog() {

        View inflate = View.inflate(getActivity(), R.layout.cx_fa_widget_neighbour_invite_dialog,
                null);
        Button bySms = (Button) inflate.findViewById(R.id.nb_list_invite_dialog_by_sms);
        Button byWeixin = (Button) inflate.findViewById(R.id.nb_list_invite_dialog_by_weixin);
        Button byCode = (Button) inflate.findViewById(R.id.nb_list_invite_dialog_by_code);
        Button cancel = (Button) inflate.findViewById(R.id.nb_list_invite_dialog_cancel);

        bySms.setOnClickListener(inviteListener);
        byWeixin.setOnClickListener(inviteListener);
        byCode.setOnClickListener(inviteListener);
        cancel.setOnClickListener(inviteListener);

        inviteDialog = new Dialog(getActivity(), R.style.simple_dialog);
        inviteDialog.setContentView(inflate);
        inviteDialog.show();

    }

    OnClickListener inviteListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            inviteDialog.dismiss();
            String smsContentStr = String.format(getString(R.string.cx_fa_invite_neighbour_word), CxGlobalParams.getInstance().getGroup_show_id());
            switch (v.getId()) {
                case R.id.nb_list_invite_dialog_by_sms:
                    Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                    i.putExtra("sms_body", smsContentStr);
                    i.setType("vnd.android-dir/mms-sms");
                    startActivity(i);
                    break;
                case R.id.nb_list_invite_dialog_by_weixin:
                    if (CxAuthenNew.api.isWXAppInstalled() && CxAuthenNew.api.isWXAppSupportAPI()) {
                        WXTextObject textObj = new WXTextObject();
                        textObj.text = smsContentStr;

                        WXMediaMessage msg = new WXMediaMessage();

                        msg.mediaObject = textObj;
                        msg.description = smsContentStr;
                        SendMessageToWX.Req req = new SendMessageToWX.Req();
                        req.transaction = String.valueOf(System.currentTimeMillis());
                        req.message = msg;
                        req.scene = WXSceneSession;

                        CxAuthenNew.api.sendReq(req);
                    } else {
                        // Toast.makeText(RkNeighbourList.this, "请先安装微信",
                        // Toast.LENGTH_LONG).show();
                        ToastUtil.getSimpleToast(getActivity(), -1, "请先安装微信", 1).show();

                    }
                    break;
                case R.id.nb_list_invite_dialog_by_code:

                    DialogUtil.getInstance().getCodeDialogShow(getActivity(),
                            R.drawable.cx_fa_invite_qr_code,
                            getString(R.string.cx_fa_neighbour_invite_code_dialog_text),
                            getString(R.string.cx_fa_neighbour_invite_code_dialog_text2));

                    // Intent intent = new Intent();
                    // intent.setAction("android.intent.action.VIEW");
                    // Uri content_url =
                    // Uri.parse(getString(R.string.cx_fa_invite_neighbour_word_url));
                    // intent.setData(content_url);
                    // startActivity(intent);
                    break;
                case R.id.nb_list_invite_dialog_cancel:
                    break;

                default:
                    break;
            }

        }
    };

    private Dialog inviteDialog;

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        int result = 0;

        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = R.string.errcode_success;
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = R.string.errcode_cancel;
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = R.string.errcode_deny;
                break;
            default:
                result = R.string.errcode_unknown;
                break;
        }

        Toast.makeText(getActivity(), getResources().getString(result), Toast.LENGTH_LONG).show();
    }

}

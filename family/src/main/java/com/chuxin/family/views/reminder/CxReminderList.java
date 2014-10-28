package com.chuxin.family.views.reminder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chuxin.family.app.CxRootActivity;
import com.chuxin.family.global.CxGlobalConst;
import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.model.CxObserverInterface;
import com.chuxin.family.models.Model;
import com.chuxin.family.models.Reminder;
import com.chuxin.family.net.ReminderApi;
import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.resource.CxResourceString;
import com.chuxin.family.utils.DialogUtil;
import com.chuxin.family.utils.CxLoadingUtil;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.utils.CxResourceManager;
import com.chuxin.family.utils.DialogUtil.OnSureClickListener;
import com.chuxin.family.views.reminder.CxReminderList.ReminderAdapter.ReminderAdapterTag;
import com.chuxin.family.widgets.QuickActionBar;
import com.chuxin.family.widgets.CxImageView;
import com.chuxin.family.widgets.ScrollableListView;
import com.chuxin.family.widgets.ScrollableListView.OnRefreshListener;
import com.chuxin.family.R;

public class CxReminderList extends CxRootActivity {

	private static final String TAG = "RkReminderList";
	private ScrollableListView mReminderList;

	private ReminderAdapter mReminderAdapter = new ReminderAdapter();

	private ReminderDisplayUtility mDisplayUtility = null;

	private QuickActionBar mActionBar = null;

//	private View mReminderListAllView;

//	public static boolean mIsStart;
	public static Handler mReminderListAllHandler;

	public static boolean sReminderGetDataFinish = false;

	private SharedPreferences mReminderSharedPreferences = null;
	private int mLocalRts = 0;
	private boolean mIsLongPolling = false;
	
//	private RkGlobalParams mGlobalParam;
	private CurrentObserver updateListener;
	public static final int UPDATE_LOCAL_REMINDER_DATA = 0; // 更新本地提醒数据
	public static final int REMINDER_LONGPOLLING = 1; // longpolling拉取新提醒数据
	public static final int RELOAD_REMINDER_DATA = 2; // 重新加载提醒数据
	public static final int UPDATE_REMINDER_STATUS = 3; // 更新提醒状态值
	
	private ImageButton mAddNewReminder;
	private Button mMenuBtn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cx_fa_view_reminder_list_all_reminders);
		mReminderListAllHandler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case UPDATE_LOCAL_REMINDER_DATA: // 只更新本地数据
					// updateListReminderData();
					refresh(false);
					break;
				case REMINDER_LONGPOLLING: // longpolling更新全部数据
					mLocalRts = msg.arg1;
					mIsLongPolling = true;
					CxLog.v(TAG, "mLocalRts=" + mLocalRts);
					refresh(true);
					break;
				case RELOAD_REMINDER_DATA: // 从新加载
					reload();
					break;
				case UPDATE_REMINDER_STATUS:
					JSONArray remindersStatus = (JSONArray) msg.obj;
					CxLog.v(TAG, "remindersStatus = " + remindersStatus);
					updateReminderStatus(remindersStatus);
					break;
//				case 4:
//					refresh(true);
//					break;
				}
				super.handleMessage(msg);
			}

		};
		//add by niechao-----------update data when profile (partner and me) changed
		updateListener = new CurrentObserver();
		List<String> tags = new ArrayList<String>();
		tags.add(CxGlobalParams.PARTNER_ICON_BIG); //对方的大头像
		tags.add(CxGlobalParams.ICON_SMALL); //自己的头像
		tags.add(CxGlobalParams.PARTNER_NAME); //对方的昵称
		updateListener.setListenTag(tags); //设置观察目标
		updateListener.setMainThread(true);
		CxGlobalParams.getInstance().registerObserver(updateListener);
		
		new Reminder(null, CxReminderList.this);
		mDisplayUtility = new ReminderDisplayUtility(getResources());
		init();
	}

	@Override
	public void onStart() {
		super.onStart();
//		setStart(true);
	}

	@Override
	public void onStop() {
		super.onStop();
//		setStart(false);
	}

	@Override
	public void onResume() {
		super.onResume();
//		setStart(true);
	}
	
	@Override
	public void onDestroy() {
//		if (null != updateListener){
//			
//		}
//		CxResourceManager resourceManager = CxResourceManager.getInstance(
//				CxReminderList.this, "head", 
//				CxReminderList.this);
//		if (null != resourceManager) {
//			resourceManager.clearMemory();
//			resourceManager = null;
//		}
		CxLoadingUtil.getInstance().dismissLoading();
		super.onDestroy();
	}

	// drop one reminder record and refresh the list
	private void drop(final String reminderId, final int flag) {
	    CxLoadingUtil.getInstance().showLoading(CxReminderList.this, true);
		CxLog.d(TAG, "drop: reminder.id=" + reminderId);
		ReminderApi.getInstance().doDeleteReminder(reminderId,
			new JSONCaller() {

				@Override
				public int call(Object result) {
					ReminderController.getInstance().cancelAlarmReminder(
							CxReminderList.this, flag);
					new Reminder().drop(reminderId);
					loadAllLocalReminders();
					CxLoadingUtil.getInstance().dismissLoading();
//					Toast.makeText(
//							getActivity(),
//							"delete " + reminderId + " returns "
//									+ result.toString(), Toast.LENGTH_SHORT)
//							.show();
					return 0;
				}

			});
	}

	// set the visibility for the No-Reminder-Tip
	private void setVisibilityForNoReminderTip(boolean display) {

		RelativeLayout layout = (RelativeLayout)findViewById(R.id.cx_fa_view_reminder_list_all_reminders__tip_layout);
		if (display) {
			if (layout.getVisibility() == View.VISIBLE)
				return;

			TextView tip2 = (TextView)findViewById(R.id.cx_fa_view_reminder_list_all_reminders__tip2);
			if (tip2.getText().length() == 0) {
				String tip2Format = getResources().getString(
						R.string.cx_fa_nls_reminder_no_reminder_tip2);
				String name = getResources().getString(CxResourceString.getInstance().str_reminder_name);
				tip2.setText(String.format(tip2Format, name));
			}

			RelativeLayout addReminderBtn = (RelativeLayout)findViewById(R.id.cx_fa_view_reminder_list_all_reminders__create2);
			addReminderBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					ReminderController controller = ReminderController
							.getInstance();
					controller.reset();

					Intent intent = new Intent(CxReminderList.this,
							ReminderCreateActivity.class);
					startActivity(intent);
					CxReminderList.this.overridePendingTransition(
							R.anim.tran_next_in,
							R.anim.tran_next_out);
					CxReminderList.this.finish();
				}

			});

			layout.setVisibility(View.VISIBLE);
		} else {
			if (layout.getVisibility() == View.GONE)
				return;

			layout.setVisibility(View.GONE);
		}
	}

	// load all reminders from local storage
	public void loadAllLocalReminders() {
//	    RkLoadingUtil.getInstance().showLoading(RkReminderList.this, true);
		List<Model> orginalReminders = new Reminder(null, CxReminderList.this).gets(
				"1=1", new String[] {}, null, 0, 0);
		if (orginalReminders == null) {
		    CxLoadingUtil.getInstance().dismissLoading();
		    mReminderAdapter.setReminderLists(null);
			return;
		}
		
		List<Reminder> newReminders = new ArrayList<Reminder>();
		Iterator<Model> i = orginalReminders.iterator();
		while (i.hasNext()) {
			Reminder reminder = (Reminder) (i.next());
			CxLog.d(TAG, "loadAllLocalReminders: reminder.id=" + reminder.mId);

			if (reminder.mId != null) {
				if (reminder.adjust() == -1) {
					ReminderController.getInstance().cancelAlarmReminder(
							CxReminderList.this, reminder.getFlag());
					reminder.drop();
				} else {
					newReminders.add(reminder);
					if(!(reminder.getTarget() == 1 && reminder.getAuthor().equals(CxGlobalParams.getInstance()
			                .getUserId()))){
    					ReminderController.getInstance().setAlarmReminder(
    							CxReminderList.this, reminder.getRealTimestamp(),
    							reminder.getId(), reminder.getPeriodType(),
    							reminder.getFlag(), reminder.getTitle());
					}
				}
			}
		}
		Comparator<Reminder> cp = new Comparator<Reminder>() {
		    public int compare(Reminder r1, Reminder r2){
	            long time = mDisplayUtility.getSecond(r1) - mDisplayUtility.getSecond(r2);
		        return (int)time;
		    }
        };
        Collections.sort(newReminders,cp);
        CxLog.i(TAG, newReminders.toString());
//        RkLoadingUtil.getInstance().dismissLoading();
		mReminderAdapter.setReminderLists(newReminders);
	}

	
	// drop all reminders from local storage except delay reminders
	private void dropAllLocalRemindersExceptDelay() {
		List<Model> orginalReminders = new Reminder(null, CxReminderList.this).gets(
				"1=1", new String[] {}, null, 0, 0);
		if (orginalReminders == null) {
			mReminderAdapter.setReminderLists(null);
			return;
		}
		List<Reminder> newReminders = new ArrayList<Reminder>();
		Iterator<Model> i = orginalReminders.iterator();
		while (i.hasNext()) {
			Reminder reminder = (Reminder) (i.next());
			CxLog.d(TAG, "loadAllLocalReminders: reminder.id=" + reminder.mId);
			CxLog.d(TAG,
					"loadAllLocalReminders: reminder.isdelay="
							+ reminder.getIsDelay());

			if (reminder.mId != null && reminder.getIsDelay() == false) {
				ReminderController.getInstance().cancelAlarmReminder(
						CxReminderList.this, reminder.getFlag());
				reminder.drop();
			}
		}

		mReminderAdapter.setReminderLists(newReminders);
		// mReminderAdapter.notifyDataSetChanged();
	}

	// make network request, and download all reminders;
	private void reload() {
		Log.d(TAG, "reload wil be begin");
		
		// 如果是推出应用弹出界面，执行到这里，通过判断context为null不执行下面代码。
		if(null != CxReminderList.this){
			//先显示本地数据。在开启子线程请求网络数据，并更新数据库里的信息。
			loadAllLocalReminders();
			ReminderApi.getInstance().doListAllReminders(new JSONCaller() {
	
				@Override
				public int call(Object result) {
					updateListReminder(result);
//					int localRts = mReminderSharedPreferences.getInt(RkGlobalConst.S_REMINDER_RTS_KEY, 0);
					loadAllLocalReminders();
					return 0;
				}
	
			});
		}
	}

	// make one refresh with/without network request;
	private void refresh(boolean withNetwork) {
		
		Log.d(TAG, "refresh withNetwork:" + withNetwork);		
		
		if (withNetwork) {
			ReminderApi.getInstance().doListUpdateReminders(new JSONCaller() {
//			ReminderApi.getInstance().doListAllReminders(new JSONCaller() {

				@Override
				public int call(Object result) {
					if (null == result) {
						return -1;
					}
                    if(null == CxReminderList.this){
                        return 0;
                    }
					if (mIsLongPolling) {
    						mReminderSharedPreferences = CxReminderList.this
    								.getSharedPreferences(
    										CxGlobalConst.S_REMINDER_PREFS_NAME, 0);
    						mReminderSharedPreferences
    								.edit()
    								.putInt(CxGlobalConst.S_REMINDER_RTS_KEY,
    										mLocalRts).commit();
    						mIsLongPolling = false;
					}
					updateListReminder(result);
					loadAllLocalReminders();
					return 0;
				}

			});
		} else {
			new RefreshTask().execute();
		}
	}

	public void updateListReminder(Object result) {
		if (null == result) {
			return;
		}
		JSONArray reminders = (JSONArray) result;
		// HashMap<String, Integer> reminderStatus = new HashMap<String,
		// Integer>();
		JSONArray remindersStatus = new JSONArray();
		JSONObject reminderStatusObj = new JSONObject();
		for (int i = 0; i < reminders.length(); i++) {
			try {
				JSONObject reminderObj = reminders.getJSONObject(i).put(
						Reminder.TAG_DELAY, false);
				Reminder reminder = new Reminder(reminderObj, CxReminderList.this);
				if (!reminder.isNewToMe()) {
					reminderStatusObj.put("reminder_id",
							reminder.getReminderId());
					reminderStatusObj.put("ts", reminder.getUpdateTimestamp());
					remindersStatus.put(reminderStatusObj);
				}
				if (reminder.adjust() != -1) {
					reminder.setFlag(reminder.getBaseTimestamp());
					reminder.put();
					mReminderSharedPreferences =  CxReminderList.this
							.getSharedPreferences(
									CxGlobalConst.S_REMINDER_PREFS_NAME, 0);
					int firstRts = mReminderSharedPreferences.getInt(
							CxGlobalConst.S_REMINDER_FIRST_RTS_KEY, 0);
					if (firstRts == 0) {
						mReminderSharedPreferences
								.edit()
								.putInt(CxGlobalConst.S_REMINDER_RTS_KEY,
										reminder.getUpdateTimestamp()).commit();
					}
				} else {
				    if( null == CxReminderList.this){
				        return;
				    }
					CxLog.v(TAG, "falg 1 drop" + reminder.getBaseTimestamp());
					ReminderController.getInstance().cancelAlarmReminder(
							CxReminderList.this, reminder.getBaseTimestamp());
					reminder.drop();
				}
			} catch (JSONException e) {
				CxLog.e(TAG,
						"Error: failed to get object from Reminders result");
				e.printStackTrace();
			}
		}
		CxLog.v(TAG, "reminderStatus0=" + remindersStatus);
		if (null != remindersStatus && remindersStatus.length() > 0) {
			// updateReminderStatus(reminderStatus);
			Message message = mReminderListAllHandler.obtainMessage(UPDATE_REMINDER_STATUS);
			message.obj = remindersStatus;
			message.sendToTarget();
		}
	}

	private void updateReminderStatus(JSONArray reminderStatuses) {
		ReminderApi.getInstance().doUpdateReminderStatus(reminderStatuses,
				new JSONCaller() {
					@Override
					public int call(Object result) {
						CxLog.v(TAG, "result=" + result);
						return 0;
					}
				});
	}

	public void updateListReminderData() {
		ReminderApi.getInstance().doListUpdateReminders(new JSONCaller() {

			@Override
			public int call(Object result) {
				updateListReminder(result);
				return 0;
			}
		});
	}

	class RefreshTask extends AsyncTask<Integer, Integer, Integer> {

		@Override
		protected Integer doInBackground(Integer... params) {
			loadAllLocalReminders();
			return null;
		}

	}

	// init() for onCreate();
	private void init() {
//		View view = LayoutInflater.from(RkReminderList.this).inflate(
//				R.layout.cx_fa_view_reminder_list_all_reminders, null);

		mMenuBtn = (Button)findViewById(R.id.cx_fa_reminder_menu);
		mMenuBtn.setText(getString(R.string.cx_fa_navi_back));
		mMenuBtn.setPadding(30, 0, 10, 0);
		mAddNewReminder = (ImageButton)findViewById(R.id.cx_fa_add_reminder_btn);
		mMenuBtn.setOnClickListener(titleBtn);
		mAddNewReminder.setOnClickListener(titleBtn);
		
		mReminderList = (ScrollableListView)findViewById(R.id.id_reminder_listallreminders_list);
		mReminderList.setSelection(0);
	    mReminderList.setStackFromBottom(false);
		mActionBar = new QuickActionBar(R.layout.cx_fa_widget_quick_action_bar,
				QuickActionBar.DISPLAY_X_EXACT_MIDDLE_OF_ANCHOR,
				QuickActionBar.DISPLAY_Y_ABOVE_OF_ANCHOR);
		final View actionBarView = LayoutInflater.from(CxReminderList.this).inflate(
				R.layout.cx_fa_widget_quick_action_bar, null);
		mActionBar.setOnClickListener(R.id.button1, new OnClickListener() {

			@Override
			public void onClick(View v) {
				CxLog.v(TAG, "v tag = " + v.getTag());
				mActionBar.dismiss();
				final ReminderAdapterTag tag = (ReminderAdapterTag) v.getTag();
				
				DialogUtil du = DialogUtil.getInstance();
				du.setOnSureClickListener(new OnSureClickListener() {
					
					@Override
					public void surePress() {
						drop(tag.mId, tag.mBaseTimeStamp);		
					}
				});
				du.getSimpleDialog(CxReminderList.this, null, getString(R.string.cx_fa_reminder_del_msg), null, null).show();
				
//				new AlertDialog.Builder(RkReminderList.this)
//						.setMessage(R.string.cx_fa_reminder_del_msg)
//						.setPositiveButton(R.string.cx_fa_confirm_text,
//								new DialogInterface.OnClickListener() {
//
//									@Override
//									public void onClick(DialogInterface dialog,
//											int which) {
//										drop(tag.mId, tag.mBaseTimeStamp);
//									}
//								})
//						.setNegativeButton(R.string.cx_fa_cancel_button_text, null)
//						.show();
			}

		});
		mReminderList.setOnHeaderRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				refresh(false);
			}

		});

		mReminderList.setSelector(R.color.cx_fa_co_transparent);
		mReminderList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				final ReminderAdapterTag tag = (ReminderAdapterTag) view
						.getTag();
				if ((tag == null) || (tag.mType == 0))
					// do nothing for invalid tags;
					return;

				Reminder reminder = (Reminder) mReminderList
						.getItemAtPosition(position);
				CxLog.d(TAG, "flag = " + reminder.getFlag());
				CxLog.d(TAG, "author = " + reminder.getAuthor());
				if (reminder.getAuthor().equals(
						CxGlobalParams.getInstance().getUserId())) {
					if(reminder.getIsDelay()){
						ReminderController.getInstance().reset(reminder);
					} else {
					ReminderController.getInstance().reset(reminder.getId(),
							reminder.getTitle(), reminder.getTarget(),
							reminder.getPeriodType(),
							(long) (reminder.getBaseTimestamp()) * 1000,
							reminder.getAdvance(), reminder.getCustomize(),
							reminder.getFlag(), reminder.getIsDelay());
					}
					Intent intent = new Intent(CxReminderList.this,
							ReminderCreateActivity.class);
					startActivity(intent);
					CxReminderList.this.overridePendingTransition(
							R.anim.tran_next_in,
							R.anim.tran_next_out);
					CxReminderList.this.finish();
					
				} else {
					showNotEditReminderDialog();
				}
			}

		});

		mReminderList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {

				final ReminderAdapterTag tag = (ReminderAdapterTag) view
						.getTag();
				if ((tag == null) || (tag.mType == 0))
					// do nothing for invalid tags;
					return true;
				mActionBar.dismiss();
				Reminder reminder = (Reminder) mReminderList
						.getItemAtPosition(position);
				CxLog.d(TAG, "flag = " + reminder.getFlag());
				CxLog.d(TAG, "author = " + reminder.getAuthor());
				if (reminder.getAuthor().equals(
						CxGlobalParams.getInstance().getUserId())) {
					mActionBar.show(view, actionBarView);
				}

				return true;
			}

		});
		mReminderList.setAdapter(mReminderAdapter);
		reload();
	}
	
	OnClickListener titleBtn = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.cx_fa_reminder_menu:
				CxReminderList.this.finish();
				overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);	
				break;
			case R.id.cx_fa_add_reminder_btn:
				ReminderController controller = ReminderController.getInstance();
                controller.reset();
                Intent intent = new Intent(CxReminderList.this, ReminderCreateActivity.class);
                startActivity(intent);
                CxReminderList.this.overridePendingTransition(
                        R.anim.tran_next_in,
                        R.anim.tran_next_out);
                CxReminderList.this.finish();
				break;
			default:
				break;
			}
			
		}
	};
	
	//by wentong.men 131109
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
			finish();
			this.overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
			return false;
		}
		return super.onKeyDown(keyCode, event);
	};
	
	

	// List Adapter;
	class ReminderAdapter extends BaseAdapter {
		class ReminderAdapterTag {
			public int mType;

			public String mId;

			public String mDayTip;

			public String mPeriodLabel;

			public String mTitle;

			public int mTarget;

			public String mTip;

			public String mAuthor;

			public int mBaseTimeStamp;
		};

		private List<Reminder> mReminders = null;

		public void setReminderLists(final List<Reminder> reminders) {
			new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

				@Override
				public void run() {
					mReminders = reminders;
					mReminderAdapter.notifyDataSetChanged();
					mReminderList.onRefreshComplete();
					mReminderList.setSelection(0);
					mReminderList.setSelectionAfterHeaderView();
					setVisibilityForNoReminderTip((mReminders == null)
							|| (mReminders.size() == 0));
				}

			}, 1);
		}

		@Override
		public int getItemViewType(int position) {
			if (position == 0)
				// header view
				return 0;
			else
				// content view
				return 1;
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public int getCount() {
			if (mReminders == null)
				return 1;
			return mReminders.size() + 1;
		}

		@Override
		public Object getItem(int position) {
			if (position == 0)
				return null;
			if (mReminders == null)
				return null;
			return mReminders.get(position - 1);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View view = null;
			ReminderAdapterTag tag = null;
			Context context = parent.getContext();

			if (position == 0) {
				// render for the header view;
				if (convertView != null)
					view = convertView;

				if (view != null)
					tag = (ReminderAdapterTag) view.getTag();

				if (tag == null) {
					tag = new ReminderAdapterTag();
					tag.mType = 0;
				}

				view = LayoutInflater.from(CxReminderList.this).inflate(
						R.layout.cx_fa_view_reminder_list_all_reminders_header,
						null);
				view.setTag(tag);
				return view;
			}

			if (mReminders == null)
				return null;

			position -= 1;
			// render for the content view;
			Reminder reminder = (Reminder) mReminders.get(position);

			if (convertView == null) {
				view = LayoutInflater.from(context).inflate(
						R.layout.cx_fa_view_reminder_list_all_reminders_row,
						null);
			} else {
				view = convertView;
				tag = (ReminderAdapterTag) view.getTag();
			}

			if (tag != null) {
//				if ((tag.mId == null) || !tag.mId.equals(reminder.getId()) || reminder.getStatus() == 2) {
					// so the tag is dirty;
					tag = null;
//				}
			}

			if (tag == null) {
				tag = new ReminderAdapterTag();
				tag.mType = 1;
				tag.mId = reminder.getId();
				if (reminder.getIsDelay()) {
					tag.mTitle = reminder.getTitle()
							+ CxReminderList.this.getResources().getString(
									R.string.cx_fa_reminder_delay_title);
				} else {
					tag.mTitle = reminder.getTitle();
				}

				tag.mTarget = reminder.getTarget();
				tag.mTip = mDisplayUtility.createNLSReminderTipLabel(reminder);
				tag.mAuthor = reminder.getAuthor();
				tag.mPeriodLabel = mDisplayUtility
						.createNLSReminderPeriodLabel(reminder);
				tag.mBaseTimeStamp = reminder.getBaseTimestamp();
				
				view.setTag(tag);
			}
			try {
				tag.mDayTip = mDisplayUtility
						.createNLSReminderHappenLabel(reminder);
			} catch (Exception e) {
				tag.mDayTip = null;
				e.printStackTrace();
			}
			CxImageView roleOwnerImageIcon = (CxImageView) view
					.findViewById(R.id.cx_fa_view_reminder_list_all_reminders_row__owner);
			TextView titleField = (TextView) view
					.findViewById(R.id.cx_fa_view_reminder_list_all_reminders_row__title);
			ImageView targetIcon = (ImageView) view
					.findViewById(R.id.cx_fa_view_reminder_list_all_reminders_row__target_icon);
			TextView creatorField = (TextView) view
					.findViewById(R.id.cx_fa_view_reminder_list_all_reminders_row__creator_text);
			TextView periodField = (TextView) view
					.findViewById(R.id.cx_fa_view_reminder_list_all_reminders_row__period);
			ImageView ownerClockIcon = (ImageView) view
					.findViewById(R.id.cx_fa_view_reminder_list_all_reminders_row__owner_clock);
			TextView dayField = (TextView) view
					.findViewById(R.id.cx_fa_view_reminder_list_all_reminders_row__reminder_day);

			// fill data

			titleField.setText(tag.mTitle);
//			roleOwnerImageIcon.setImageURI(Uri.parse(RkGlobalParams
//					.getInstance().getIconSmall()));
			if (TextUtils.equals(reminder.getAuthor(), 
					CxGlobalParams.getInstance().getUserId())) { //属于自己创建 cx_fa_hb_icon_small
				/*roleOwnerImageIcon.setBackgroundResource(R.drawable.cx_fa_wf_icon_small);
				roleOwnerImageIcon.setImage(RkGlobalParams.getInstance().getIconBig(), 
						false, 74, RkReminderList.this, "head", 
						RkReminderList.this);*/
				roleOwnerImageIcon.displayImage(imageLoader, 
						CxGlobalParams.getInstance().getIconSmall(), 
						R.drawable.cx_fa_wf_icon_small, true, 
						CxGlobalParams.getInstance().getSmallImgConner());
			}else{ //对方创建
				/*roleOwnerImageIcon.setBackgroundResource(R.drawable.cx_fa_hb_icon_small);
				roleOwnerImageIcon.setImage(RkGlobalParams.getInstance().getPartnerIconBig(),
						false, 74, RkReminderList.this, "head", 
						RkReminderList.this);*/
				roleOwnerImageIcon.displayImage(imageLoader, 
						CxGlobalParams.getInstance().getPartnerIconBig(), 
						R.drawable.cx_fa_hb_icon_small, true, 
						CxGlobalParams.getInstance().getMateSmallImgConner());
				
			}
			
//			if (tag.mTarget == 2) { //双方
				targetIcon.setImageResource(R.drawable.remind_icondouble);
//			} else { //
//				targetIcon.setImageResource(R.drawable.remind_iconsingle);
//			}

			if (tag.mTip != null) {
				creatorField.setText(tag.mTip);
			} else {
				creatorField.setText("");
			}

			if (tag.mAuthor.equals(CxGlobalParams.getInstance().getUserId())) {
				ownerClockIcon.setImageResource(R.drawable.remind_clockblue);
			} else {
				ownerClockIcon.setImageResource(R.drawable.remind_clockorange);
			}

			if (tag.mPeriodLabel == null)
				periodField.setText("");
			else
				periodField.setText(tag.mPeriodLabel);

			if (tag.mDayTip == null)
				dayField.setText("");
			else
				dayField.setText(tag.mDayTip);

			return view;
		}

	}
	

	/**
	 * pop is save dialog
	 */
	private void showNotEditReminderDialog() {
		String msg = getResources().getString(
				R.string.cx_fa_reminder_check_not_edit_msg);
		Toast toast = Toast.makeText(CxReminderList.this, msg, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		LinearLayout toastView = (LinearLayout) toast.getView();
		ImageView imageCodeProject = new ImageView(CxReminderList.this);
		imageCodeProject.setImageResource(R.drawable.cancel_button);
		toastView.addView(imageCodeProject, 0);
		toast.show();
	}
	
	class CurrentObserver extends CxObserverInterface{

		@Override
		public void receiveUpdate(String actionTag) {
			if (null != mReminderAdapter) {
				mReminderAdapter.notifyDataSetChanged();
			}
		}
	}
	
}



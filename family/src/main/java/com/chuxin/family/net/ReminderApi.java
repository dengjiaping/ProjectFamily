package com.chuxin.family.net;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.chuxin.family.utils.CxLog;

public class ReminderApi extends ConnectionManager {
	private static final String TAG = "ReminderApi";
	private static final ReminderApi sInstance = new ReminderApi();
	private static final String PATH_LIST_REMINDERS = HttpApi.HTTP_SERVER_PREFIX
			+ "/User/reminder/list";
	private static final String PATH_CREATE_REMINDER = HttpApi.HTTP_SERVER_PREFIX
			+ "/User/reminder/create";
	private static final String PATH_UPDATE_REMINDER = HttpApi.HTTP_SERVER_PREFIX
			+ "/User/reminder/update";
	private static final String PATH_DELETE_REMINDER = HttpApi.HTTP_SERVER_PREFIX
			+ "/User/reminder/delete";
	private static final String PATH_UPDATE_REMINDER_STATUS = HttpApi.HTTP_SERVER_PREFIX
			+ "/User/reminder/update_status";

	private ReminderApi() {
	}

	public static ReminderApi getInstance() {
		return sInstance;
	}

	public void doListAllReminders(final ConnectionManager.JSONCaller callback) {
		NameValuePair[] params = { new BasicNameValuePair("flag", "0"), };

		doListReminders(params, callback);
	}

	public void doListUpdateReminders(
			final ConnectionManager.JSONCaller callback) {
		NameValuePair[] params = { new BasicNameValuePair("flag", "1"), };
		CxLog.d(TAG, "flag 1");
		doListReminders(params, callback);
	}

	protected void doListReminders(NameValuePair[] params,
			final ConnectionManager.JSONCaller callback) {
		final String url = PATH_LIST_REMINDERS;
		doHttpGet(url, new ConnectionManager.JSONCaller() {

			@Override
			public int call(Object data) {
				JSONObject result = (JSONObject) data;
				CxLog.d(TAG,
						"flag 1 THE RESULT of " + url + ":" + result.toString());
				try {
					int rc = result.getInt("rc");
					if (rc != 0) {//视为失败add by shichao
						callback.call(null);
						return 0;
					}

					if (callback != null) {
						JSONArray reminders = result.getJSONArray("data");
						callback.call(reminders);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

				return 0;
			}
		}, params);
	}

	public void doUpdateReminder(final String reminderId, final String title,
			final int target, final int period, final int baseTime,
			final int advance, final int custom,
			final ConnectionManager.JSONCaller callback) {

		NameValuePair[] params = { new BasicNameValuePair("id", reminderId),
				new BasicNameValuePair("cycle_type", String.valueOf(period)),
				new BasicNameValuePair("real_ts", String.valueOf(baseTime)),
				new BasicNameValuePair("base_ts", String.valueOf(baseTime)),
				new BasicNameValuePair("data", String.valueOf(custom)),
				new BasicNameValuePair("advance", String.valueOf(advance)),
				new BasicNameValuePair("comment", title),
				new BasicNameValuePair("target", String.valueOf(target)), };

		final String url = PATH_UPDATE_REMINDER;
		doHttpPost(url, new ConnectionManager.JSONCaller() {

			@Override
			public int call(Object data) {
				JSONObject result = (JSONObject) data;
				Log.d("HI", "THE RESULT of " + url + ":" + result.toString());
				try {
					int rc = result.getInt("rc");
					if (rc != 0) {
						callback.call(result);
						return 0;
					}

					if (callback != null) {
						JSONObject reminder = result.getJSONObject("data");
						callback.call(reminder);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

				return 0;
			}
		}, params);
	}

	public void doCreateReminder(final String title, final int target,
			final int period, final int baseTime, final int advance,
			final int custom, final String dateString, 
			final ConnectionManager.JSONCaller callback) {

		NameValuePair[] params = {
				new BasicNameValuePair("target", String.valueOf(target)),
				new BasicNameValuePair("cycle_type", String.valueOf(period)),
				new BasicNameValuePair("real_ts", String.valueOf(baseTime)),
				new BasicNameValuePair("base_ts", String.valueOf(baseTime)),
				new BasicNameValuePair("data", String.valueOf(custom)),
				new BasicNameValuePair("advance", String.valueOf(advance)),
				new BasicNameValuePair("date_text", dateString),
				new BasicNameValuePair("comment", title), };

		final String url = PATH_CREATE_REMINDER;
		doHttpPost(url, new ConnectionManager.JSONCaller() {

			@Override
			public int call(Object data) {
				JSONObject result = (JSONObject) data;
				Log.d("HI", "THE RESULT of " + url + ":" + result.toString());
				try {
					int rc = result.getInt("rc");
					if (rc != 0) {
						callback.call(result);
						return 0;
					}

					if (callback != null) {
						JSONObject reminder = result.getJSONObject("data");
						callback.call(reminder);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

				return 0;
			}
		}, params);
	}

	public void doDeleteReminder(final String reminderId,
			final ConnectionManager.JSONCaller callback) {

		NameValuePair[] params = { new BasicNameValuePair("id", reminderId), };

		final String url = PATH_DELETE_REMINDER;
		doHttpPost(url, new ConnectionManager.JSONCaller() {

			@Override
			public int call(Object data) {
				JSONObject result = (JSONObject) data;
				Log.d("HI", "THE RESULT of " + url + ":" + result.toString());
				try {
					int rc = result.getInt("rc");
					if (rc != 0) {
						callback.call(result);
						return 0;
					}

					if (callback != null) {
						callback.call(null);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

				return 0;
			}
		}, params);
	}

	public void doUpdateReminderStatus(final JSONArray reminderStatuses,
			final ConnectionManager.JSONCaller callback) {

		if (reminderStatuses.length() == 0)
			return;

		NameValuePair[] params = { new BasicNameValuePair("status",
				reminderStatuses.toString()), };

		final String url = PATH_UPDATE_REMINDER_STATUS;
		doHttpPost(url, new ConnectionManager.JSONCaller() {

			@Override
			public int call(Object data) {
				JSONObject result = (JSONObject) data;
				Log.d("HI", "THE RESULT of " + url + ":" + result.toString());
				try {
					int rc = result.getInt("rc");
					if (rc != 0) {
						callback.call(result);
						return 0;
					}

					if (callback != null) {
						JSONObject reminder = result.getJSONObject("data");
						callback.call(reminder);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

				return 0;
			}
		}, params);
	}

}

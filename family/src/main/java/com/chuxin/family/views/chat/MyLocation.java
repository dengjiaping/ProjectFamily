package com.chuxin.family.views.chat;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.baidu.platform.comapi.map.Projection;
import com.chuxin.family.app.CxRootActivity;
import com.chuxin.family.global.CxGlobalConst;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MyLocation extends CxRootActivity {

	BMapManager mBMapMan = null;
	MapView mMapView = null;

	private Button mBackBtn, mSendLocation;
	private boolean isNameFinish = false; // 地名获取成功
	private MKAddrInfo addressInfo;

	View popCacheView;
	TextView popName;
	ImageView locIcon;

	ItemizedOverlay<OverlayItem> centerOverlay;

	boolean isFirstLoc = true;
	LocationClient mLocClient;
	LocationData locData = null;
	BDLocationListener locListener = new BDLocationListener() {

		@Override
		public void onReceivePoi(BDLocation arg0) {

		}

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (null == location) {
				return;
			}
			if (null == mMapView) {
				return;
			}
			// Log.i("$$$", ""+location.getAddrStr());
			locData.latitude = location.getLatitude();
			locData.longitude = location.getLongitude();
			//
			locData.accuracy = location.getRadius();
			locData.direction = location.getDerect();
			//
			myLocationOverlay.setData(locData);
			//
			mMapView.refresh();

			//
			if (isFirstLoc) {
				//
				GeoPoint gp = new GeoPoint((int) (locData.latitude * 1e6),
						(int) (locData.longitude * 1e6));

				if (!isShow) { // 设置POI点
					mMapView.getController().animateTo(gp);
					// 发起geo请求
					mSendLocation.setVisibility(View.INVISIBLE);
					search.reverseGeocode(gp);
				} else { // 仅仅查看POI点

				}
			}
			//
			isFirstLoc = false;

		}
	};

	//
	MyLocationOverlay myLocationOverlay = null;

	MKSearch search;

	private boolean isShow = false; // 默认为false表示用户自定义poi点
	float lat, lon; // POI点经纬度
	String locName; // poi点地名

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mBMapMan = new BMapManager(getApplication());
		mBMapMan.init("DA6C4AB6C1E034D0D3F1875CBD80BFC7C8A43AE7", null);
		mBMapMan.start();
		//
		setContentView(R.layout.cx_fa_activity_location_map);

		popCacheView = getLayoutInflater().inflate(R.layout.pop_poi_info, null);
		popName = (TextView) popCacheView.findViewById(R.id.pop_name);
		// locIcon = (ImageView)popCacheView.findViewById(R.id.loc_icon);

		mBackBtn = (Button) findViewById(R.id.cx_fa_activity_title_back);
		mSendLocation = (Button) findViewById(R.id.cx_fa_activity_title_more);
		mBackBtn.setText(getString(R.string.cx_fa_navi_back));
		mBackBtn.setOnClickListener(funcsLintener);

		Intent dataIntent = MyLocation.this.getIntent();
		if ((null != dataIntent)
				&& (1 == dataIntent.getIntExtra(CxGlobalConst.S_LOCATION_TYPE,
						0))) { // 从聊天记录查看位置
			// 这个情况下，地图是单纯的现实，应该没有"发送位置"和"回到自己位置"，用户定义的POI点不可改变
			mSendLocation.setVisibility(View.INVISIBLE);
			isShow = true;
			lat = dataIntent.getFloatExtra(CxGlobalConst.S_LOCATION_LAT, 0);
			lon = dataIntent.getFloatExtra(CxGlobalConst.S_LOCATION_LON, 0);
			locName = dataIntent.getStringExtra(CxGlobalConst.S_LOCATION_TEXT);

		}

		mSendLocation.setText(getString(R.string.cx_fa_send_location_info));
		mSendLocation.setOnClickListener(funcsLintener);
		
		locIcon = (ImageView)findViewById(R.id.cx_fa_loc_me);
		locIcon.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				isFirstLoc = true;
				mLocClient.requestLocation();
			}
		});
		if (isShow) {
			locIcon.setVisibility(View.GONE);
		}else{
			locIcon.setVisibility(View.VISIBLE);
		}

		mMapView = (MapView) findViewById(R.id.cx_fa_map);
		mMapView.setBuiltInZoomControls(true);
		mMapView.getController().setZoom(14);
		mMapView.getController().enableClick(true);
		// GeoPoint center = new GeoPoint((int)(lat*1e6), (int)(lon*1e6));
		// RkLog.i("%%%%%%%%",
		// "lat="+center.getLatitudeE6()+",lon="+center.getLongitudeE6());
		// mMapView.getController().animateTo(center);

		mMapView.regMapViewListener(mBMapMan, new MKMapViewListener() {

			@Override
			public void onMapMoveFinish() {
				// Log.i("-----", "onMapMoveFinish method");
				// popPoiHandler.sendEmptyMessage(1);
				//
				isNameFinish = false;
				addressInfo = null;
				mSendLocation.setVisibility(View.INVISIBLE);
				if (isShow) { // 只需要重新生成POI 的view
					CxLog.i("!!!", "--------------------------");
					return;
				}
				search.reverseGeocode(mMapView.getMapCenter());
			}

			@Override
			public void onMapAnimationFinish() {

			}

			@Override
			public void onGetCurrentMap(Bitmap arg0) {

			}

			@Override
			public void onClickMapPoi(MapPoi arg0) {

			}
		});

		// ------------location
		mLocClient = new LocationClient(MyLocation.this);
		locData = new LocationData();
		mLocClient.registerLocationListener(locListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);//
		option.setCoorType("bd09ll"); //
		option.setScanSpan(5000);
		mLocClient.setLocOption(option);
		mLocClient.start();

		//
		myLocationOverlay = new MyLocationOverlay(mMapView);
		//
		myLocationOverlay.setData(locData);
		//
		mMapView.getOverlays().add(myLocationOverlay);
		myLocationOverlay.enableCompass();
		//
		mMapView.refresh();

		// -----------------------reverseGeocode

		search = new MKSearch();
		search.init(mBMapMan, new MKSearchListener() {

			@Override
			public void onGetWalkingRouteResult(MKWalkingRouteResult arg0,
					int arg1) {

			}

			@Override
			public void onGetTransitRouteResult(MKTransitRouteResult arg0,
					int arg1) {

			}

			@Override
			public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {

			}

			@Override
			public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {

			}

			@Override
			public void onGetPoiDetailSearchResult(int arg0, int arg1) {

			}

			@Override
			public void onGetDrivingRouteResult(MKDrivingRouteResult arg0,
					int arg1) {

			}

			@Override
			public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {

			}

			@Override
			public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
				if (0 != arg1) {
					return;
				}
				if (arg0.type == MKAddrInfo.MK_REVERSEGEOCODE) {
					Log.i("!!!!!",
							arg0.strAddr + ";" + arg0.geoPt.getLatitudeE6()
									+ "," + arg0.geoPt.getLongitudeE6());
					Message msg = new Message();
					msg.obj = arg0;
					// popPoiHandler.sendMessage(msg);
					popCenterHandler.sendMessage(msg);
				}

			}
		});

		new Thread(new Runnable() {

			@Override
			public void run() {

				if (isShow && (0 != lat) && (0 != lon)) {
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
					}
					final GeoPoint center = new GeoPoint((int) (lat * 1e6),
							(int) (lon * 1e6));
					mMapView.getController().animateTo(center);
					search.reverseGeocode(center);
				}

			}
		}).start();

	} // end onCreate()

	private OnClickListener funcsLintener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.cx_fa_activity_title_back:
				MyLocation.this.finish();
				break;
			// case R.id.rk_loc_layer: // 发送位置信息
			case R.id.cx_fa_activity_title_more:
				//
				if (!isNameFinish) {
					Toast.makeText(MyLocation.this,
							getString(R.string.cx_fa_chat_locate_fail),
							Toast.LENGTH_LONG).show();
					return;
				}
				if ((null == addressInfo) || (null == addressInfo.strAddr)
						|| (null == addressInfo.geoPt)) {
					Toast.makeText(MyLocation.this,
							getString(R.string.cx_fa_chat_locate_fail),
							Toast.LENGTH_LONG).show();
					return;
				}

				try {
					ChatFragment.getInstance().sendMessage(addressInfo.strAddr,
							5,
							(float) (addressInfo.geoPt.getLatitudeE6() / 1e6),
							(float) (addressInfo.geoPt.getLongitudeE6() / 1e6));
				} catch (Exception e) {
					e.printStackTrace();
				}
				MyLocation.this.finish();
				break;
			default:
				break;
			}

		}
	};

	@Override
	protected void onDestroy() {
		try {
			if (null != mLocClient) {
				mLocClient.stop();
			}

			if (null != mMapView) {
				mMapView.getOverlays().clear();
				mMapView.destroy();
			}
			if (null != mBMapMan) {
				mBMapMan.stop();
				mBMapMan.destroy();
			}
		} catch (Exception e) {
		}
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		if (null != mMapView) {
			mMapView.onPause();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		if (null != mMapView) {
			mMapView.onResume();
		}
		super.onResume();
	}

	public Bitmap getBitmapFromView(View view) {
		view.destroyDrawingCache();
		view.measure(View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
				.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.setDrawingCacheEnabled(true);
		Bitmap bitmap = view.getDrawingCache(true);
		return bitmap;
	}

	Handler popCenterHandler = new Handler() {
		public void handleMessage(Message msg) {
			CxLog.i("!!!", "************************");
			if (null == msg) {
				return;
			}

			MKAddrInfo arg0 = null;
			try {
				arg0 = (MKAddrInfo) msg.obj;
			} catch (Exception e) {
			}
			if (null == arg0) {
				return;
			}

			if (!isShow) {
				addressInfo = arg0;
				isNameFinish = true;
				mSendLocation.setVisibility(View.VISIBLE);
			}

			if ((null != centerOverlay) && (centerOverlay.size() > 0)) {
				centerOverlay.removeAll();
				mMapView.getOverlays().remove(centerOverlay);
			}

			centerOverlay = new ItemizedOverlay<OverlayItem>(getResources()
					.getDrawable(R.drawable.chat_location), mMapView);

			GeoPoint p = new GeoPoint(arg0.geoPt.getLatitudeE6(),
					arg0.geoPt.getLongitudeE6());
			OverlayItem item = new OverlayItem(p, "", "");
			item.setMarker(getResources().getDrawable(R.drawable.chat_location));

			centerOverlay.addItem(item);
			mMapView.getOverlays().add(centerOverlay);

			PopupOverlay placeOverlay = new PopupOverlay(mMapView,
					new PopupClickListener() {

						@Override
						public void onClickedPopup(int arg0) {

						}
					});

			Point sp = new Point();
			Projection proj = mMapView.getProjection();
			sp = proj.toPixels(p, sp);

			GeoPoint pName = proj.fromPixels(sp.x, sp.y - 30);
			// GeoPoint pName = new GeoPoint (arg0.geoPt.getLatitudeE6()+5000,
			// arg0.geoPt.getLongitudeE6());
			popName.setText(arg0.strAddr);
			placeOverlay.showPopup(getBitmapFromView(popName), pName, 8);
			mMapView.getController().setCenter(p);
			mMapView.refresh();

		};
	};

}

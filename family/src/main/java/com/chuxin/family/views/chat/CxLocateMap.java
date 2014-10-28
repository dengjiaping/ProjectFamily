package com.chuxin.family.views.chat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
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
import com.chuxin.family.app.CxRootActivity;
import com.chuxin.family.global.CxGlobalConst;
import com.chuxin.family.R;

public class CxLocateMap extends CxRootActivity {

	private boolean isFirstLocate = true;
	private boolean isLocClientStop = false;
	private boolean isNameFinish = false; //地名获取成功

	private String mBaiduKey = "DA6C4AB6C1E034D0D3F1875CBD80BFC7C8A43AE7";
	private BMapManager mBaiduMapManager;
	private MapView mMapView;
	private MapController mMapController = null;
	private LocationManager mLocManager;
	private String mBestProvider;
	private Location mLocation;
	private MKSearch mSearch = null;
	
	private LocationClient mLocateClient;
	private LocationData mLocateData = null;
	private MyLocationOverlay mLocationOverlay = null;
	
	private PopupOverlay mPop = null;
	
	private View popLayer;
	private TextView popInfoTextView;
	
	private Button mBackBtn, mSendLocation;
	
	private GeoPoint specialLocation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mBaiduMapManager = new BMapManager(CxLocateMap.this);
		boolean initialRes = false;
		try {
			initialRes = mBaiduMapManager.init(mBaiduKey, mGeneralListener);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!initialRes) {
			CxLocateMap.this.finish();
			return;
		}
		
		setContentView(R.layout.cx_fa_activity_location_map);
		
		mBackBtn = (Button) findViewById(R.id.cx_fa_activity_title_back);
		mSendLocation = (Button) findViewById(R.id.cx_fa_activity_title_more);
		mBackBtn.setText(getString(R.string.cx_fa_navi_back));
		mBackBtn.setOnClickListener(funcsLintener);
		
		Intent dataIntent = CxLocateMap.this.getIntent();
		if ((null != dataIntent)
				&& (1 == dataIntent.getIntExtra(CxGlobalConst.S_LOCATION_TYPE,
						0))) { // 从聊天记录查看位置
			mSendLocation.setVisibility(View.INVISIBLE);
			return;
		}
		
		mMapView = (MapView)findViewById(R.id.cx_fa_map);
		mMapController = mMapView.getController();
		mMapController.enableClick(true);
		mMapController.setZoom(16);
        mMapView.setBuiltInZoomControls(true);
//		mMapView.regMapViewListener(mBaiduMapManager, mapViewListener);
		
//		mSearch = new MKSearch();
//		mSearch.init(mBaiduMapManager, geoSearch);
		
		mLocateClient = new LocationClient(CxLocateMap.this);
		mLocateData = new LocationData();
		mLocateClient.registerLocationListener(new MyLocationListenner());
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);//打开gps
        option.setCoorType("bd09ll");
        option.setScanSpan(5000);
        mLocateClient.setLocOption(option);
        mLocateClient.start();
        
      //定位图层初始化
		mLocationOverlay = new MyLocationOverlay(mMapView);
		//设置定位数据
	    mLocationOverlay.setData(mLocateData);
	    //添加定位图层
		mMapView.getOverlays().add(mLocationOverlay);
		mLocationOverlay.enableCompass();
		//修改定位数据后刷新图层生效
		mMapView.refresh();
		
//		new Handler().postDelayed(requestLocation, 100);
		
	}
	
	private OnClickListener funcsLintener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.cx_fa_activity_title_back:
				CxLocateMap.this.finish();
				break;
//			case R.id.rk_loc_layer: // 发送位置信息
			case R.id.cx_fa_activity_title_more:
				//
				if (!isNameFinish) {
					Toast.makeText(CxLocateMap.this,
							getString(R.string.cx_fa_chat_locate_fail),
							Toast.LENGTH_LONG).show();
					return;
				}
				try {
					ChatFragment.getInstance().sendMessage(
							popInfoTextView.getText().toString(), 5,
							(float)(specialLocation.getLatitudeE6()/1e6),
							(float)(specialLocation.getLongitudeE6()/1e6) );
				} catch (Exception e) {
					e.printStackTrace();
				}
				CxLocateMap.this.finish();
				break;
			default:
				break;
			}

		}
	};
	
	private void loadPopText(){
		popLayer = getLayoutInflater().inflate(R.layout.cx_fa_activity_location_poptext, null); 
		popInfoTextView = (TextView)popLayer.findViewById(R.id.rk_loc_name);
		
	}
	
	@Override
	protected void onDestroy() {
		if (mLocateClient != null)
			mLocateClient.stop();
		mMapView.destroy();
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	mMapView.onSaveInstanceState(outState);
    	
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
    	mMapView.onRestoreInstanceState(savedInstanceState);
    }
    
    MKSearchListener geoSearch = new MKSearchListener() {
		
		@Override
		public void onGetWalkingRouteResult(MKWalkingRouteResult arg0, int arg1) {
		}
		
		@Override
		public void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1) {
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
		public void onGetDrivingRouteResult(MKDrivingRouteResult arg0, int arg1) {
		}
		
		@Override
		public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
		}
		
		@Override
		public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
			if (arg1 != 0) { //goecoder response error
				return;
			}
			
			if (arg0.type == MKAddrInfo.MK_REVERSEGEOCODE){
				//反地理编码：通过坐标点检索详细地址及周边poi
				String strInfo = arg0.strAddr;
				if (null != strInfo) {
					isNameFinish = true;
				}
				Toast.makeText(CxLocateMap.this, strInfo, Toast.LENGTH_LONG).show();
				
			}
			
			
		}
	};
    
    private MKMapViewListener mapViewListener = new MKMapViewListener() {
		
		@Override
		public void onMapMoveFinish() {
//			RkLog.i("ppppp", mMapView.getMapCenter().getLatitudeE6()+","+mMapView.getMapCenter().getLongitudeE6());
			isNameFinish = false;
			mSearch.reverseGeocode(mMapView.getMapCenter());
			specialLocation = mMapView.getMapCenter();
			
			//add center red icon
			
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
	};
	
	/*private Runnable requestLocation = new Runnable() {
		
		@Override
		public void run() {
			mLocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			
			Criteria mCriteria01 = new Criteria();
			mCriteria01.setAccuracy(Criteria.ACCURACY_COARSE);
			mCriteria01.setAltitudeRequired(false);
			mCriteria01.setBearingRequired(false);
			mCriteria01.setCostAllowed(true);
			mCriteria01.setSpeedRequired(false);
			mCriteria01.setPowerRequirement(Criteria.POWER_LOW);

			mBestProvider = mLocManager.getBestProvider(mCriteria01, true);
			
			if (null != mBestProvider) {
				mLocation = mLocManager.getLastKnownLocation(mBestProvider);
				int count = 0;
				while( (null == mLocation) && (count < 2)){
					count++;
					mLocation = mLocManager.getLastKnownLocation(mBestProvider);
					if (null != mLocation) {
						moveToCurrent.sendEmptyMessageDelayed(1, 50);
						break;
					}
					
					mLocManager.requestLocationUpdates(mBestProvider, 3 * 1000, 1,
							new LocationListener() {

								@Override
								public void onStatusChanged(String provider, int status,
										Bundle extras) {

								}

								@Override
								public void onProviderEnabled(String provider) {

								}

								@Override
								public void onProviderDisabled(String provider) {

								}

								@Override
								public void onLocationChanged(Location location) {
									//
									if ((null == mLocation) && (null != location)) {
										mLocation = location;
										// 移动地图
										moveToCurrent.sendEmptyMessage(1);
									}

								}
							});
					mLocation = mLocManager.getLastKnownLocation(mBestProvider);
				}
				if (null == mLocation) {
					mLocation = mLocManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				}
				
				if (null != mLocation) {
					moveToCurrent.sendEmptyMessageDelayed(1, 50);
				}

			}
			
		}
	};*/ 
	
	public class MyLocationListenner implements BDLocationListener {
    	
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null || isLocClientStop)
                return ;
            mLocateData.latitude = location.getLatitude();
            mLocateData.longitude = location.getLongitude();
            //如果不显示定位精度圈，将accuracy赋值为0即可
            mLocateData.accuracy = location.getRadius();
            mLocateData.direction = location.getDerect();
            //更新定位数据
            mLocationOverlay.setData(mLocateData);
            //更新图层数据执行刷新后生效
            mMapView.refresh();
            //是手动触发请求或首次定位时，移动到定位点
            if (isFirstLocate){
            	//移动地图到定位点
                mMapController.setCenter(new GeoPoint((int)(mLocateData.latitude* 1e6), 
                		(int)(mLocateData.longitude *  1e6)));
                isFirstLocate = false; //首次定位完成
            }
            
        }
        
        public void onReceivePoi(BDLocation poiLocation) {
            if (poiLocation == null){
                return ;
            }
        }
    }
	
	private Handler moveToCurrent = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (null != mLocation) {
				
				GeoPoint center = new GeoPoint((int)(mLocation.getLatitude() * 1E6), 
						(int)(mLocation.getLongitude() * 1E6));
				mMapController.setCenter(center);
				
				//生成ItemizedOverlay图层用来标注结果点
				ItemizedOverlay<OverlayItem> itemOverlay = new ItemizedOverlay<OverlayItem>(null, mMapView);
				//生成Item
				OverlayItem item = new OverlayItem(center, "", null);
				//得到需要标在地图上的资源
				Drawable marker = getResources().getDrawable(R.drawable.chat_currentlocation);  
				//为maker定义位置和边界
				marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
				//给item设置marker
				item.setMarker(marker);
				//在图层上添加item
				itemOverlay.addItem(item);
				
				//清除地图其他图层
				mMapView.getOverlays().clear();
				//添加一个标注ItemizedOverlay图层
				mMapView.getOverlays().add(itemOverlay);
				
				
				//执行刷新使生效
				mMapView.refresh();
				
			}
		};
	};
	
	MKGeneralListener mGeneralListener = new MKGeneralListener() {
		
		@Override
		public void onGetPermissionState(int arg0) {

		}
		
		@Override
		public void onGetNetworkState(int arg0) {
			if (arg0 ==  MKEvent.ERROR_PERMISSION_DENIED) {
                //授权Key错误：
                Toast.makeText(CxLocateMap.this, 
                        "请在 DemoApplication.java文件输入正确的授权Key！", Toast.LENGTH_LONG).show();
                return;
            }
			
		}
	};
	
}

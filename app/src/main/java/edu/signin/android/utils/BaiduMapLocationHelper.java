package edu.signin.android.utils;

import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import edu.signin.android.SignInApplication;

public final class BaiduMapLocationHelper  extends BDAbstractLocationListener implements LifecycleObserver {

    private static final String TAG = "BaiduMapLocationHelper";

    private WeakReference<LifecycleOwner> ownerWeakReference;

    private WeakReference<MapView> mapViewWeakReference;

    private WeakReference<BaiduMap> mapWeakReference;

    private WeakReference<LocationClient> locationClientWeakReference;

    private OnLocation mOnLocation;

    private final AtomicBoolean firstLocation = new AtomicBoolean(true);

    private final AtomicBoolean flashMapLocation = new AtomicBoolean(false);


    public void bind(LifecycleOwner owner, MapView mapView){
        this.bind(owner, mapView, null);
    }

    public void bind(LifecycleOwner owner, MapView mapView, OnLocation onLocation){
        this.ownerWeakReference = new WeakReference<>(owner);
        this.mapViewWeakReference = new WeakReference<>(mapView);
        this.mapWeakReference = new WeakReference<>(mapView.getMap());
        this.mOnLocation = onLocation;
        owner.getLifecycle().addObserver(this);
        this.locationClientWeakReference = new WeakReference<>(new LocationClient(SignInApplication.getApplication()));
    }

    /**
     * ??????????????????????????????????????????????????????
     * @param flash
     */
    public void setFlashMapLocation(boolean flash){
        flashMapLocation.set(flash);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    void onCreate(){
        Log.d(TAG, "onCreate");
        if (this.mapWeakReference == null){
            return;
        }
        if (this.locationClientWeakReference == null){
            return;
        }
        BaiduMap map = this.mapWeakReference.get();
        if (map != null){
            //???????????????????????????????????????
            map.setIndoorEnable(true);
            map.setMapType(BaiduMap.MAP_TYPE_NORMAL);
            map.setMyLocationEnabled(true);
        }

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    void onResume(){
        Log.d(TAG, "onResume");
        if (this.mapViewWeakReference == null){
            return;
        }
        MapView mapView = this.mapViewWeakReference.get();
        if (mapView == null){
            return;
        }
        mapView.onResume();

        LocationClient client = this.locationClientWeakReference.get();
        if (client == null){
            return;
        }
        if (client.isStarted()){
            return;
        }
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // ??????gps
        option.setCoorType("bd09ll"); // ??????????????????
        option.setScanSpan(1000);
        option.setIsNeedAddress(true);
        option.setNeedNewVersionRgc(true);
        option.setIgnoreKillProcess(true);
        //??????locationClientOption
        client.setLocOption(option);
        //??????LocationListener?????????
        client.registerLocationListener(this);
        //????????????????????????
        client.start();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    void onPause(){
        Log.d(TAG, "onPause");
        if (this.mapViewWeakReference == null){
            return;
        }
        MapView mapView = this.mapViewWeakReference.get();
        if (mapView == null){
            return;
        }
        mapView.onPause();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onDestroy(LifecycleOwner owner){
        if (this.locationClientWeakReference != null){
            LocationClient client = this.locationClientWeakReference.get();
            if (client != null){
                client.stop();
            }
            client = null;
            this.locationClientWeakReference.clear();
        }
        if (this.mapWeakReference != null){
            BaiduMap map = this.mapWeakReference.get();
            if (map != null){
                map.setMyLocationEnabled(false);
            }
            map = null;
            this.mapWeakReference.clear();
        }
        if (this.mapViewWeakReference != null){
            MapView mapView = this.mapViewWeakReference.get();
            if (mapView != null){
                mapView.onDestroy();
            }
            mapView = null;
            this.mapViewWeakReference.clear();
        }
        owner.getLifecycle().removeObserver(this);

        if (this.ownerWeakReference != null){
            this.ownerWeakReference.clear();
        }
        this.mOnLocation = null;
        this.firstLocation.set(true);
        Log.d(TAG, "onDestroy");
    }


    @Override
    public void onReceiveLocation(BDLocation location) {
        if (this.ownerWeakReference == null){
            return;
        }
        if (this.mapViewWeakReference == null){
            return;
        }
        if (this.mapWeakReference == null){
            return;
        }
        LifecycleOwner owner = this.ownerWeakReference.get();
        if (owner == null){
            return;
        }

        MapView mapView = this.mapViewWeakReference.get();

        if (location == null || mapView == null){
            return;
        }
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(location.getRadius())
                // ?????????????????????????????????????????????????????????0-360
                .direction(location.getDirection()).latitude(location.getLatitude())
                .longitude(location.getLongitude()).build();

        if (location.getLocType() == 61 || location.getLocType() == 66 || location.getLocType() == 161){
            if (location.getAddrStr() == null){
                return;
            }
            if (this.mOnLocation != null){
                if (firstLocation.get()){
                    this.mOnLocation.onFirstLocation(location);
                    //this.markStart(location.getLatitude(), location.getLongitude(), R.drawable.map_icon_start);
                }
                this.mOnLocation.onLocation(location);
            }
        }

        if (owner.getLifecycle().getCurrentState() == Lifecycle.State.RESUMED && (this.flashMapLocation.get() || this.firstLocation.get())){
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(ll).zoom(18.0f);
            BaiduMap baiduMap = this.mapWeakReference.get();
            if (baiduMap == null){
                return;
            }
            baiduMap.setMyLocationData(locData);
            baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            firstLocation.set(false);
        }
    }

    /**
     * ????????????????????????
     * @return
     */
    public boolean isFlashMapLocation(){
        return this.flashMapLocation.get();
    }

    public void markStart(double lat, double lon, int res){
        this.markStart(lat, lon, res, null);
    }

    /**
     * ?????????
     * @param lat
     * @param lon
     * @param res
     */
    public void markStart(double lat, double lon, int res, BaiduMap map){

        //??????Maker?????????
        LatLng point = new LatLng(lat, lon);
        //??????Marker??????
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(res);
        //??????MarkerOption???????????????????????????Marker
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .scaleX(0.5f)
                .scaleY(0.5f)
                .icon(bitmap);
        //??????????????????Marker????????????
        BaiduMap baiduMap = map;
        if (baiduMap == null){
            if (this.mapWeakReference == null){
                return;
            }
            baiduMap = this.mapWeakReference.get();
        }
        baiduMap.addOverlay(option);
    }


    public Overlay buildOverlayLine(List<LatLng> points, BaiduMap map){
        if (points.size() == 1){
            points.add(points.get(0));
        }
        OverlayOptions mOverlayOptions = new PolylineOptions()
                .width(10)
                .color(0xAAFF0000)
                .points(points);
        //????????????????????????
        //mPloyline ????????????
        return  map.addOverlay(mOverlayOptions);
    }



    public interface OnLocation{

        void onFirstLocation(BDLocation location);

        void onLocation(BDLocation location);
    }
}

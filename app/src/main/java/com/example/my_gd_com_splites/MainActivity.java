package com.example.my_gd_com_splites;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.SubPoiItem;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PoiSearch.OnPoiSearchListener, WeatherSearch.OnWeatherSearchListener {

    private MapView mapView;
    private AMap aMap;

    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private Location amapLocation;
    private RecyclerView recy;
    private RecyAdapter recyAdapter;
    private EditText ed_te;
    private Button but_sou;
    private ArrayList<PoiItem> pois;
    private String st;
    private Toolbar toolbar;
    private WeatherSearch mweathersearch;
    private WeatherSearchQuery mquery;
    private LocalWeatherLive weatherlive;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recy = findViewById(R.id.recy);
        ed_te = findViewById(R.id.ed_te);
        but_sou = findViewById(R.id.but_sou);
        toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle("天气查询");
        setSupportActionBar(toolbar);

        mquery = new WeatherSearchQuery("北京", WeatherSearchQuery.WEATHER_TYPE_LIVE);
        mweathersearch = new WeatherSearch(this);
        mweathersearch.setOnWeatherSearchListener(this);
        mweathersearch.setQuery(mquery);
        mweathersearch.searchWeatherAsyn(); //异步搜索


        but_sou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                st = ed_te.getText().toString();
                initJs();
            }
        });

        //定义了一个地图view

        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        aMap = mapView.getMap();

        //获取地图控件引用
        mapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mapView.onCreate(savedInstanceState);
        initLa();

        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        //aMap.getUiSettings().setMyLocationButtonEnabled(true);设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。

        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
    }

    private void initJs() {
        PoiSearch.Query nan = new PoiSearch.Query(st, "", "北京");
        nan.setPageSize(10);

        PoiSearch poiSearch = new PoiSearch(this, nan);

        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();

    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        pois = poiResult.getPois();
        recyAdapter = new RecyAdapter(this, pois);
        recy.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
        recy.setLayoutManager(new LinearLayoutManager(this));
        recy.setAdapter(recyAdapter);
        recyAdapter.notifyDataSetChanged();

        Log.i("TAG", "onPoiSearched: " + pois.get(0));

    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {


    }

    @Override
    public void onWeatherLiveSearched(LocalWeatherLiveResult localWeatherLiveResult, int i) {
        if (i == 1000) {
            if (localWeatherLiveResult != null && localWeatherLiveResult.getLiveResult() != null) {
                weatherlive = localWeatherLiveResult.getLiveResult();
//                reporttime1.setText(weatherlive.getReportTime() + "发布");
//                weather.setText(weatherlive.getWeather());
//                Temperature.setText(weatherlive.getTemperature() + "°");
                toolbar.setTitle(weatherlive.getWindDirection() + "风     " + weatherlive.getWindPower() + "级");
//                humidity.setText("湿度         " + weatherlive.getHumidity() + "%");
            } else {
//                ToastUtil.show(this, R.string.no_result);
            }
        } else {
//            ToastUtil.showerror(MainActivity.this, "暂无");
        }
    }

    @Override
    public void onWeatherForecastSearched(LocalWeatherForecastResult localWeatherForecastResult, int i) {


    }


    private static class DemoDetails {
        private final int titleId;
        private final int descriptionId;
        private final Class<? extends android.app.Activity> activityClass;

        public DemoDetails(int titleId, int descriptionId,
                           Class<? extends android.app.Activity> activityClass) {
            super();
            this.titleId = titleId;
            this.descriptionId = descriptionId;
            this.activityClass = activityClass;
        }

    }


    private void initLa() {
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。

//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_SHOW);//只定位一次。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW);//连续定位、且将视角移动到地图中心点，定位蓝点跟随设备移动。（1秒1次定位）
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE);//连续定位、且将视角移动到地图中心点，地图依照设备方向旋转，定位点会跟随设备移动。（1秒1次定位）
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);


//        aMap.setLocationSource(this, activate(OnLocationChangedListener, listener));
//        deactivate();

        // 设置定位监听
// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationEnabled(true);
// 设置定位的类型为定位模式，有定位、跟随或地图根据面向方向旋转几种
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tian:
                Toast.makeText(this, "北京天气晴朗", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
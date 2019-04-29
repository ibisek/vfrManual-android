package com.ibisek.vfrmanualcz;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import com.ibisek.outlanded.navigation.OrientationSensorSource;
import com.ibisek.vfrmanualcz.config.Configuration;
import com.ibisek.vfrmanualcz.data.AirportRecord;
import com.ibisek.vfrmanualcz.data.DataRepository;

import java.util.Date;
import java.util.List;

public class NearestActivity extends AppCompatActivity implements LocationListener {

    static public final int REQUEST_LOCATION = 1;
    static public final int GPS_UPDATE_MIN_TIME = 10000; // [ms]
    static public final int GPS_UPDATE_MIN_DIST = 10; // [m]

    private LocationManager locationManager;
    private AirportListItemAdapter listItemAdapter;
    private MyOrientationListener myOrientationListener;

    private void initListView() {
        final ListView listView = findViewById(R.id.nearestListView);
        listItemAdapter = new AirportListItemAdapter(this);
        listItemAdapter.setWithDistAndDir(true);
        listView.setAdapter(listItemAdapter);
        listView.setOnItemClickListener(new AirportListOnClickListener(this, listItemAdapter));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearest);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // force screen on

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // enable BACK button
        getSupportActionBar().setTitle(R.string.nearest_title);   // ugly'n'dirty

        myOrientationListener = new MyOrientationListener();

        initListView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED )
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            // we cannot do much without GPS -> go back to main screen
            Intent intent = new Intent(NearestActivity.this, MainActivity.class);
            startActivity(intent);
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_UPDATE_MIN_TIME, GPS_UPDATE_MIN_DIST, this);

        boolean gpsEnabled = checkGpsEnabled();

        if(gpsEnabled) {
            Toast toast = Toast.makeText(this, R.string.nearest_welcome_toast, Toast.LENGTH_LONG);
            toast.show();
        }


        OrientationSensorSource.getInstance(this).addListener(myOrientationListener);
        OrientationSensorSource.getInstance(this).resume();
    }

    private boolean checkGpsEnabled() {
        boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!gpsEnabled)
            new AlertDialog.Builder(this)
                    .setMessage(R.string.gps_settings_text)
                    .setPositiveButton(R.string.btn_enable, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            NearestActivity.this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton(R.string.btn_cancel,null)
                    .show();

        return gpsEnabled;
    }

    protected void onPause() {
        super.onPause();

        if(locationManager != null) locationManager.removeUpdates(this);

        OrientationSensorSource.getInstance(this).removeListener(myOrientationListener);
        OrientationSensorSource.getInstance(this).pause();
    }

    @Override
    public void onLocationChanged(Location loc) {
        System.out.println("LOC:"+loc.toString());

        double lat = Math.toRadians(loc.getLatitude());
        double lon = Math.toRadians(loc.getLongitude());
//        double lat = Math.toRadians(49.3886800);
//        double lon = Math.toRadians(16.1097653);

        List<AirportRecord> records = DataRepository.getInstance(this).findNearest(lat,lon, Configuration.LRU_LIST_MAX_LEN);

        listItemAdapter.setValues(records);
        listItemAdapter.setCurrentLocation(lat, lon);
        listItemAdapter.notifyDataSetChanged();
    }

    public void onStatusChanged(String s, int i, Bundle bundle) {
        // This method was deprecated in API level Q.
        // This callback will never be invoked.
    }

    public void onProviderEnabled(String s) {
        // called when the user has enabled the provider
    }

    public void onProviderDisabled(String s) {
        // called when the user has disabled the provider
    }

    private class MyOrientationListener implements SensorEventListener {

        private long prevSensorChangeTs = 0;

        @Override
        public void onAccuracyChanged(Sensor arg0, int arg1) {
            // not used
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            long now = new Date().getTime(); // [ms]
            if(now - prevSensorChangeTs > 1000) { // once per 2 seconds at most (!)
                float heading = Math.round(event.values[0]); // 0-360 deg
                //System.out.println("## deviceHeading: "+ deviceHeading);
                listItemAdapter.setDeviceHeading(heading);

                prevSensorChangeTs = now;
            }
        }
    }

}

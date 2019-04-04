package com.ibisek.vfrmanualcz;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.ibisek.vfrmanualcz.config.Configuration;
import com.ibisek.vfrmanualcz.data.AirportRecord;
import com.ibisek.vfrmanualcz.data.DataRepository;

import java.util.List;

public class NearestActivity extends AppCompatActivity implements LocationListener {

    static public final int REQUEST_LOCATION = 1;
    static public final int GPS_UPDATE_MIN_TIME = 1000; // [ms]
    static public final int GPS_UPDATE_MIN_DIST = 1; // [m]

    private LocationManager locationManager;
    private AirportListItemAdapter listItemAdapter;

    private void initListView() {
        final ListView listView = findViewById(R.id.nearestListView);
        listItemAdapter = new AirportListItemAdapter(this);
        listView.setAdapter(listItemAdapter);
        listView.setOnItemClickListener(new AirportListOnClickListener(this, listItemAdapter));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearest);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // enable BACK button
        getSupportActionBar().setTitle(R.string.nearest_title);   // ugly'n'dirty

        initListView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED )
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_UPDATE_MIN_TIME, GPS_UPDATE_MIN_DIST, this);
    }

    protected void onPause() {
        super.onPause();

        if(locationManager != null) locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location loc) {
        System.out.println("LOC:"+loc.toString());

        double lat = Math.toRadians(loc.getLatitude());
        double lon = Math.toRadians(loc.getLongitude());
//        double lat = Math.toRadians(49.3886800);
//        double lon = Math.toRadians(16.1097653);

        List<AirportRecord> records = DataRepository.getInstance(this).findNearest(lat,lon, Configuration.LRU_LIST_MAX_LEN);
        for(AirportRecord record : records) {
            System.out.println("XXX: "+record);
        }

        listItemAdapter.setValues(records);
        listItemAdapter.notifyDataSetChanged();
    }

    public void onStatusChanged(String s, int i, Bundle bundle) {
        System.out.println("## STATUS CHANGED;" + s + ";" + i);
        // nix
    }

    public void onProviderEnabled(String s) {
        System.out.println("## PROV ENABLED" + s);
        // nix
    }

    public void onProviderDisabled(String s) {
        System.out.println("## PROV DISABLED" + s);
        // nix
    }

}

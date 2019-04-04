package com.ibisek.vfrmanualcz;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import com.ibisek.vfrmanualcz.data.AirportRecord;

public class AirportListOnClickListener implements AdapterView.OnItemClickListener {

    private Activity activity;
    private AirportListItemAdapter listItemAdapter;

    public AirportListOnClickListener(Activity activity, AirportListItemAdapter listItemAdapter) {
        this.activity = activity;
        this.listItemAdapter = listItemAdapter;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        System.out.println("LIST ITEM CLICKED:" + position);
        AirportRecord rec = listItemAdapter.values.get(position);

        if(rec.contacts.size() > 0) {    // UL fields don't have any additional info.. (!YET!) //TODO WILL HAVE SOON!!
            //Intent mapIntent = new Intent(MainActivity.this, MapViewActivity.class);
            Intent mapIntent = new Intent(activity, SwipeActivity.class);
            mapIntent.putExtra("airportCode", rec.code);
            mapIntent.putExtra("returnToActivity", activity.getClass().getCanonicalName());
            activity.startActivity(mapIntent);
        }
    }
}

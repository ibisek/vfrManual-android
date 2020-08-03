package com.ibisek.vfrmanualcz;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import com.ibisek.vfrmanualcz.config.Configuration;
import com.ibisek.vfrmanualcz.data.AirportRecord;

import java.util.List;

public class AirportListOnClickListener implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

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

        // add clicked item to top of the main list while in NearestActivity:
        if (listItemAdapter.getContext() instanceof NearestActivity) {
            List<AirportRecord> records = Configuration.getInstance(listItemAdapter.getContext()).getLastRecentlyUsedSearchResults();
            addValueToBeginningOfList(records, rec);
            Configuration.getInstance(listItemAdapter.getContext()).saveLastRecentlyUsedSearchResults(records);
        }

        if(rec.contacts.size() > 0) {    // UL fields don't have any additional info.. (!YET!) //TODO WILL HAVE SOON!! (says RLP.cz)
            //Intent mapIntent = new Intent(MainActivity.this, MapViewActivity.class);
            Intent mapIntent = new Intent(activity, SwipeActivity.class);
            mapIntent.putExtra("airportCode", rec.code);
            mapIntent.putExtra("returnToActivity", activity.getClass().getCanonicalName());
            activity.startActivity(mapIntent);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        System.out.println("LIST ITEM <LONG> CLICKED:" + position);
        AirportRecord rec = listItemAdapter.values.get(position);

        if (listItemAdapter.getContext() instanceof MainActivity) { // cannot be removed in nearest landables list
            listItemAdapter.values.remove(position);
            listItemAdapter.notifyDataSetChanged();
        }

        // TODO send freq to radio instead (if KRT2BT is in list of BT devices)

        return true;
    }

    private void addValueToBeginningOfList(List<AirportRecord> records, AirportRecord record) {
        // if already present.. move it to the beginning:
        if (records.contains(record)) {
            if (records.indexOf(record) > 0) {
                records.remove(record);
                records.add(0, record);
            }
        } else { // add to the beginning:
            records.add(0, record);
        }
    }

}

package com.ibisek.vfrmanualcz.swipe;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

/**
 * Created by jaja on 25.3.18.
 */

public class SwipeAdapter extends FragmentStatePagerAdapter {

    private final static String TAG = SwipeAdapter.class.getName();

    private String airportCode;

    public SwipeAdapter(FragmentManager fm, String airportCode) {
        super(fm);
        this.airportCode = airportCode;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment frag = null;

        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        bundle.putString("airportCode", airportCode);

        if(position == 0 || position == 1) {
            frag = new MapFragment();
            frag.setArguments(bundle);

        } else if(position == 2) {
            frag = new TextInfoFragment();
            frag.setArguments(bundle);

        } else {
            Log.e(TAG, String.format("Unsupported swipe screen #%s", position));
            return null;
        }

        return frag;
    }

    @Override
    public int getCount() {
        return 3;
    }
}

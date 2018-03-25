package com.ibisek.vfrmanualcz.swipe;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ibisek.vfrmanualcz.R;

public class TextInfoFragment extends Fragment {

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        int position = bundle.getInt("position");
        String airportCode = bundle.getString("airportCode");

        view = inflater.inflate(R.layout.fragment_text_info, container, false);

        //TODO load text info

        return view;
    }

}

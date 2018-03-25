package com.ibisek.vfrmanualcz.swipe;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.chrisbanes.photoview.PhotoView;
import com.ibisek.vfrmanualcz.R;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by jaja on 25.3.18.
 */

public class MapFragment extends Fragment {

    private final static String TAG = MapFragment.class.getName();

    private enum MapType {ADC, VOC};

    View view;
    PhotoView photoView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        int position = bundle.getInt("position");
        String airportCode = bundle.getString("airportCode");

        view = inflater.inflate(R.layout.fragment_map, container, false);
        photoView = view.findViewById(R.id.mapView);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        if(position == 0) {
            setImage(MapType.ADC, airportCode);
//            toolbar.setTitle(R.string.menu_adc_long);   //TODO tady toto nefunguje! -> proto ugly'n'dirty
        } else if(position == 1) {
            setImage(MapType.VOC, airportCode);
//            toolbar.setTitle(R.string.menu_voc_long);     //TODO tady toto nefunguje! -> proto ugly'n'dirty
        } else {
            Log.w(TAG, String.format("Position cannot be '%s' at this spot.", position));
            return null;
        }

        return view;
    }

    private void setImage(MapType type, String airportCode) {
        String t = type.toString().toLowerCase();
        String filename = String.format("%s/%s_%s.jpg", t, airportCode.toLowerCase(), t);

        Drawable d;
        InputStream is = null;
        try {
            is = getActivity().getAssets().open(filename);
            d = Drawable.createFromStream(is, airportCode);
            photoView.setImageDrawable(d);

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}

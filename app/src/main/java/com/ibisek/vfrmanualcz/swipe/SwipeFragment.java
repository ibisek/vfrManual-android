package com.ibisek.vfrmanualcz.swipe;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ibisek.vfrmanualcz.R;

/**
 * Created by jaja on 25.3.18.
 */

public class SwipeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        int position = bundle.getInt("position");

//        View view = inflater.inflate(R.layout.activity_map_view, container, false);
//        TextView t = view.findViewById(R.id.text);
//        t.setText(pageNum);

//        return view;
        return null;
    }
}

package com.ibisek.vfrmanualcz;

import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class MapViewActivity extends AppCompatActivity {

    private enum MapType {ADC, VOC};

    private String code;    // airport code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // enable BACK button
        getSupportActionBar().setTitle(R.string.menu_adc_long);

        code = getIntent().getStringExtra("code");

        setImage(MapType.ADC);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mapmenu, menu);
        return true;
    }

    private void setImage(MapType type) {
        String t = type.toString().toLowerCase();
        String filename = String.format("%s/%s_%s.jpg", t, code.toLowerCase(), t);

        PhotoView photoView = findViewById(R.id.mapView);

        Drawable d;
        InputStream is = null;
        try {
            is = this.getAssets().open(filename);
            d = Drawable.createFromStream(is, code);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.action_map_adc:
                setImage(MapType.ADC);
                getSupportActionBar().setTitle(R.string.menu_adc_long);
                return true;

            case R.id.action_map_voc:
                setImage(MapType.VOC);
                getSupportActionBar().setTitle(R.string.menu_voc_long);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        System.out.println("## EVENT: " + event);
        //System.out.println("## str:" + MotionEvent.actionToString(event.getAction()));
        return super.onTouchEvent(event);
    }
}

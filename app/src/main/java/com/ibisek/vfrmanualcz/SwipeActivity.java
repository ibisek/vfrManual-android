package com.ibisek.vfrmanualcz;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.ibisek.vfrmanualcz.swipe.SwipeAdapter;

public class SwipeActivity extends AppCompatActivity {

//    Class returnToActivity = MainActivity.class;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.swipescreen_title_adc);   // ugly'n'dirty

        String airportCode = getIntent().getStringExtra("airportCode");

        // setup the swipe view:
        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(1); // cache one following item
        SwipeAdapter sa = new SwipeAdapter(getSupportFragmentManager(), airportCode);
        viewPager.setAdapter(sa);
        viewPager.setCurrentItem(0);

        viewPager.addOnPageChangeListener(new MyOnPageChangeListener());

//        if (getIntent().hasExtra("returnToActivity")) {
//            String returnToActivityStr = getIntent().getStringExtra("returnToActivity");
//            try {
//                returnToActivity =  Class.forName(returnToActivityStr);
//            } catch (ClassNotFoundException ex) {
//                System.err.println("Error when instantiating returnToActivity:" + ex.getLocalizedMessage());
//            }
//        }
    }

    // dirty'n'ugly
    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int position) {
            Toolbar toolbar = SwipeActivity.this.findViewById(R.id.toolbar);
            toolbar.setTitle("POS: "+position);
            switch (position) {
                case 0:
                    toolbar.setTitle(R.string.swipescreen_title_adc);
                    break;
                case 1:
                    toolbar.setTitle(R.string.swipescreen_title_vor);
                    break;
                case 2:
                    toolbar.setTitle(R.string.swipescreen_title_textinfo);
                    break;
                default:
                    toolbar.setTitle("Sorry vole tape error!");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            //nix
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            //nix
        }
    }
}

package com.ibisek.vfrmanualcz;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // enable BACK button

        TextView url1 = findViewById(R.id.url1a);
        TextView url2 = findViewById(R.id.url2);
        TextView url3 = findViewById(R.id.url3);
        TextView url4 = findViewById(R.id.url4);
        TextView url5 = findViewById(R.id.url5);

        MyOnClickListener ocl = new MyOnClickListener();
        url1.setOnClickListener(ocl);
        url2.setOnClickListener(ocl);
        url3.setOnClickListener(ocl);
        url4.setOnClickListener(ocl);
        url5.setOnClickListener(ocl);
    }

    private class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if(view instanceof TextView) {
                String url = "http://" + ((TextView)view).getText().toString();

                try {
                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(myIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(AboutActivity.this, "There is no browser??",  Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }

    }
}

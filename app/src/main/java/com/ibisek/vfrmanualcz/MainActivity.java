package com.ibisek.vfrmanualcz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.ibisek.vfrmanualcz.config.Configuration;
import com.ibisek.vfrmanualcz.data.AirportRecord;
import com.ibisek.vfrmanualcz.data.DataRepository;
import com.ibisek.vfrmanualcz.data.Frequency;

import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MyListItemAdapter listItemAdapter;
    private DataRepository dataRepository;
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        dataRepository = DataRepository.getInstance(MainActivity.this);

        initSearchField();
        initListView();

        gestureDetector = new GestureDetector(this, new Gesture());
    }

    private void initSearchField() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, dataRepository.getSearchStrings());
        AutoCompleteTextView searchField = findViewById(R.id.searchField);
        searchField.setThreshold(1);//will start working from first character
        searchField.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        SearchFieldEventListener listener = new SearchFieldEventListener();
        searchField.setOnItemClickListener(listener);
        searchField.setOnFocusChangeListener(listener);

        searchField.selectAll();
    }

    private void initListView() {
        final ListView listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(new MyOnClickListener());
        listItemAdapter = new MyListItemAdapter(this);
        listView.setAdapter(listItemAdapter);
    }

    @Override
    public void onPause() {
        super.onPause();
        Configuration.getInstance(this).saveLastRecentlyUsedSearchResults(listItemAdapter.values);
    }

    @Override
    public void onResume() {
        super.onResume();
        // restore recent search results:
        List<AirportRecord> records = Configuration.getInstance(this).getLastRecentlyUsedSearchResults();
        listItemAdapter.setValues(records);
    }

    @Override
    public void onStop() {
        super.onStop();
        Configuration.getInstance(this).save();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_show_about:
                Intent mapIntent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(mapIntent);
                return true;

            case R.id.action_delete_history:
                listItemAdapter.values.clear();
                listItemAdapter.notifyDataSetChanged();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // LIST click:
    private class MyOnClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            System.out.println("LIST ITEM CLICKED:" + position);
            AirportRecord rec = listItemAdapter.values.get(position);

            if(rec.runways.size() > 0) {    // UL fields don't have any additional info
                //Intent mapIntent = new Intent(MainActivity.this, MapViewActivity.class);
                Intent mapIntent = new Intent(MainActivity.this, SwipeActivity.class);
                mapIntent.putExtra("airportCode", rec.code);
                startActivity(mapIntent);
            }
        }
    }

    // SEARCH field click:
    private class SearchFieldEventListener implements AdapterView.OnItemClickListener, AdapterView.OnFocusChangeListener {

        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            AutoCompleteTextView searchField = findViewById(R.id.searchField);
            String searchStr = searchField.getText().toString();

            // hide keyboard:
            InputMethodManager inputMethodManager = (InputMethodManager) MainActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken(), 0);

            AirportRecord rec = dataRepository.findByCodeOrAlias(searchStr);
            listItemAdapter.addValueToBeginning(rec);
        }

        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if(hasFocus) {
                AutoCompleteTextView searchView = (AutoCompleteTextView) view;
//                searchView.selectAll();
                searchView.setText("");
            }

        }
    }

    private class MyListItemAdapter extends ArrayAdapter<String> {

        private Context context;
        protected List<AirportRecord> values = new ArrayList<>();

        public MyListItemAdapter(Context context) {
            super(context, android.R.layout.simple_list_item_1, new ArrayList<String>(Arrays.asList(new String[] { "xxx sem neco :)" })));
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View listItemView = null;

            if (values != null && position < values.size()) {
                AirportRecord rec = values.get(position);

                if(rec.frequencies.size() <= 1) {   // some ULs don't have freq(!)
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    listItemView = inflater.inflate(R.layout.fragment_list_item_single_freq, parent, false);

                    TextView callSign = listItemView.findViewById(R.id.callSign);
                    TextView code = listItemView.findViewById(R.id.code);
                    TextView frequency = listItemView.findViewById(R.id.frequency);
                    TextView runway = listItemView.findViewById(R.id.runway);
                    TextView runwayDim = listItemView.findViewById(R.id.runwayDim);
                    TextView elevation = listItemView.findViewById(R.id.elevation);
                    TextView circleAlt = listItemView.findViewById(R.id.circleAlt);

                    code.setText(rec.code);

                    if(rec.frequencies.size() > 0) {
                        callSign.setText(rec.frequencies.get(0).callSign);
                        frequency.setText(rec.frequencies.get(0).freq);
                    } else {    // some UL airfields use shared frequency:
                        callSign.setText("");       // RADIO
                        frequency.setText("?");     // 125.830 (CZ 2018)
                    }

                    if(rec.runways.size() > 0) {  // ULs dont' have it :|
                        runway.setText(rec.runways.get(0).directions);
                        runwayDim.setText(rec.runways.get(0).dimensions);
                    } else {
                        runway.setVisibility(View.GONE);
                        runwayDim.setVisibility(View.GONE);
                        listItemView.findViewById(R.id.rwyLabel).setVisibility(View.GONE);
                    }

                    elevation.setText(String.format("%sm", rec.elevationMeters));

                    if(rec.circleAltFt > 0) {
                        circleAlt.setText(String.format("%sft", rec.circleAltFt));
                    } else {
                        //circleAlt.setVisibility(View.GONE);
                        //listItemView.findViewById(R.id.circleAltLabel).setVisibility(View.GONE);

                        // calculate the circle altitude from elevation:
                        double caRounded = Math.ceil((rec.elevationFt + 1000) / 10.0) * 10.0;
                        circleAlt.setText(String.format("%.0fft", caRounded));
                    }

                    if(rec.runways.size() > 1) {
                        TextView runway2 = listItemView.findViewById(R.id.runway2);
                        TextView runway2Dim = listItemView.findViewById(R.id.runway2Dim);

                        runway2.setText(rec.runways.get(1).directions);
                        runway2Dim.setText(rec.runways.get(1).dimensions);

                        runway2.setVisibility(View.VISIBLE);
                        runway2Dim.setVisibility(View.VISIBLE);

                        if(rec.runways.size() > 2) {
                            TextView runway3 = listItemView.findViewById(R.id.runway3);
                            TextView runway3Dim = listItemView.findViewById(R.id.runway3Dim);

                            runway3.setText(rec.runways.get(2).directions);
                            runway3Dim.setText(rec.runways.get(2).dimensions);

                            runway3.setVisibility(View.VISIBLE);
                            runway3Dim.setVisibility(View.VISIBLE);
                        }
                    }

                } else {
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    listItemView = inflater.inflate(R.layout.fragment_list_item_multi_freq, parent, false);

                    TextView name = listItemView.findViewById(R.id.name);
                    TextView code = listItemView.findViewById(R.id.code);
                    TextView freqCallSign = listItemView.findViewById(R.id.freqCallSign);
                    TextView elevation = listItemView.findViewById(R.id.elevation);
                    TextView circleAlt = listItemView.findViewById(R.id.circleAlt);

                    name.setText(rec.name);
                    code.setText(rec.code);

                    StringBuilder sb = new StringBuilder();
                    for(Frequency f : rec.frequencies) {
                        sb.append(f.freq);
                        sb.append(" ");
                        sb.append(f.callSign);
                        sb.append("\n");
                    }
                    freqCallSign.setText(sb.toString().trim());

                    elevation.setText(String.format("%sm", rec.elevationMeters));
                    if(rec.circleAltFt == 0) {  // not specified -> hide related elements
                        circleAlt.setVisibility(View.GONE);
                        listItemView.findViewById(R.id.circleAltLabel).setVisibility(View.GONE);
                    } else {
                        circleAlt.setText(String.format("%sft", rec.circleAltFt));
                    }

                    TextView runway = listItemView.findViewById(R.id.runway);
                    TextView runwayDim = listItemView.findViewById(R.id.runwayDim);
                    runway.setText(rec.runways.get(0).directions);
                    runwayDim.setText(rec.runways.get(0).dimensions);

                    if(rec.runways.size() > 1) {
                        TextView runway2 = listItemView.findViewById(R.id.runway2);
                        TextView runway2Dim = listItemView.findViewById(R.id.runway2Dim);

                        runway2.setText(rec.runways.get(1).directions);
                        runway2Dim.setText(rec.runways.get(1).dimensions);

                        runway2.setVisibility(View.VISIBLE);
                        runway2Dim.setVisibility(View.VISIBLE);

                        if(rec.runways.size() > 2) {
                            TextView runway3 = listItemView.findViewById(R.id.runway3);
                            TextView runway3Dim = listItemView.findViewById(R.id.runway3Dim);

                            runway3.setText(rec.runways.get(2).directions);
                            runway3Dim.setText(rec.runways.get(2).dimensions);

                            runway3.setVisibility(View.VISIBLE);
                            runway3Dim.setVisibility(View.VISIBLE);
                        }
                    }
                }

            } else { // return an empty list item - data is not loaded yet
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                listItemView = inflater.inflate(R.layout.fragment_empty_list_item, parent, false);
            }

            return listItemView;
        }

        public void addValueToBeginning(AirportRecord record) {
            // if already present.. move it to the beginning:
            if (values.contains(record)) {
                if (values.indexOf(record) > 1) {
                    values.remove(record);
                    values.add(0, record);
                }
            } else { // add to the beginning:
                values.add(0, record);
                super.add(record.code);
            }
        }

        public void setValues(List<AirportRecord> newValues) {
            if (newValues != null) {
                this.values.clear();
                this.values.addAll(newValues);

                // pokud clear() vyhodi UnsupportedOperationException, tak je to tim, ze
                // kolekce/pole co je v kontruktoru super.ArrayAdapter-u nema na sobe
                // clear(!)
                super.clear();

                for (AirportRecord r : values)
                    super.add(r.code);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
//        return gestureDetector.onTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }

    class Gesture extends GestureDetector.SimpleOnGestureListener{

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            System.out.println("FLING");

            if(e1.getX() > e2.getX()) {
                System.out.println(" <<<--- ");
            } else {
                System.out.println(" --->>> ");
            }

            return false;
        }
    }
}

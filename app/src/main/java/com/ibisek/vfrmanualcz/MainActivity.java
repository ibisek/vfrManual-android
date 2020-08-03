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

import com.ibisek.vfrmanualcz.config.Configuration;
import com.ibisek.vfrmanualcz.data.AirportRecord;
import com.ibisek.vfrmanualcz.data.DataRepository;
import com.ibisek.vfrmanualcz.data.Frequency;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    protected AirportListItemAdapter listItemAdapter;
    private DataRepository dataRepository;
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        listItemAdapter = new AirportListItemAdapter(this);
        listItemAdapter.setWithDistAndDir(false);
        listView.setAdapter(listItemAdapter);

        AirportListOnClickListener l = new AirportListOnClickListener(this, listItemAdapter);
        listView.setOnItemClickListener(l);
        listView.setOnItemLongClickListener(l);
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

            case R.id.action_show_nearest:
                mapIntent = new Intent(MainActivity.this, NearestActivity.class);
                startActivity(mapIntent);
                return true;

            case R.id.action_show_other:
                mapIntent = new Intent(MainActivity.this, OtherActivity.class);
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

            // clear the text in the searchField:
            searchField.setText("");
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

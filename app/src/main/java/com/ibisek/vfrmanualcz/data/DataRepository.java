package com.ibisek.vfrmanualcz.data;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by ibisek on 19.3.18.
 */

public class DataRepository {

    private String DATA_DIR = "";

    private static final String TAG = DataRepository.class.getSimpleName();
    private Context context;
    private List<AirportRecord> records = new ArrayList<>();

    private static DataRepository instance;
    public static DataRepository getInstance(Context context) {
        if(instance == null) instance = new DataRepository(context);
        return instance;
    }

    private DataRepository(Context context) {
        this.context = context;
        loadJsonFiles();
    }

    private void loadJsonFiles() {
        // file:///android_asset/airports.json

        // FIND json files:
        String[] jsonFiles = null;
        try {
            jsonFiles = context.getResources().getAssets().list("json");
        } catch (IOException ex) {
            Log.e(TAG, "Cannot list assets: " + ex.getMessage());
        }

        if(jsonFiles == null) {
            Log.e(TAG, "Could not load any JSON files!!");
            return;
        }

        // LOAD those files:
        for(String file : jsonFiles) {
            StringBuffer sb = new StringBuffer();
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(context.getResources().getAssets().open("json/"+file)));
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }

            // PARSE json and add to records list:
            try {
                AirportRecord rec = new AirportRecord(sb.toString());
                records.add(rec);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }

        }
    }

    public String[] getSearchStrings() {

        List<String> words = new ArrayList<>();
        for(AirportRecord rec : records) {
            if(rec.nameAlias != null)
                words.add(rec.nameAlias);
            else if(rec.name != null)
                words.add(rec.name);
            words.add(rec.code);
        }

        return words.toArray(new String[words.size()]);
    }

    public AirportRecord findByCodeOrAlias(String codeOrAlias) {
        if(codeOrAlias == null) return null;

        for(AirportRecord rec : records)
            if(codeOrAlias.equals(rec.code) || codeOrAlias.equals(rec.nameAlias))
                return rec;

        return null;
    }

    public AirportRecord findByCode(String code) {
        if(code == null) return null;

        for(AirportRecord rec : records)
            if(code.equals(rec.code))
                return rec;

        return null;
    }

    public List<AirportRecord> findNearest(double latitude, double longitude, int n) {
        List<AirportRecord> list = new ArrayList<>();

        for(AirportRecord rec : records)
            rec.calcDistance(latitude, longitude);

        Collections.sort(records, new DistanceComparator());

        return records.subList(0, n);
    }

    private class DistanceComparator implements Comparator<AirportRecord> {

        public int compare(AirportRecord a, AirportRecord b) {
            return (int)(a.distance - b.distance);
        }

    }
}

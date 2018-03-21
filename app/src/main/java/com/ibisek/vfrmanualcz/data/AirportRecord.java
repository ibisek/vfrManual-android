package com.ibisek.vfrmanualcz.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ibisek on 19.3.18.
 */

public class AirportRecord {

    public String name;
    public String code;
    public String nameAlias;
    public double latitude, longitude;
    public int elevationFt;
    public int elevationMeters;
    public int circleAltFt;
    public int circleAltMeters;

    public List<Frequency> frequencies = new ArrayList<>();
    public List<Runway> runways = new ArrayList<>();

    public AirportRecord(String jsonString) throws JSONException {
        if(jsonString == null) return;

        JSONObject j = new JSONObject(jsonString);
        name = j.getString("name");
        code = j.getString("code");

        if(j.has("nameAlias")) {
            nameAlias = j.getString("nameAlias");
        } else {
            nameAlias = name;
        }

        JSONArray arr = j.getJSONArray("coords");
        latitude = arr.getDouble(0);
        longitude = arr.getDouble(1);

        arr = j.getJSONArray("elev");
        elevationFt = arr.getInt(0);
        elevationMeters = arr.getInt(1);

        if(j.has("circleAlt")) {
            arr = j.getJSONArray("circleAlt");
            circleAltFt = arr.getInt(0);
            circleAltMeters = arr.getInt(1);
        }

        arr = j.getJSONArray("freq");
        for(int i =0; i<arr.length(); i++) {
            JSONArray arr2 = (JSONArray) arr.get(i);
            String callSign = arr2.getString(0);
            String freq = arr2.getString(1);

            Frequency f = new Frequency(callSign, freq);
            frequencies.add(f);
        }

        arr = j.getJSONArray("rwy");
        for(int i =0; i<arr.length(); i++) {
            JSONArray arr2 = (JSONArray) arr.get(i);
            String directions = arr2.getString(0);
            String dimensions = arr2.getString(1);

            Runway r = new Runway(directions, dimensions);
            runways.add(r);
        }
    }

    public String toString() {
        //TODO

        return "TODO";
    }

    private String listToStr(List<String> list) {
        if(list.size() == 0) return "";

        if(list.size() == 1) return list.get(0);

        StringBuilder sb = new StringBuilder();
        for(String item : list) {
            sb.append(item);
            sb.append("\n");
        }

        return sb.toString();
    }

//    public String getFreqAsStr() {
//        return listToStr(frequencies);
//    }

//    public String getRwyAsStr() {
//        return listToStr(runways);
//    }

//    public String getRwyDimAsStr() {
//        return listToStr(runwayDimensions);
//    }

}

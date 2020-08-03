package com.ibisek.vfrmanualcz.data;

import com.ibisek.outlanded.navigation.gps.GpsMath;

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
    public double latitude, longitude, latitudeRad, longitudeRad;
    public int elevationFt;
    public int elevationMeters;
    public int circleAltFt;
    public int circleAltMeters;

    public List<Frequency> frequencies = new ArrayList<>();
    public List<Runway> runways = new ArrayList<>();
    public List<Contact> contacts = new ArrayList<>();
    public List<String> procedures = new ArrayList<>();
    public String fuel, oil;

    public double distance = Double.MAX_VALUE;  // [m] last calculated distance to a point

    public AirportRecord(String jsonString) throws JSONException {
        if(jsonString == null) return;

        JSONObject j = new JSONObject(jsonString);
        if(j.has("name")) name = j.getString("name");   // some UL records don't have it
        code = j.getString("code");

        if(j.has("nameAlias")) {
            nameAlias = j.getString("nameAlias");
        } else {
            nameAlias = name;
        }

        JSONArray arr = j.getJSONArray("coords");
        latitude = arr.getDouble(0);
        longitude = arr.getDouble(1);
        latitudeRad = Math.toRadians(latitude);
        longitudeRad = Math.toRadians(longitude);

        arr = j.getJSONArray("elev");
        elevationFt = arr.getInt(0);
        elevationMeters = arr.getInt(1);

        if(j.has("circleAlt")) {
            arr = j.getJSONArray("circleAlt");
            circleAltFt = arr.getInt(0);
            circleAltMeters = arr.getInt(1);
        }

        if(j.has("freq")) {  // some UL records don't have it :|
            arr = j.getJSONArray("freq");
            for (int i = 0; i < arr.length(); i++) {
                JSONArray arr2 = (JSONArray) arr.get(i);
                String callSign = arr2.getString(0);
                String freq = arr2.getString(1);

                Frequency f = new Frequency(callSign, freq);
                frequencies.add(f);
            }
        }

        if(j.has("rwy")) {  // UL records don't have it :|
            arr = j.getJSONArray("rwy");
            for (int i = 0; i < arr.length(); i++) {
                JSONArray arr2 = (JSONArray) arr.get(i);
                String directions = arr2.getString(0);
                String dimensions = arr2.getString(1);

                Runway r = new Runway(directions, dimensions);
                runways.add(r);
            }
        }

        if(j.has("txt")) {
            JSONObject j2 = j.getJSONObject("txt");
            if(j2.has("cz")) {
                JSONObject j3 = j2.getJSONObject("cz");
                if(j3.has("proc")) {
                    JSONArray procArr = j3.getJSONArray("proc");
                    for (int i = 0; i < procArr.length(); i++) {
                        procedures.add(procArr.getString(i));
                    }
                }

                if(j3.has("contacts")) {
                    arr = j3.getJSONArray("contacts");
                    for (int i = 0; i < arr.length(); i++) {
                        Contact c = new Contact(arr.getJSONObject(i));
                        contacts.add(c);
                    }
                }
            }

            if (j2.has("fuel")) fuel = j2.get("fuel").toString();
            if (j2.has("oil")) oil = j2.get("oil").toString();
        }
    }

    public String toString() {
//        public String name;
//        public String code;
//        public String nameAlias;
//        public double latitude, longitude;
//        public int elevationFt;
//        public int elevationMeters;
//        public int circleAltFt;
//        public int circleAltMeters;

        StringBuilder sb = new StringBuilder("#");
        sb.append(AirportRecord.class.getName());
        sb.append(":\n code:");
        sb.append(code);
        sb.append("\n name:");
        sb.append(name);
        sb.append("\n lat:"); sb.append(latitude); sb.append("; lon:"); sb.append(longitude);
        sb.append("\n elevation :"); sb.append(elevationMeters); sb.append("m; "); sb.append(elevationFt); sb.append("ft");
        sb.append("\n circle :"); sb.append(circleAltMeters); sb.append("m; "); sb.append(circleAltFt); sb.append("ft");
        sb.append("\n dist:");
        sb.append(distance);

        return sb.toString();
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

    /**
     * @param latitude [rad]
     * @param longitude [rad]
     */
    public void calcDistance(double latitude, double longitude) {
        distance = GpsMath.getDistanceInKm(this.latitudeRad, this.longitudeRad, latitude, longitude);
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

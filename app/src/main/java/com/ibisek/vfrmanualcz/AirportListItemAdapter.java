package com.ibisek.vfrmanualcz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ibisek.vfrmanualcz.data.AirportRecord;
import com.ibisek.vfrmanualcz.data.Frequency;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AirportListItemAdapter extends ArrayAdapter<String> {

    private Context context;
    protected List<AirportRecord> values = new ArrayList<>();

    public AirportListItemAdapter(Context context) {
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

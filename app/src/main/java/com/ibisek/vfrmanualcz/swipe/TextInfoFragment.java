package com.ibisek.vfrmanualcz.swipe;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ibisek.vfrmanualcz.R;
import com.ibisek.vfrmanualcz.data.AirportRecord;
import com.ibisek.vfrmanualcz.data.Contact;
import com.ibisek.vfrmanualcz.data.DataRepository;

public class TextInfoFragment extends Fragment {

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        int position = bundle.getInt("position");
        String airportCode = bundle.getString("airportCode");

        view = inflater.inflate(R.layout.fragment_text_info, container, false);


        AirportRecord rec = DataRepository.getInstance(getContext()).findByCode(airportCode);

        // PROCEDURES:
        StringBuilder sb = new StringBuilder();
        int numProcedures = rec.procedures.size();
        for(int i=0; i<numProcedures; i++) {
            sb.append(rec.procedures.get(i));
            if(i < numProcedures - 1) sb.append("\n\n");
        }

        TextView procedures = view.findViewById(R.id.procedures);
        procedures.setText(sb.toString());


        // CONTACTS:
        sb = new StringBuilder();
        for(Contact c : rec.contacts) {
            if(c.name != null) {
                sb.append(c.name);
            }
            if(c.phone != null) {
                sb.append("\n  ");
                sb.append(c.phone);
            }
            if(c.mail != null) {
                sb.append("\n  ");
                sb.append(c.mail);
            }
            sb.append("\n\n");
        }
        TextView contacts = view.findViewById(R.id.contacts);
        contacts.setText(sb.toString());

        return view;
    }

}

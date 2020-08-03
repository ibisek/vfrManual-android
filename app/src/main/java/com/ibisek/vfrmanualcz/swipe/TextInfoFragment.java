package com.ibisek.vfrmanualcz.swipe;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.text.util.Linkify;
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
        for (int i = 0; i < rec.contacts.size(); i++) {
            Contact c = rec.contacts.get(i);

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

            if (i < rec.contacts.size()-1)
                sb.append("\n\n");
        }
        TextView contacts = view.findViewById(R.id.contacts);
        contacts.setText(sb.toString());

        Linkify.addLinks(contacts, Linkify.ALL);
        stripUnderlines(contacts);

        // SERVICES / FUEL & OIL availability:
        sb = new StringBuilder();
        sb.append(String.format("%s:\n  %s\n", getResources().getString(R.string.textinfo_services_fuel), (rec.fuel == null ? '?' : rec.fuel)));
        sb.append(String.format("%s:\n  %s", getResources().getString(R.string.textinfo_services_oil), (rec.oil == null ? '?' : rec.oil)));
        TextView services = view.findViewById(R.id.services);
        services.setText(sb.toString());

        return view;
    }

    private void stripUnderlines(TextView textView) {
        Spannable s = new SpannableString(textView.getText());
        URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
        for (URLSpan span: spans) {
            int start = s.getSpanStart(span);
            int end = s.getSpanEnd(span);
            s.removeSpan(span);
            span = new URLSpanNoUnderline(span.getURL());
            s.setSpan(span, start, end, 0);
        }
        textView.setText(s);
    }

    private class URLSpanNoUnderline extends URLSpan {
        public URLSpanNoUnderline(String url) {
            super(url);
        }
        @Override public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }
    }
}

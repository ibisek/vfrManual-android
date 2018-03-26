package com.ibisek.vfrmanualcz.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ibisek on 26.3.18.
 */

public class Contact {

    public String name;
    public String phone;
    public String mail;

    public Contact(String name, String phone, String mail) {
        this.name = name;
        this.phone = phone;
        this.mail = mail;
    }

    public Contact(JSONObject j) throws JSONException {
        if(j.has("name")) name = j.getString("name");
        if(j.has("phone")) phone = j.getString("phone");
        if(j.has("mail")) mail = j.getString("mail");
    }

}

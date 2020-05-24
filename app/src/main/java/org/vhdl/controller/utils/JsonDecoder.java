package org.vhdl.controller.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Iterator;

public class JsonDecoder {
    public Date timeNow;
    public String timeNowStr, fromID, toID, action;
    public JSONObject query;
    public String type;
    public String[] querys;

    public JsonDecoder(String jsonStr) throws JSONException {
        JSONObject wholeJson = new JSONObject(jsonStr);
        this.timeNowStr = wholeJson.getString("time");
        this.fromID = wholeJson.getString("from");
        this.toID = wholeJson.getString("to");
        this.action = wholeJson.getString("action");
        this.query = wholeJson.getJSONObject("query");

        this.type = this.query.getString("type");
        if(this.query.length() > 1){
            querys = new String[this.query.length() - 1];
        }
        Iterator<String> iterator = this.query.keys();
        iterator.next();
        while (iterator.hasNext()){
            String the_num = iterator.next();
            querys[Integer.parseInt(the_num) - 1 ] = this.query.getString(the_num);
        }
        timeNow = new Date(Long.parseLong(this.timeNowStr));
    }
}

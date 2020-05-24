package org.vhdl.controller.utils;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class JsonMaker {
    private Date timeNow;
    private String fromID, toID, action;
    private JSONObject query;
    private String type;
    private String[] querys;

    public JsonMaker(){
        this.fromID = "";
        this.toID = "";
        this.action = "";
        this.query = new JSONObject();
        this.type = "";
        this.querys = null;
    }

    public void setID(@Nullable String fromID, @Nullable String toID){
        if(fromID == null)this.fromID = "";
        else this.fromID = fromID;
        if(toID == null)this.toID = "";
        else this.toID = toID;
    }

    public void setAction(String action){
        this.action = action;
    }

    public void setType(@Nullable String type){
        if(type == null)this.type = "";
        else this.type = type;
    }

    public void setQuerys(@Nullable String[] query_list){
        this.querys = query_list;
    }

    private JSONObject makeJSON() throws JSONException {
        JSONObject result = new JSONObject();
        Long timestamp = System.currentTimeMillis();
        result.put("time", timestamp);
        result.put("from", fromID);
        result.put("to", toID);
        result.put("action", action);
        this.query.put("type", type);
        if(this.querys != null){
            for(int i = 0; i < this.querys.length; i++){
                this.query.put(String.valueOf(i + 1), this.querys[i]);
            }
        }
        result.put("query", this.query);
        return result;
    }

    public JSONObject getJsonObj() throws JSONException {return makeJSON();}

    public String getJsonStr() throws JSONException {return makeJSON().toString();}
}

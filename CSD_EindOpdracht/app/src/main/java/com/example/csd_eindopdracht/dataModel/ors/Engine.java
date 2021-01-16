package com.example.csd_eindopdracht.dataModel.ors;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Engine implements Serializable {

    public String version;
    public String buildDate;
    public String graphDate;

    public Engine (JSONObject jsonObject){
        try {
            this.version = jsonObject.getString("version");
            this.buildDate = jsonObject.getString("build_date");
            this.graphDate = jsonObject.getString("graph_date");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

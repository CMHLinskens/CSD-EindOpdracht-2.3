package com.example.csd_eindopdracht.dataModel.ors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Feature implements Serializable {

    public double[] bbox;
    public String type;
    public Property property;
    public Geometry geometry;

    public Feature (JSONObject jsonObject){
        try {
            this.bbox = jsonArrayToArray(jsonObject.getJSONArray("bbox"));
            this.type = jsonObject.getString("type");
            this.property = new Property(jsonObject.getJSONObject("properties"));
            this.geometry = new Geometry(jsonObject.getJSONObject("geometry"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public double[] jsonArrayToArray(JSONArray array){
        final double[] bboxArray = new double[4];
        for (int jsonArrayIndex = 0; jsonArrayIndex < array.length(); jsonArrayIndex++){
            try {
                bboxArray[jsonArrayIndex] = array.getDouble(jsonArrayIndex);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return bboxArray;
    }
}

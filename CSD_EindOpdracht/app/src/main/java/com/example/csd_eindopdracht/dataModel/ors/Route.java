package com.example.csd_eindopdracht.dataModel.ors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class Route implements Serializable {
    public ArrayList<Feature> features;
    public double[] bbox;
    public Metadata metadata;

    public Route(JSONObject jsonObject){
        try{
            this.features = jsonArrayToList(jsonObject.getJSONArray("features"));
            this.bbox = jsonArrayToArray(jsonObject.getJSONArray("bbox"));
            this.metadata = new Metadata(jsonObject.getJSONObject("metadata"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Feature> jsonArrayToList(JSONArray array){
        ArrayList<Feature> list = new ArrayList<>();
        for (int arrayIndex = 0; arrayIndex < array.length(); arrayIndex++){
            try {
                list.add(new Feature(array.getJSONObject(arrayIndex)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
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

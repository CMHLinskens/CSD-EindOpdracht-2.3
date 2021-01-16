package com.example.csd_eindopdracht.dataModel.ors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class Query implements Serializable {
    public ArrayList<double[]> coordinates;
    public String profile;
    public String format;

    public Query (JSONObject jsonObject){
        try {
            this.coordinates = jsonArrayToList(jsonObject.getJSONArray("coordinates"));
            this.profile = jsonObject.getString("profile");
            this.format = jsonObject.getString("format");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<double[]> jsonArrayToList(JSONArray array){
        ArrayList<double[]> list = new ArrayList<>();
        for (int arrayIndex = 0; arrayIndex < array.length(); arrayIndex++){
            try {
                list.add(jsonArrayToArray(array.getJSONArray(arrayIndex)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public double[] jsonArrayToArray(JSONArray array){
        final double[] coordinateArray = new double[2];
        for (int jsonArrayIndex = 0; jsonArrayIndex < array.length(); jsonArrayIndex++){
            try {
                coordinateArray[jsonArrayIndex] = array.getDouble(jsonArrayIndex);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return coordinateArray;
    }
}

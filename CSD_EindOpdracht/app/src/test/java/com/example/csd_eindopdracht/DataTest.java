package com.example.csd_eindopdracht;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;

import com.example.csd_eindopdracht.dataModel.Data;
import com.example.csd_eindopdracht.dataModel.collectable.Collectable;
import com.example.csd_eindopdracht.dataModel.wayPoint.WayPoint;
import com.example.csd_eindopdracht.ui.SplashScreenActivity;
import com.example.csd_eindopdracht.util.Factory;
import com.example.csd_eindopdracht.util.RandomCardListener;
import com.example.csd_eindopdracht.util.YugiohFactory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Equals;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

public class DataTest {
    Context context;
    Factory factory;
    SharedPreferences sharedPrefs;
    AssetManager assetManager;

    @Before
    public void initialise() {
        factory = new YugiohFactory();
        sharedPrefs = Mockito.mock(SharedPreferences.class);
        context = Mockito.mock(Context.class);
        assetManager = Mockito.mock(AssetManager.class);
        Mockito.when(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPrefs);
        Mockito.when(context.getAssets()).thenReturn(assetManager);
        Mockito.when(new Gson().fromJson(anyString(), any())).thenReturn(new ArrayList<>(Arrays.asList("46986414", "89631139")));

        Data.INSTANCE.retrieveAllData(context, factory);
    }

    @Test
    public void getRandomCard_Test() {
        Data.INSTANCE.getRandomCardWithLevel(5, newCollectable -> {
            Assert.assertEquals(5, newCollectable.getLevel());
        });
    }

    @Test
    public void getWayPoints_Test() {
        List<WayPoint> wayPoints = new ArrayList<>();

        try {
            JSONArray wayPointsJsonArray = new JSONArray(Data.INSTANCE.getJsonFromAssets(new SplashScreenActivity(), "waypoints.json"));

            for(int j = 0; j < wayPointsJsonArray.length(); j++){
                wayPoints.add(factory.createCachePoint(wayPointsJsonArray.getJSONObject(j)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Assert.assertEquals(3, wayPoints.size());
        Assert.assertEquals("Stadhuis", wayPoints.get(0).getName());
    }
}

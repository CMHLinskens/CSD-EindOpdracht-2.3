package com.example.csd_eindopdracht.util;

import androidx.fragment.app.Fragment;

import com.example.csd_eindopdracht.dataModel.collectable.Collectable;
import com.example.csd_eindopdracht.dataModel.wayPoint.CachePoint;
import com.example.csd_eindopdracht.dataModel.wayPoint.WayPoint;
import com.example.csd_eindopdracht.ui.fragment.CardDetailFragment;
import com.example.csd_eindopdracht.ui.fragment.InventoryFragment;
import com.example.csd_eindopdracht.ui.fragment.MapFragment;
import com.example.csd_eindopdracht.ui.fragment.RewardFragment;
import com.example.csd_eindopdracht.ui.fragment.WayPointDetailFragment;
import com.google.gson.JsonObject;

import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import kotlin.NotImplementedError;

public abstract class Factory {

    public abstract Collectable createCollectable(String name, String imgLink, int level, String id, String description);
    public abstract Collectable createCollectable(JSONObject jsonObject);
    public abstract CachePoint createCachePoint(String name, GeoPoint location);
    public abstract CachePoint createCachePoint(JSONObject jsonObject);

    public Fragment createWayPointDetailFragment(){
        return new WayPointDetailFragment();
    }

    public Fragment createInventoryFragment(){
        return new InventoryFragment();
    }

    public Fragment createRewardFragment(Collectable newCollectable){
        return new RewardFragment(newCollectable);
    }

    public Fragment createMapFragment(){
        return new MapFragment();
    }

    public Fragment createCardDetailFragment(Collectable collectable) { return new CardDetailFragment(collectable); }
}

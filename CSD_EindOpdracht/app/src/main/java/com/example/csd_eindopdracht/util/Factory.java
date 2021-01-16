package com.example.csd_eindopdracht.util;

import androidx.fragment.app.Fragment;

import com.example.csd_eindopdracht.dataModel.collectable.Collectable;
import com.example.csd_eindopdracht.dataModel.wayPoint.CachePoint;
import com.example.csd_eindopdracht.ui.fragment.InventoryFragment;
import com.example.csd_eindopdracht.ui.fragment.MapFragment;
import com.example.csd_eindopdracht.ui.fragment.RewardFragment;
import com.example.csd_eindopdracht.ui.fragment.WayPointDetailFragment;

import org.osmdroid.util.GeoPoint;

public abstract class Factory {

    public abstract Collectable createCollectable(String name, String imgLink, boolean isInInventory);
    public abstract CachePoint createCachePoint(String name, GeoPoint location, Collectable collectable);

    public Fragment createWayPointDetailFragment(){
        return new WayPointDetailFragment();
    }

    public Fragment createInventoryFragment(){
        return new InventoryFragment();
    }

    public Fragment createRewardFragment(){
        return new RewardFragment();
    }

    public Fragment createMapFragment(){
        return new MapFragment();
    }
}

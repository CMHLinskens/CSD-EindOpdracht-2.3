package com.example.csd_eindopdracht.util;

import com.example.csd_eindopdracht.dataModel.collectable.Collectable;
import com.example.csd_eindopdracht.dataModel.collectable.YugiohCollectable;
import com.example.csd_eindopdracht.dataModel.wayPoint.CachePoint;
import com.example.csd_eindopdracht.dataModel.wayPoint.YugiohCachePoint;

import org.osmdroid.util.GeoPoint;

public class YugiohFactory extends Factory{

    @Override
    public Collectable createCollectable(String name, String imgLink, boolean isInInventory) {
        return new YugiohCollectable(name,  imgLink, isInInventory);
    }

    @Override
    public CachePoint createCachePoint(String name, GeoPoint location, Collectable collectable) {
        return new YugiohCachePoint(name, location, collectable);
    }
}

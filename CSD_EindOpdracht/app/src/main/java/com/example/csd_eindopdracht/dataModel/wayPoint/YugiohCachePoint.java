package com.example.csd_eindopdracht.dataModel.wayPoint;

import com.example.csd_eindopdracht.dataModel.collectable.Collectable;
import org.osmdroid.util.GeoPoint;

public class YugiohCachePoint extends CachePoint {

    public YugiohCachePoint(String name, GeoPoint location, Collectable collectable) {
        super(name, location, collectable);
    }
}

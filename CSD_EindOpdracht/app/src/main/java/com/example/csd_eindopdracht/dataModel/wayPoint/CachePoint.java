package com.example.csd_eindopdracht.dataModel.wayPoint;

import org.osmdroid.util.GeoPoint;

public abstract class CachePoint extends WayPoint {
    public CachePoint(String name, GeoPoint location) {
        super(name, location);
    }
}

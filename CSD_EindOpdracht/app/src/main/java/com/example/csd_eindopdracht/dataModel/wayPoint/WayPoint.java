package com.example.csd_eindopdracht.dataModel.wayPoint;

import org.osmdroid.util.GeoPoint;

public abstract class WayPoint {
    String name;
    GeoPoint location;

    public WayPoint(String name, GeoPoint location) {
        this.name = name;
        this.location = location;
    }
}

package com.example.csd_eindopdracht.dataModel.wayPoint;

import com.example.csd_eindopdracht.dataModel.collectable.Collectable;

import org.osmdroid.util.GeoPoint;


public abstract class CachePoint extends WayPoint {
    Collectable collectable;

    public CachePoint(String name, GeoPoint location, Collectable collectable) {
        super(name, location);
        this.collectable = collectable;
    }

    public Collectable getCollectable() {
        return collectable;
    }

    public void setCollectable(Collectable collectable) {
        this.collectable = collectable;
    }
}

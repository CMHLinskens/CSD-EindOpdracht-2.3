package com.example.csd_eindopdracht;

import com.example.csd_eindopdracht.services.LocationService;

import org.junit.Assert;
import org.junit.Test;
import org.osmdroid.util.GeoPoint;

public class LocationServiceTest {
    @Test
    public void randomLocation_Test(){
        GeoPoint location = new GeoPoint(1,1);
        GeoPoint randomPoint = LocationService.getRandomCompletionPoint(location);

        Assert.assertTrue(randomPoint.getLatitude() - location.getLatitude() > -0.0003154653 || randomPoint.getLatitude() - location.getLatitude() < 0.0003154653);
        Assert.assertTrue(randomPoint.getLongitude() - location.getLongitude() > -0.0003984365 || randomPoint.getLatitude() - location.getLatitude() < 0.0003154653);
    }
}

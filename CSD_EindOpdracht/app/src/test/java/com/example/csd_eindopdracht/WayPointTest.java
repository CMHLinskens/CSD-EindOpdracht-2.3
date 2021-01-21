package com.example.csd_eindopdracht;

import com.example.csd_eindopdracht.dataModel.collectable.Collectable;
import com.example.csd_eindopdracht.dataModel.collectable.YugiohCollectable;
import com.example.csd_eindopdracht.dataModel.wayPoint.WayPoint;
import com.example.csd_eindopdracht.dataModel.wayPoint.YugiohCachePoint;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.osmdroid.util.GeoPoint;

public class WayPointTest {
    WayPoint wayPoint;
    WayPoint stadHuis;

    @Before
    public void initialise() {
        wayPoint = new YugiohCachePoint("test", null);
        stadHuis = new YugiohCachePoint("Stadhuis", new GeoPoint(51.588750, 4.776112));
    }

    @Test
    public void createDarkMagician_Test() {
        wayPoint.setName("Stadhuis");
        wayPoint.setLocation(new GeoPoint(51.588750, 4.776112));

        Assert.assertEquals(stadHuis.getName(), wayPoint.getName());
        Assert.assertTrue(stadHuis.getLocation().distanceToAsDouble(wayPoint.getLocation()) < 0.1);
    }
}
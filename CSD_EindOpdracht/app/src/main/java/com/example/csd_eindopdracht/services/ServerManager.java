package com.example.csd_eindopdracht.services;

import com.example.csd_eindopdracht.dataModel.Data;
import com.example.csd_eindopdracht.dataModel.collectable.Collectable;
import com.example.csd_eindopdracht.dataModel.wayPoint.CachePoint;

import java.util.Random;

public enum ServerManager {
    INSTANCE;

    Random random = new Random();

    /**
     * Looks up what collectable is stored inside the given cache point
     * @param cachePoint the corresponding cache point
     * @return collectable stored in the cache point
     */
    public Collectable getCachePointCollectable(CachePoint cachePoint){
        return Data.INSTANCE.getCollectables().get(random.nextInt(Data.INSTANCE.getCollectables().size()));
        // TODO actually look up and return the stored collectable
    }
}

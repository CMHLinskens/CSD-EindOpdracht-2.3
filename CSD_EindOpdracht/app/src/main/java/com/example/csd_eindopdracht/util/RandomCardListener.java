package com.example.csd_eindopdracht.util;

import com.example.csd_eindopdracht.dataModel.collectable.Collectable;

public interface RandomCardListener {
    void onRandomCardReceived(Collectable newCollectable);
}
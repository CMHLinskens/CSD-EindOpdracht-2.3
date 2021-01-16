package com.example.csd_eindopdracht.dataModel.collectable;

public abstract class Collectable {
    String name;
    String imgLink;
    boolean isInInventory;

    public Collectable(String name, String imgLink, boolean isInInventory) {
        this.name = name;
        this.imgLink = imgLink;
        this.isInInventory = isInInventory;
    }
}

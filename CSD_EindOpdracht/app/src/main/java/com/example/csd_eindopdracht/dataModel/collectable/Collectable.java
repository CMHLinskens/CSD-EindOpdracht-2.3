package com.example.csd_eindopdracht.dataModel.collectable;

public abstract class Collectable {
    String name;
    String imgLink;

    public Collectable(String name, String imgLink) {
        this.name = name;
        this.imgLink = imgLink;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgLink() {
        return imgLink;
    }

    public void setImgLink(String imgLink) {
        this.imgLink = imgLink;
    }
}

package com.example.csd_eindopdracht.dataModel.collectable;

public abstract class Collectable {
    String name;
    String imgLink;
    int level;
    String id;


    public Collectable(String name, String imgLink, int level, String id) {
        this.name = name;
        this.imgLink = imgLink;
        this.level = level;
        this.id = id;
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

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

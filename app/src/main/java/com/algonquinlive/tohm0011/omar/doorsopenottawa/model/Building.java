package com.algonquinlive.tohm0011.omar.doorsopenottawa.model;

import android.graphics.Bitmap;



public class Building {


    // INSTANCE VARIABLES
    private int buildingId;
    private String name;
    private String address;
    private String image;
    private Bitmap bitmap;
    private String description;


    // GETTERS

    public String getAddress() {
        return address;
    }

    public String getImage() {
        return image;
    }

    public int getBuildingId() {
        return buildingId;
    }

    public String getName() {
        return name;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public String getDescription() {
        return description;
    }

    // SETTERS
    public void setAddress(String address) {
        this.address = address + " Ottawa, Ontario";
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setBuildingId(int buildingId) {
        this.buildingId = buildingId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}

package com.simpla.turnedTables.Objects;

import java.io.Serializable;

public class SearchObject implements Serializable {

    private String profName;
    private String fieldName;
    private String cityUniName;
    private String photoName;
    private String itemID;
    private double ratingNumber;
    private String title;

    public SearchObject(String profName, String fieldName, String cityUniName, String photoName, double ratingNumber, String itemID) {
        this.profName = profName;
        this.fieldName = fieldName;
        this.cityUniName = cityUniName;
        this.photoName = photoName;
        this.ratingNumber = ratingNumber;
        this.itemID = itemID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProfName() {
        return profName;
    }

    public void setProfName(String profName) {
        this.profName = profName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getCityUniName() {
        return cityUniName;
    }

    public void setCityUniName(String cityUniName) {
        this.cityUniName = cityUniName;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public double getRatingNumber() {
        return ratingNumber;
    }

    public void setRatingNumber(double ratingNumber) {
        this.ratingNumber = ratingNumber;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }
}

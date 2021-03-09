package com.simpla.turnedTables.Objects;

import java.io.Serializable;

public class NotificationObject implements Serializable {

    private UserObject object;
    private String profName;
    private String picName;
    private String ratingID;
    private String itemID;
    private long time;
    private long totalLikes;
    private long totalDislike;

    public NotificationObject(String profName, String picName, String ratingID, long time, String itemID) {
        this.profName = profName;
        this.picName = picName;
        this.ratingID = ratingID;
        this.time = time;
        this.itemID = itemID;
    }

    public UserObject getObject() {
        return object;
    }

    public void setObject(UserObject object) {
        this.object = object;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getProfName() {
        return profName;
    }

    public void setProfName(String profName) {
        this.profName = profName;
    }

    public String getPicName() {
        return picName;
    }

    public void setPicName(String picName) {
        this.picName = picName;
    }

    public String getRatingID() {
        return ratingID;
    }

    public void setRatingID(String ratingID) {
        this.ratingID = ratingID;
    }

    public long getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(long totalLikes) {
        this.totalLikes = totalLikes;
    }

    public long getTotalDislike() {
        return totalDislike;
    }

    public void setTotalDislike(long totalDislike) {
        this.totalDislike = totalDislike;
    }
}

package com.simpla.turnedTables.Objects;

public class PhotoObject {

    private String photoName;
    private int activityNumber;

    public PhotoObject(String photoName, int activityNumber) {
        this.photoName = photoName;
        this.activityNumber = activityNumber;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public int getActivityNumber() {
        return activityNumber;
    }

    public void setActivityNumber(int activityNumber) {
        this.activityNumber = activityNumber;
    }
}

package com.simpla.turnedTables.Objects;

import java.io.Serializable;

public class UserObject implements Serializable {
    private String username;
    private String uniName;
    private String fieldName;
    private String photoName;
    private long totalRatings;
    private long totalLikes;
    private long totalDislikes;
    private long totalLiked;
    private long totalDisliked;
    private String email;

    public UserObject(String username, String uniName, String fieldName, String photoName, String email) {
        this.username = username;
        this.uniName = uniName;
        this.fieldName = fieldName;
        this.photoName = photoName;
        this.email = email;
    }

    public long getTotalDislikes() {
        return totalDislikes;
    }

    public void setTotalDislikes(long totalDislikes) {
        this.totalDislikes = totalDislikes;
    }

    public long getTotalLiked() {
        return totalLiked;
    }

    public void setTotalLiked(long totalLiked) {
        this.totalLiked = totalLiked;
    }

    public long getTotalDisliked() {
        return totalDisliked;
    }

    public void setTotalDisliked(long totalDisliked) {
        this.totalDisliked = totalDisliked;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUniName() {
        return uniName;
    }

    public void setUniName(String uniName) {
        this.uniName = uniName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public long getTotalRatings() {
        return totalRatings;
    }

    public void setTotalRatings(long totalRatings) {
        this.totalRatings = totalRatings;
    }

    public long getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(long totalLikes) {
        this.totalLikes = totalLikes;
    }
}

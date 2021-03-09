package com.simpla.turnedTables.Objects;

public class CommentsObject {

    private String photoName;
    private String username;
    private String fieldName;
    private String uniName;
    private double avgRating;
    private double helpNum;
    private double diffNum;
    private double lecNum;
    private double attNum;
    private double txtNum;
    private String comment;
    private long likeNum;
    private long dislikeNum;
    private long userLikedNum;
    private long userDislikedNum;
    private long time;
    private int popularity;
    private String itemID;
    private String userID;
    private String profID;
    private boolean check;

    public CommentsObject(String userID, String photoName, String username, String fieldName, String uniName, double avgRating, double helpNum, double diffNum, double lecNum, String comment, long time) {
        this.userID = userID;
        this.photoName = photoName;
        this.username = username;
        this.fieldName = fieldName;
        this.uniName = uniName;
        this.avgRating = avgRating;
        this.helpNum = helpNum;
        this.diffNum = diffNum;
        this.lecNum = lecNum;
        this.comment = comment;
        this.time = time;
    }

    public long getUserLikedNum() {
        return userLikedNum;
    }

    public void setUserLikedNum(long userLikedNum) {
        this.userLikedNum = userLikedNum;
    }

    public long getUserDislikedNum() {
        return userDislikedNum;
    }

    public void setUserDislikedNum(long userDislikedNum) {
        this.userDislikedNum = userDislikedNum;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public String getProfID() {
        return profID;
    }

    public void setProfID(String profID) {
        this.profID = profID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getUniName() {
        return uniName;
    }

    public void setUniName(String uniName) {
        this.uniName = uniName;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }

    public double getHelpNum() {
        return helpNum;
    }

    public void setHelpNum(double helpNum) {
        this.helpNum = helpNum;
    }

    public double getDiffNum() {
        return diffNum;
    }

    public void setDiffNum(double diffNum) {
        this.diffNum = diffNum;
    }

    public double getLecNum() {
        return lecNum;
    }

    public void setLecNum(double lecNum) {
        this.lecNum = lecNum;
    }

    public double getAttNum() {
        return attNum;
    }

    public void setAttNum(double attNum) {
        this.attNum = attNum;
    }

    public double getTxtNum() {
        return txtNum;
    }

    public void setTxtNum(double txtNum) {
        this.txtNum = txtNum;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(long likeNum) {
        this.likeNum = likeNum;
    }

    public long getDislikeNum() {
        return dislikeNum;
    }

    public void setDislikeNum(long dislikeNum) {
        this.dislikeNum = dislikeNum;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}

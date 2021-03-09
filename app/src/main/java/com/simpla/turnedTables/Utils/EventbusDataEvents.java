package com.simpla.turnedTables.Utils;

import com.simpla.turnedTables.Objects.UserObject;

import java.util.ArrayList;

public class EventbusDataEvents {

    public static class chosenPhoto{
        private String photoName;

        public chosenPhoto(String photoName) {
            this.photoName = photoName;
        }

        public String getPhotoName() {
            return photoName;
        }

        public void setPhotoName(String photoName) {
            this.photoName = photoName;
        }
    }

    public static class signUpData{
        private String username;
        private String email;
        private String password;
        private String chosenPhoto;

        public signUpData(String username,String email,String password,String chosenPhoto) {
            this.username = username;
            this.email = email;
            this.password = password;
            this.chosenPhoto = chosenPhoto;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getChosenPhoto() {
            return chosenPhoto;
        }

        public void setChosenPhoto(String chosenPhoto) {
            this.chosenPhoto = chosenPhoto;
        }
    }

    public static class activityCheck{
        private int number;

        public activityCheck(int number) {
            this.number = number;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }
    }

    public static class filterType{
        private int number;
        private String cityName;
        private String uniName;
        private String fieldName;

        public filterType(int number, String cityName, String uniName, String fieldName) {
            this.number = number;
            this.cityName = cityName;
            this.uniName = uniName;
            this.fieldName = fieldName;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public String getCityName() {
            return cityName;
        }

        public void setCityName(String cityName) {
            this.cityName = cityName;
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
    }

    public static class filterTypeBack{
        private boolean fCheck;
        private ArrayList<String> cityList;
        private ArrayList<String> uniList;
        private ArrayList<String> fieldList;
        private String ratingSytle;

        public filterTypeBack(boolean fCheck, ArrayList<String> cityList, ArrayList<String> uniList, ArrayList<String> fieldList, String ratingSytle) {
            this.fCheck = fCheck;
            this.cityList = cityList;
            this.uniList = uniList;
            this.fieldList = fieldList;
            this.ratingSytle = ratingSytle;
        }

        public boolean getCheck() {
            return fCheck;
        }

        public void setCheck(boolean fCheck) {
            this.fCheck = fCheck;
        }

        public ArrayList<String> getCityList() {
            return cityList;
        }

        public void setCityList(ArrayList<String> cityList) {
            this.cityList = cityList;
        }

        public ArrayList<String> getUniList() {
            return uniList;
        }

        public void setUniList(ArrayList<String> uniList) {
            this.uniList = uniList;
        }

        public ArrayList<String> getFieldList() {
            return fieldList;
        }

        public void setFieldList(ArrayList<String> fieldList) {
            this.fieldList = fieldList;
        }

        public String getRatingSytle() {
            return ratingSytle;
        }

        public void setRatingSytle(String ratingSytle) {
            this.ratingSytle = ratingSytle;
        }
    }

    public static class userData{
        private UserObject object;

        public userData(UserObject object) {
            this.object = object;
        }

        public UserObject getObject() {
            return object;
        }

        public void setObject(UserObject object) {
            this.object = object;
        }
    }

    public static class userLikeNumber{
        private long totalNumber;

        public userLikeNumber(long totalNumber) {
            this.totalNumber = totalNumber;
        }

        public long getTotalNumber() {
            return totalNumber;
        }

        public void setTotalNumber(long totalNumber) {
            this.totalNumber = totalNumber;
        }
    }

}

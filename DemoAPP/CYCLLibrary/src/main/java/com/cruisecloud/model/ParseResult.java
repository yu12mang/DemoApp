package com.cruisecloud.model;

import java.util.ArrayList;

/**
 * Created by wand on 2017/7/7.
 */

public class ParseResult {

    public String cmd;
    public String status;

    public String value;
    public String strValue;

    public String mSSID;
    public String mPassPhrase;

    private String mSN;
    private String mMacAddr;

    private String movieLiveViewLink;

    private String recName;
    private String index;

    private ArrayList<MediaFile> videoList;
    private ArrayList<MediaFile> photoList;


    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getStrValue() {
        return strValue;
    }

    public void setStrValue(String strValue) {
        this.strValue = strValue;
    }

    public String getSSID() {
        return mSSID;
    }

    public void setSSID(String mSSID) {
        this.mSSID = mSSID;
    }

    public String getPassPhrase() {
        return mPassPhrase;
    }

    public void setPassPhrase(String mPassPhrase) {
        this.mPassPhrase = mPassPhrase;
    }

    public String getSN() {
        return mSN;
    }

    public void setSN(String mSN) {
        this.mSN = mSN;
    }

    public String getMacAddr() {
        return mMacAddr;
    }

    public void setMacAddr(String mMacAddr) {
        this.mMacAddr = mMacAddr;
    }

    public String getMovieLiveViewLink() {
        return movieLiveViewLink;
    }

    public void setMovieLiveViewLink(String movieLiveViewLink) {
        this.movieLiveViewLink = movieLiveViewLink;
    }

    public String getRecName() {
        return recName;
    }

    public void setRecName(String recName) {
        this.recName = recName;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public void setVideoList(ArrayList<MediaFile> lists){
        videoList = lists;
    }
    public void setPhotoList(ArrayList<MediaFile> lists){
        photoList = lists;
    }

    public ArrayList<MediaFile> getVideoList(){
        return videoList;
    }
    public ArrayList<MediaFile> getPhotoList(){
        return photoList;
    }

    @Override
    public String toString() {
        return "ParseResult{" +
                "cmd='" + cmd + '\'' +
                ", status='" + status + '\'' +
                ", value='" + value + '\'' +
                ", strValue='" + strValue + '\'' +
                ", mSSID='" + mSSID + '\'' +
                ", mPassPhrase='" + mPassPhrase + '\'' +
                ", mSN='" + mSN + '\'' +
                ", mMacAddr='" + mMacAddr + '\'' +
                '}';
    }
}

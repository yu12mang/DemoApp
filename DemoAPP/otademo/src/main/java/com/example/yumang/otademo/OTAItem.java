package com.example.yumang.otademo;

/**
 * Created by Administrator on 2017/7/18.
 */

public class OTAItem {

    private String appVersion;
    private String desc;
    private String sum;
    private String FWUrl;
    private String FWVersion;
    private String time;
    private String fileSize;



    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getSum() {
        return sum;
    }

    public void setSum(String sum) {
        this.sum = sum;
    }

    public String getFWUrl() {
        return FWUrl;
    }

    public void setFWUrl(String FWUrl) {
        this.FWUrl = FWUrl;
    }

    public String getFWVersion() {
        return FWVersion;
    }

    public void setFWVersion(String FWVersion) {
        this.FWVersion = FWVersion;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }
    @Override
    public String toString() {
        return "OTAItem{" +
                "appVersion='" + appVersion + '\'' +
                ", desc='" + desc + '\'' +
                ", sum='" + sum + '\'' +
                ", FWUrl='" + FWUrl + '\'' +
                ", FWVersion='" + FWVersion + '\'' +
                ", time='" + time + '\'' +
                ", fileSize='" + fileSize + '\'' +
                '}';
    }

}

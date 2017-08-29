package com.cruisecloud.model;

import com.cruisecloud.cckit.CCKitUnit;

/**
 * Created by wand on 2017/7/7.
 */

public class DeviceModel {

    private int type = 655;

    private int deviceMode = CCKitUnit.MODE_DEV_AP;
    private int statusMode = CCKitUnit.MODE_MOVIE;

    private static DeviceModel device;

    private DeviceModel() {
    }

    public static DeviceModel getInstance() {
        if (device == null) {
            synchronized (DeviceModel.class) {
                if (device == null) {
                    device = new DeviceModel();
                }
            }
        }

        return device;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getDeviceMode() {
        return deviceMode;
    }

    public void setDeviceMode(int deviceMode) {
        this.deviceMode = deviceMode;
    }

    public int getStatusMode() {
        return statusMode;
    }

    public void setStatusMode(int statusMode) {
        this.statusMode = statusMode;
    }
}

package com.cruisecloud.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wand on 2017/7/7.
 */

public class MediaFile {

    public String name; // video:2017_0707_205423.MOV, photo:
    public String path;
    public String fPath;
    public String date;
    public String time;
    public String duration; // (for video)
    public String attr;
    public String resolution;

    public int size;
    public int timeCode;
    public int position;
    public int flag = -1; // -1 end, 0 video, 1 photo, 5 date(just flag date item)
    public int status = 0; // 0 unread, 1 read, 2 no record (just for video)

    public boolean lock = false; // (for video)
    public boolean select = false;

    public Bitmap bitmap = null;


}

package com.cruisecloud.cckit;

/**
 * Created by wand on 2017/7/3.
 */

public class CCKitUnit {

    public static final int MODE_DEV_AP      = 0;
    public static final int MODE_DEV_STATION = 1;

//    public static final int MODE_PHOTO    = 0; // no use
    public static final int MODE_MOVIE    = 1;
    public static final int MODE_PLAYBACK = 2;

    public static final int ENUM_VIDEO = 0;
    public static final int ENUM_PHOTO = 1;
    public static final int ENUM_DATE  = 5;
    public static final int ENUM_VALID = -1;

    public static String VIDEO_DIR = null;
    public static String PHOTO_DIR = null;

    public static final String IP           = "192.168.1.254";
    public static final String CUSTOM_KEY   = "custom";
    public static final String CMD_KEY      = "cmd";
    public static final String PARAM_KEY    = "par";
    public static final String STR_KEY      = "str";
    public static final String CUSTOM_VALUE = "1";

    public static final String URL_CMD         = "http://" + IP + "/?";
    public static final String URL_FILE        = "http://" + IP;
    public static final String URL_FILE_SUFFIX = "/?custom=1&cmd=4001";

    public static String LIVEVIEW_LINK = "rtsp://192.168.1.254/xxx.mov";


    public static final int RTSP_CMD_PARAM_STOP  = 0;
    public static final int RTSP_CMD_PARAM_START = 1;

    public static final int RTSP_CMD_TYPE_LIVEVIEW = 2015;


}

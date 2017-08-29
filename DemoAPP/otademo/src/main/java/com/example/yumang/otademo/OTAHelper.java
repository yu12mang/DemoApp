package com.example.yumang.otademo;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017/7/18.
 */

public class OTAHelper {

    private final static String TAG_PROD_MOD = "productmodel";
    private final static String TAG_APP_VER  = "android_Version";//App Version
    private final static String TAG_DESC     = "description";
    private final static String TAG_SUM      = "summary";
    private final static String TAG_URL      = "url";
    private final static String TAG_VER      = "version";
    private final static String TAG_TIME     = "updateDate";
    private final static String TAG_F_SIZE   = "file_size";

    public static final String[] OTA_VER_URL = {"https://myota.in-dash-ota-generic.com","http://in-dash-ota-generic-backup.com",
            "https://dn4kljvp8f.execute-api.us-east-1.amazonaws.com/Production/"};

    public static OTAItem getOTAVersion(){

        OTAItem item = null;
        for (String url :OTA_VER_URL){
            String result = HttpUtil.requestByGet(url);
            if (!result.equals("request fail")){
                item = parseJSONData(result);
                break;
            }
        }
        return item;
    }
    private static OTAItem parseJSONData(String data) {

        OTAItem item = new OTAItem();
        try {
            JSONObject jsonObject = new JSONObject(data);
            if (jsonObject.has(TAG_PROD_MOD) && !jsonObject.get(TAG_PROD_MOD).equals("null")) {
                JSONObject prod = jsonObject.getJSONObject(TAG_PROD_MOD);
                if (prod != null) {
                    item.setAppVersion(prod.getString(TAG_APP_VER)) ;
                    item.setDesc(prod.getString(TAG_DESC));
                    item.setSum(prod.getString(TAG_SUM));
                    item.setFWUrl(prod.getString(TAG_URL));
                    item.setFWVersion(prod.getString(TAG_VER));
                    item.setTime(prod.getString(TAG_TIME));
                    item.setFileSize(prod.getString(TAG_F_SIZE));
                     return item;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return item;
    }



}

package com.cruisecloud.util;

import android.content.Context;
import android.os.Environment;

import com.cruisecloud.cckit.CCKitUnit;
import com.cruisecloud.library.R;
import com.yanzhenjie.nohttp.InitializationConfig;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.OkHttpNetworkExecutor;
import com.yanzhenjie.nohttp.cache.DiskCacheStore;

import java.io.File;

/**
 * Created by wand on 2017/7/13.
 */

public class LocalUtil {

    public static void initNoHttp(Context context) {
        DiskCacheStore cache = new DiskCacheStore(getCacheDir(context));
        NoHttp.initialize(InitializationConfig.newBuilder(context)
                .connectionTimeout(10 * 1000) // default 10s
                .readTimeout(10 * 1000)
                .cacheStore(cache)
                .networkExecutor(new OkHttpNetworkExecutor())
                .build());
    }

    public static String getCacheDir(Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {

            File externalCacheDir = context.getExternalCacheDir();
            if (externalCacheDir != null) {
                return externalCacheDir.getAbsolutePath();
            }
        }

        return context.getCacheDir().getAbsolutePath();
    }

    public static void initLocalDir(Context context, String appName) {
//        File fileDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        if(fileDir == null){
//            fileDir = new File(context.getFilesDir(), "Pictures");
//            if(!fileDir.exists()){
//                fileDir.mkdir();
//            }
//            CCLog.i("*******warning****** getExternalFilesDir DIRECTORY_PICTURES == null");
//        }
//        if(fileDir == null){
//            throw new IllegalArgumentException("The local Pictures Directory can't be null.");
//        }
//        CCKitUnit.PHOTO_DIR = fileDir.getAbsolutePath();
//        CCLog.i("CCKit localDir:"+CCKitUnit.PHOTO_DIR);

        String sdDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        File appDir = new File(sdDir + File.separator + appName);
        if(!appDir.exists()){
            appDir.mkdir();
        }
        File fileDir = new File(appDir.getAbsolutePath() + "/Pictures");
        if(!fileDir.exists()){
            fileDir.mkdir();
        }
        if(fileDir == null){
            throw new IllegalArgumentException("The local Pictures Directory can't be null.");
        }
        CCKitUnit.PHOTO_DIR = fileDir.getAbsolutePath();
        CCLog.i("CCKit localPhotoDir:"+CCKitUnit.PHOTO_DIR);

//        fileDir = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES);
//        if(fileDir == null){
//            fileDir = new File(context.getFilesDir(), "Movies");
//            if(!fileDir.exists()){
//                fileDir.mkdir();
//            }
//            CCLog.i("*******warning****** getExternalFilesDir DIRECTORY_MOVIES == null");
//        }
        fileDir = new File(appDir.getAbsolutePath() + "/Movies");
        if(!fileDir.exists()){
            fileDir.mkdir();
        }
        if(fileDir == null){
            throw new IllegalArgumentException("The local Movies Directory can't be null.");
        }
        CCKitUnit.VIDEO_DIR = fileDir.getAbsolutePath();
        CCLog.i("CCKit localVideoDir:"+CCKitUnit.VIDEO_DIR);
    }

    public static String createFile(Context context, String fileName) {
        File fileDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        if(fileDir == null){
            fileDir = new File(context.getFilesDir(), "Downloads");
            if(!fileDir.exists()){
                fileDir.mkdir();
            }
            CCLog.i("*******warning****** getExternalFilesDir DIRECTORY_DOWNLOADS == null");
        }
        if(fileDir == null){
            throw new IllegalArgumentException("The local Downloads Directory can't be null.");
        }

        File file = new File(fileDir, fileName);
        CCLog.i("CCKit createFile path:"+file.getAbsolutePath());

        return file.getAbsolutePath();
    }

}

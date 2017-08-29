package com.cruisecloud.cckit;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;

import com.cruisecloud.library.BuildConfig;
import com.cruisecloud.model.DeviceModel;
import com.cruisecloud.util.CCLog;
import com.cruisecloud.util.LocalUtil;
import com.yanzhenjie.nohttp.InitializationConfig;
import com.yanzhenjie.nohttp.Logger;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.cache.DiskCacheStore;
import com.yanzhenjie.nohttp.download.DownloadListener;
import com.yanzhenjie.nohttp.download.DownloadQueue;
import com.yanzhenjie.nohttp.download.DownloadRequest;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;

import java.io.File;

/**
 * Created by wand on 2017/7/3.
 */

public class CCKit {

    private DeviceModel device;

    /**
     * 请求队列
     */
    private RequestQueue  requestQueue;
    /**
     * 下载队列
     */
    private DownloadQueue downloadQueue;

    /**
     * 单例
     */
    public static CCKit getInstance() {
        return Builder.instance;
    }

    private CCKit() {
        device = DeviceModel.getInstance();
        requestQueue = NoHttp.newRequestQueue(1); // 默认创建1个请求队列，含有1个线程
    }

    /**
     * 初始化下载队列，3个并发线程
     */
    public void initDownQueue() {
        if (downloadQueue != null) {
            downloadQueue.cancelAll();
            downloadQueue.stop();
        }

        downloadQueue = NoHttp.newDownloadQueue(1);

    }

    /**
     * 创建1个请求
     */
    public Request<String> createRequest(String url, RequestMethod methodType) {
        return NoHttp.createStringRequest(url, methodType);
    }

    /**
     * 创建1个请求，默认为RequestMethod.GET
     */
    public Request<String> createRequest(String url) {
        return NoHttp.createStringRequest(url, RequestMethod.GET);
    }
    /**
     * 创建1个缩略图请求，默认为RequestMethod.GET
     */
    public Request<Bitmap> createBitmapRequest(String url) {
        return NoHttp.createImageRequest(url, RequestMethod.GET);
    }

    /**
     * 添加一个请求到请求队列
     *
     * @param requestCode 用来标志请求, 当多个请求使用同一个Listener时, 在回调方法中会返回这个what
     * @param request     请求对象
     * @param listener    结果回调对象
     */
    public <T> void addRequest(int requestCode, Request<T> request, OnResponseListener listener) {
        requestQueue.add(requestCode, request, listener);
    }

    /**
     * 添加一个请求到下载队列
     *
     * @param what     用来标志请求, 当多个请求使用同一个Listener时, 在回调方法中会返回这个what
     * @param request  请求对象
     * @param listener 结果回调对象
     */
    public <T> void addDownloadRequest(int what, DownloadRequest request, DownloadListener listener) {
        downloadQueue.add(what, request, listener);
    }

    /**
     * 取消这个sign标记的所有请求
     *
     * @param sign 请求的取消标志
     */
    public void cancelBySign(Object sign) {
        requestQueue.cancelBySign(sign);
    }

    /**
     * 取消队列中所有请求
     */
    public void cancelAll() {
        requestQueue.cancelAll();
    }

    /**
     * 取消下载队列中所有请求
     */
    public void cancelAllDownload() {
        downloadQueue.cancelAll();
    }

    /**
     * 停止下载队列
     */
    public void stopDownload() {
        if (downloadQueue != null) {
            downloadQueue.cancelAll();
            downloadQueue.stop();
            downloadQueue = null;
        }
    }

    /**
     * 初始化
     */
    public static void init(Context context, String appName) {
        Logger.setDebug(BuildConfig.LOG_DEBUG); // 设置NoHttp的调试模式, 开启后可看到请求过程、日志和错误信息
        Logger.setTag("###NoHttp###"); // 设置NoHttp打印Log的tag

        CCLog.setDebug(BuildConfig.LOG_DEBUG); //设置Log的调试模式
        CCLog.i("###CCKit init###");

        // init NoHttp
        LocalUtil.initNoHttp(context);

        // init local file path
        LocalUtil.initLocalDir(context, appName);
    }

    /**
     * 反初始化
     */
    public void deinit() {
        requestQueue.cancelAll();
        requestQueue.stop();
        if (downloadQueue != null) {
            downloadQueue.cancelAll();
            downloadQueue.stop();
        }
    }

    public DeviceModel getDevice() {
        return device;
    }

    private static class Builder {
        private static final CCKit instance = new CCKit();
    }
}

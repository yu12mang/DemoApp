package com.example.yumang.vlcdemo;

import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/8/10.
 */

public class MyVideoPlayer implements IVLCVout.Callback, Handler.Callback {

    //需要传递的参数
    private Context context;
    private SurfaceView mSurfaceView;
    private SurfaceHolder surfaceHolder;

    //vlc相关
    private LibVLC libvlc;
    private MediaPlayer mMediaPlayer;

    //参数
    private static String videoPath = "";
    private int mVideoWidth, mVideoHeight;

    private static MyVideoPlayer videoPlayer;


    public static void setVideoPath(String videoPath) {
        MyVideoPlayer.videoPath = videoPath;
    }


    public static MyVideoPlayer getInstance(Context context,SurfaceView mSurfaceView){
        if (videoPlayer == null){
            videoPlayer = new MyVideoPlayer(context,mSurfaceView);
        }
        return videoPlayer;
    }

    public MyVideoPlayer(Context context,SurfaceView mSurfaceView) {

        this.context = context;
        this.mSurfaceView = mSurfaceView;
        this.surfaceHolder = surfaceHolder;
//        SurfaceHolder surfaceHolder = mSurfaceView.getHolder();

    }
    public  void play() {
        releasePlayer();
        try {

            ArrayList<String> options = new ArrayList<>();
            //options.add("--aout=opensles");
            options.add("--audio-time-stretch");//启用时间拉抻音频 (默认开启)
            options.add("--no-sub-autodetect-file");//自动检测字幕文件
            options.add("--network-caching=1000");//远程文件额外增加的缓存值，以毫秒为单位
            options.add("--avcodec-hw=any");
            //options.add("-vvv");
            libvlc = new LibVLC(context, options);
            mMediaPlayer = new MediaPlayer(libvlc);
            mMediaPlayer.setEventListener(new MediaPlayer.EventListener() {
                @Override
                public void onEvent(MediaPlayer.Event event) {
                    switch (event.type) {
                        case MediaPlayer.Event.EndReached:
                            play();
                            break;

                        case MediaPlayer.Event.Playing:
                            break;

                        case MediaPlayer.Event.Paused:
                            break;

                        case MediaPlayer.Event.Stopped:
                            break;

                        case MediaPlayer.Event.PositionChanged:
                            break;

                        case MediaPlayer.Event.Buffering:
                            break;

                        default:
                            break;
                    }
                }
            });

            IVLCVout vlcVout = mMediaPlayer.getVLCVout();

            vlcVout.setVideoView(mSurfaceView);
            vlcVout.addCallback(this);
            vlcVout.attachViews();
            Media media = new Media(libvlc, Uri.parse(videoPath));
            media.setHWDecoderEnabled(false, false);
            mMediaPlayer.setMedia(media);
            Log.e("logan","videopath ="+videoPath);
            mMediaPlayer.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //释放内存
    private void releasePlayer() {
        if (libvlc == null)
            return;
        mMediaPlayer.stop();
        final IVLCVout vout = mMediaPlayer.getVLCVout();
        vout.removeCallback(this);
        vout.detachViews();
        libvlc.release();
        libvlc = null;
        mVideoWidth = 0;
        mVideoHeight = 0;
    }


    //自适应屏幕大小
    private void setSize(int width, int height) {
        mVideoWidth = width;
        mVideoHeight = height;
        if (mVideoWidth * mVideoHeight <= 1)
            return;
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getResources().getDisplayMetrics();
        int w = dm.widthPixels;
        int h = dm.heightPixels;

        boolean isPortrait = context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        if (w > h && isPortrait || w < h && !isPortrait) {
            int i = w;
            w = h;
            h = i;
        }

        float videoAR = (float) mVideoWidth / (float) mVideoHeight;
        float screenAR = (float) w / (float) h;

        if (screenAR < videoAR)
            h = (int) (w / videoAR);
        else
            w = (int) (h * videoAR);

        mSurfaceView.getHolder().setFixedSize(mVideoWidth, mVideoHeight);

        ViewGroup.LayoutParams lp = mSurfaceView.getLayoutParams();
        lp.width = w;
        lp.height = h;
        mSurfaceView.setLayoutParams(lp);
        mSurfaceView.invalidate();
    }







    @Override
    public boolean handleMessage(Message message) {
//        switch (msg.what) {
//            case SHOW_PROGRESS:
//                setSeekProgress();
//                mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 20);
//                break;
//        }
        return false;
    }



    @Override
    public void onNewLayout(IVLCVout vlcVout, int width, int height, int visibleWidth, int visibleHeight, int sarNum, int sarDen) {
        if (width * height == 0)
            return;
        setSize(width, height);
    }

    @Override
    public void onSurfacesCreated(IVLCVout vlcVout) {

        play();
    }

    @Override
    public void onSurfacesDestroyed(IVLCVout vlcVout) {

    }

    @Override
    public void onHardwareAccelerationError(IVLCVout vlcVout) {

    }


}

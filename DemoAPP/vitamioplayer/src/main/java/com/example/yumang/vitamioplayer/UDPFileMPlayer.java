package com.example.yumang.vitamioplayer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;

/**
 *
 *  接收UDP TS流实现边缓存边播放<br/>
 * 该类可以实现，但存在以下不足<br/>
 * 1、播放过程会稍微卡一下这是 由于播放时候setOnCompletionListener中方法被执行<br/>
 * 2、需要思考怎么解决调用onError方法
 * @author cuiran
 * @version 1.0.0
 */
public class UDPFileMPlayer extends Activity {

    private static final String TAG="UDPFileMPlayer";
    private io.vov.vitamio.widget.VideoView mVideoView;
    private TextView tvcache;
    private String remoteUrl;
    private String localUrl;
    private ProgressDialog progressDialog = null;
    private Thread receiveThread=null;
    /**
     * 定义了初始缓存区的大小，当视频加载到初始缓存区满的时候，播放器开始播放，
     */
    private static final int READY_BUFF = 1316 * 1024*10;

    private static final String FILE_DIR=Environment.getExternalStorageDirectory().getAbsolutePath()+"/VideoCache/";

    /**
     * 核心交换缓存区，主要是用来动态调节缓存区，当网络环境较好的时候，该缓存区为初始大小，
     * 当网络环境差的时候，该缓存区会动态增加，主要就是为了避免视频播放的时候出现一卡一卡的现象。
     */
    private static final int CACHE_BUFF = 10 * 1024;
    /**
     * 单播或组播端口
     */
    private static final int PORT = 1234;

    private boolean isready = false;
    private boolean iserror = false;
    private int errorCnt = 0;
    private int curPosition = 0;
    private long mediaLength = 0;
    private long readSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Vitamio.isInitialized(getApplicationContext());
        setContentView(R.layout.activity_video);

        findViews();
        init();
        playvideo();
    }
    /**
     * 初始化组件
     * 2013-11-21 下午2:20:10
     *
     */
    private void findViews() {
        this.mVideoView = (io.vov.vitamio.widget.VideoView) findViewById(R.id.bbvideo);
        this.tvcache = (TextView) findViewById(R.id.tv);
    }

    private void init() {
        remoteUrl = "rtsp://192.168.1.254/xxx.mov";

        if (this.remoteUrl == null) {
            finish();
            return;
        }

//		this.localUrl = intent.getStringExtra("cache");
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            public void onPrepared(MediaPlayer mediaplayer) {
                Log.i(TAG, "onPrepared");
                dismissProgressDialog();
                mVideoView.seekTo(curPosition);
                mediaplayer.start();
            }
        });

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            public void onCompletion(MediaPlayer mediaplayer) {
                Log.i(TAG, "onCompletion"+localUrl);
//				curPosition = 0;
                if(localUrl.endsWith("1.mp4")){
                    localUrl=localUrl.replace("1.mp4", "2.mp4");
                    mVideoView.setVideoPath(localUrl);
                    mVideoView.start();
                }else if(localUrl.endsWith("2.mp4")){
                    localUrl=localUrl.replace("2.mp4", "3.mp4");
                    mVideoView.setVideoPath(localUrl);
                    mVideoView.start();
                }else{
                    localUrl=localUrl.replace("3.mp4", "1.mp4");
                    mVideoView.setVideoPath(localUrl);
                    mVideoView.start();
                }

            }
        });

        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            public boolean onError(MediaPlayer mediaplayer, int i, int j) {
                Log.i(TAG, "onError");
                iserror = true;
                errorCnt++;
                mVideoView.pause();
                showProgressDialog();
                return true;
            }
        });
        mVideoView.setVideoPath(remoteUrl);
        mVideoView.start();
    }

    private void showProgressDialog() {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if (progressDialog == null) {
                    progressDialog = ProgressDialog.show(UDPFileMPlayer.this,
                            "视频缓存", "正在努力加载中 ...", true, false);
                }
            }
        });
    }

    private void dismissProgressDialog() {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
            }
        });
    }
    /**
     * 播放视频
     * 2013-11-21 下午2:20:34
     *
     */
    private void playvideo() {


        showProgressDialog();

        receiveThread=new Thread(new Runnable() {

            @Override
            public void run() {
                FileOutputStream out = null;
                DatagramSocket dataSocket=null;
                DatagramPacket dataPacket=null;
                try {
                    dataSocket = new DatagramSocket(PORT);
                    byte[] receiveByte = new byte[8192];
                    dataPacket = new DatagramPacket(receiveByte, receiveByte.length);
                    Log.i(TAG, "UDP服务启动...");
                    if (localUrl == null) {
                        localUrl = FILE_DIR+"1.mp4";
                    }
                    Log.i(TAG, "localUrl="+localUrl);
                    File cacheFile = new File(localUrl);
                    Log.e("local file ==",localUrl);

                    if (!cacheFile.exists()) {
                        cacheFile.getParentFile().mkdirs();
                        cacheFile.createNewFile();
                    }

                    out = new FileOutputStream(cacheFile, true);
                    int size = 0;
                    long lastReadSize = 0;
                    int number=0;

                    int fileNum=0;
//					mHandler.sendEmptyMessage(VIDEO_STATE_UPDATE);

                    while(size==0){
                        // 无数据，则循环
                        dataSocket.receive(dataPacket);
                        size = dataPacket.getLength();
                        if (size > 0) {
                            try {
                                if(readSize>=READY_BUFF){
                                    fileNum++;

                                    switch(fileNum%3){
                                        case 0:
                                            out=new FileOutputStream(FILE_DIR+"1.mp4");
                                            break;
                                        case 1:
                                            out=new FileOutputStream(FILE_DIR+"2.mp4");
                                            break;
                                        case 2:
                                            out=new FileOutputStream(FILE_DIR+"3.mp4");
                                            break;
                                    }

                                    readSize=0;
                                    if (!isready) {
                                        mHandler.sendEmptyMessage(CACHE_VIDEO_READY);
                                    }
                                }
                                out.write(dataPacket.getData(), 0, size);
                                out.flush();
                                readSize += size;
                                size = 0;// 循环接收


                            } catch (Exception e) {
                                Log.e(TAG, "出现异常0",e);
                            }

                        }else{
                            Log.i(TAG, "TS流停止发送数据");
                        }

                    }

                    mHandler.sendEmptyMessage(CACHE_VIDEO_END);
                } catch (Exception e) {
                    Log.e(TAG, "出现异常",e);
                } finally {
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException e) {
                            //
                            Log.e(TAG, "出现异常1",e);
                        }
                    }

                    if (dataSocket != null) {
                        try {
                            dataSocket.close();
                        } catch (Exception e) {
                            Log.e(TAG, "出现异常2",e);
                        }
                    }
                }

            }
        });
        receiveThread.start();
    }

    private final static int VIDEO_STATE_UPDATE = 0;
    /**
     * 缓存准备
     */
    private final static int CACHE_VIDEO_READY = 1;
    /**
     * 缓存修改
     */
    private final static int CACHE_VIDEO_UPDATE = 2;
    /**
     * 缓存结束
     */
    private final static int CACHE_VIDEO_END = 3;
    /**
     * 缓存播放
     */
    private final static int CACHE_VIDEO_PLAY = 4;

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case VIDEO_STATE_UPDATE:
                    boolean isPlay=mVideoView.isPlaying();
                    Log.i(TAG, "更新显示 isPlay="+isPlay);
                    double cachepercent = readSize * 100.00 / mediaLength * 1.0;
                    String s = String.format("已缓存: [%.2f%%]", cachepercent);
                    if (isPlay) {
                        curPosition = (int)mVideoView.getCurrentPosition();
                        int duration = (int)1000;
                        duration = duration == 0 ? 1 : duration;

                        double playpercent = curPosition * 100.00 / duration * 1.0;

                        int i = curPosition / 1000;
                        int hour = i / (60 * 60);
                        int minute = i / 60 % 60;
                        int second = i % 60;

                        s += String.format(" 播放: %02d:%02d:%02d [%.2f%%]", hour,
                                minute, second, playpercent);
                    }
//
//				tvcache.setText(s);
                    tvcache.setVisibility(View.GONE);
                    mHandler.sendEmptyMessageDelayed(VIDEO_STATE_UPDATE, 1000);



                    break;

                case CACHE_VIDEO_READY:
                    Log.i(TAG, "缓存准备");
                    isready = true;
                    mVideoView.setVideoPath(localUrl);
                    mVideoView.start();

                    break;

                case CACHE_VIDEO_UPDATE:
                    Log.i(TAG, "缓存修改"+iserror);
                    if (iserror) {
                        mVideoView.setVideoPath(localUrl);
                        mVideoView.start();
                        iserror = false;
                    }
                    break;

                case CACHE_VIDEO_END:
                    Log.i(TAG, "缓存结束"+iserror);
                    if (iserror) {

                        mVideoView.setVideoPath(localUrl);
                        mVideoView.start();
                        iserror = false;
                    }
                    break;
                case CACHE_VIDEO_PLAY:
                    Log.i(TAG, "CACHE_VIDEO_PLAY");
                    mVideoView.setVideoPath(localUrl);
                    mVideoView.start();
                    mHandler.sendEmptyMessageDelayed(CACHE_VIDEO_PLAY, 5000);
                    break;
            }

            super.handleMessage(msg);
        }
    };

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        if(mVideoView!=null){
            mVideoView.stopPlayback();
        }
        super.onDestroy();
    }


}

package com.example.yumang.vitamioplayer;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Locale;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.VideoView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnPreparedListener, SeekBar.OnSeekBarChangeListener{

    private final String TEST_URL = "http://192.168.1.254/CARDV/MOVIE/2017_0810_183129_007.MOV";

    //ui相关


    private final static String TAG = "LOGAN_TAG";
    //常量
    private final static int PLAYER_START = 0X1001;
    private final static int PLAYER_PAUSE = 0X1003;
    private final static int PLAYER_STOP = 0X1004;
    //ui相关
    private VideoView mVideoView;
    private TextView mCurTime, mTotalTime;
    private ImageButton btnPlay;
    private SeekBar seekBar;
    private RelativeLayout videoLayout = null;

    //参数
    private String curPlayTime = "";
    private String videoFilePath = "";
    private int pos = 0;

    private int curProgress = 0;
    private int lockPosition = 0;

    private boolean isPlaying = false;//是否在播放
    private boolean isTouching = false;//是否在播放
    private boolean isVideoEnd = false;//是否播放完成
    private boolean hasChangedStatus = false;

    private Handler progressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case PLAYER_START: {
                    Log.e(TAG, "Video start");
                    progressHandler.post(progressRunnable);
                    mVideoView.start();
                    btnPlay.setBackgroundResource(R.mipmap.bg_btn_stop);
                    isPlaying = true;
                }

                break;
                case PLAYER_PAUSE: {
                    Log.e(TAG, "Video pause");
                    progressHandler.removeCallbacks(progressRunnable);
                    mVideoView.pause();
                    btnPlay.setBackgroundResource(R.mipmap.bg_btn_play);
                    isPlaying = false;
                }

                break;
                case PLAYER_STOP: {

                    progressHandler.removeCallbacks(progressRunnable);
                    seekBar.setProgress(0);
                    mCurTime.setText("00:00");
                    btnPlay.setBackgroundResource(R.mipmap.bg_btn_play);
                    isPlaying = false;
                }

                break;

                default:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Vitamio.isInitialized(getApplicationContext());

        setContentView(R.layout.activity_main);
        initView();
        initPlayer(TEST_URL);


    }

    /*
    初始化UI
     */
    private void initView() {
        mVideoView = (VideoView) findViewById(R.id.buffer);
        mCurTime = (TextView) findViewById(R.id.txt_time_current);
        mTotalTime = (TextView) findViewById(R.id.txt_time_total);
        btnPlay = (ImageButton) findViewById(R.id.play_btn);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        videoLayout = (RelativeLayout) findViewById(R.id.ijk_layout);




        btnPlay.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(this);
        videoLayout.setOnClickListener(this);

        Configuration configuration = getResources().getConfiguration();
        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setPortraitView();
        } else if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setLandscapeView();
        }

    }

    /*
    初始化播放器
     */
    private void initPlayer(String path) {
        if (path.equals("")) {
            Toast.makeText(MainActivity.this, "地址为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        mVideoView.setVideoPath(path);
        mVideoView.requestFocus();

        mVideoView.setOnInfoListener(MainActivity.this);
        mVideoView.setBufferSize(1024);
        mVideoView.setOnBufferingUpdateListener(MainActivity.this);
        mVideoView.setOnPreparedListener(MainActivity.this);
//        mVideoView.setOnTouchListener(this);
//        mVideoView.setLongClickable(true);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.play_btn: {

                if (isVideoEnd) {

                    mVideoView.seekTo(0);
                    progressHandler.sendEmptyMessage(PLAYER_START);
                } else {

                    progressHandler.sendEmptyMessage(isPlaying ? PLAYER_PAUSE : PLAYER_START);
                }

            }

            break;

            default:
                break;
        }

    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                progressHandler.sendEmptyMessage(PLAYER_PAUSE);
//                if (mVideoView.isPlaying()) {
//                    mVideoView.pause();
//
//                }
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                progressHandler.sendEmptyMessage(PLAYER_START);
                break;
            case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
                break;
        }
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.e(TAG, "video Onprepare");
        mp.setPlaybackSpeed(1.0f);
        progressHandler.post(progressRunnable);
        int total = (int) mVideoView.getDuration();
        seekBar.setMax(total);
        mTotalTime.setText(FormatTime(total));
    }

    private Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {


            if (isPlaying && !mVideoView.isBuffering()) {

                if (mVideoView.isPlaying()) {
                    if (lockPosition != 0) {
                        mVideoView.seekTo(lockPosition);
                        seekBar.setProgress(lockPosition);
                        lockPosition = 0;
                    }
                }

                if (!isTouching) {
                    curProgress = (int) mVideoView.getCurrentPosition();
                    Log.e(TAG, "" + curProgress);
                    curPlayTime = FormatTime(curProgress);
                    seekBar.setProgress(curProgress);
                    mCurTime.setText(curPlayTime);
                }

                if (curProgress == mVideoView.getDuration()) {
                    progressHandler.sendEmptyMessage(PLAYER_STOP);
                    isVideoEnd = true;
                } else {
                    isVideoEnd = false;
                }

            }
            progressHandler.postDelayed(progressRunnable, 100);
        }
    };

    private Runnable animationRunnable = new Runnable() {
        @Override
        public void run() {

            Configuration configuration = getResources().getConfiguration();
            if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            } else if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            }


        }
    };
    private Runnable orientationRunnable = new Runnable() {
        @Override
        public void run() {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        }
    };


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        if (fromUser) {
            mCurTime.setText(FormatTime(progress));
        }


    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isTouching = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar mseekBar) {


        isTouching = false;
        mVideoView.seekTo(mseekBar.getProgress());
        mVideoView.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {

                progressHandler.sendEmptyMessage(PLAYER_PAUSE);

            }
        });

    }

    private String FormatTime(int time) {
        time /= 1000;
        int minute = time / 60;
        int second = time % 60;
        minute %= 60;
        return String.format("%02d:%02d", minute, second);
    }

    @Override
    public void onBackPressed() {
        Configuration configuration = getResources().getConfiguration();
        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {

            finish();
        } else if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {//竖屏
            setPortraitView();
        } else {//横屏
            setLandscapeView();

        }

        progressHandler.postDelayed(animationRunnable, 200);
        super.onConfigurationChanged(newConfig);
    }


    private void setLandscapeView() {
        ViewGroup.LayoutParams lp = mVideoView.getLayoutParams();
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        mVideoView.setLayoutParams(lp);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void setPortraitView() {
        ViewGroup.LayoutParams lp = mVideoView.getLayoutParams();
        lp.height = 600;
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        mVideoView.setLayoutParams(lp);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//显示状态栏
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVideoView.pause();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (isPlaying) {
            mVideoView.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressHandler.removeCallbacks(progressRunnable);
        mVideoView = null;
    }
}

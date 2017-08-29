package com.example.yumang.vlcdemo;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class Main3Activity extends AppCompatActivity implements SurfaceHolder.Callback  {

    private static final int SHOW_PROGRESS = 0;

    private LibVLC libvlc;

    private MediaPlayer mMediaPlayer;

    private SurfaceView mSurfaceView;

    private FrameLayout mSurfaceFrame;


    private ImageButton mBtnPlay;

    private SeekBar mSeekBar;

//    private AudioManager mAudioManger;

    private Handler mHandler, timeHandler;

    private int mVideoWidth, mVideoHeight;

    private Runnable timeRunnable;

    private TextView playingTime;

    private String mMediaUrl= "rtsp://192.168.1.254/xxx.mov";
//    private String mMediaUrl= "http://192.168.1.254/CARDV/MOVIE/2017_0809_195304_016.MOV";


    private ArrayAdapter<String> adapter;

    private String clarity[] = {"普清", "高清", "流畅"};

    private float currentProgress;

    private Handler HideControlsHandler;

    private Runnable HideControlsRunnable;

    private ProgressBar progressBar;

    private MyVideoPlayer videoPlayer;
    private SurfaceHolder mSurfaceHolder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        mAudioManger = (AudioManager) getSystemService(Context.AUDIO_SERVICE);//音频控制器
        mSurfaceView = (SurfaceView) findViewById(R.id.surface);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);




        mSurfaceFrame = (FrameLayout) findViewById(R.id.player_surface_frame);
        mSeekBar = (SeekBar) findViewById(R.id.seekbar);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);//进度圈
        mSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        mBtnPlay = (ImageButton) findViewById(R.id.btn_play);
        playingTime = (TextView) findViewById(R.id.playingTime);
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, clarity);
        adapter.setDropDownViewResource(android.
                R.layout.simple_spinner_dropdown_item);

        mBtnPlay.setOnClickListener(btnClickListener);
//        mHandler = new Handler(this);
        timeHandler = new Handler();
        timeRunnable = new Runnable() {
            @Override
            public void run() {
                if (mMediaPlayer != null) {
                    long curTime = mMediaPlayer.getTime();
                    long totalTime = (long) (curTime / mMediaPlayer.getPosition());
                    int minutes = (int) (curTime / (60 * 1000));
                    int seconds = (int) ((curTime / 1000) % 60);
                    int endMinutes = (int) (totalTime / (60 * 1000));
                    int endSeconds = (int) ((totalTime / 1000) % 60);
                    String duration = String.format("%02d:%02d/%02d:%02d", minutes, seconds, endMinutes, endSeconds);
                    playingTime.setText(duration);

                }
                timeHandler.postDelayed(timeRunnable, 1000);
            }
        };

        timeRunnable.run();
        mSurfaceFrame.setClickable(true);
        mSurfaceFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOverlay();
                HideControlsHandler.postDelayed(HideControlsRunnable, 3000); //3秒后执行runnable 的run方法
            }
        });

//


        HideControlsHandler = new Handler();
        HideControlsRunnable = new Runnable() {
            @Override
            public void run() {  //2秒后执行该方法
                // handler自带方法实现定时器
                try {
                    hideOverlay();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
        HideControlsHandler.postDelayed(HideControlsRunnable, 2000); //2秒后执行runnable 的run方法


        videoPlayer = MyVideoPlayer.getInstance(this,mSurfaceView);
        videoPlayer.setVideoPath(mMediaUrl);
        videoPlayer.play();

    }
    //存储非正常情况下销毁的视频播放位置
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putFloat("currentFloat", currentProgress);
    }

    //显示控件
    private void showOverlay() {
        mSeekBar.setVisibility(View.VISIBLE);
        mBtnPlay.setVisibility(View.VISIBLE);
        playingTime.setVisibility(View.VISIBLE);
    }



    //隐藏控件
    private void hideOverlay() {
        mSeekBar.setVisibility(View.INVISIBLE);
        mBtnPlay.setVisibility(View.INVISIBLE);
        playingTime.setVisibility(View.INVISIBLE);

    }



    @Override
    protected void onStop() {
        super.onStop();
    }



    //设置进度条
    private int setSeekProgress() {
        if (mMediaPlayer == null)
            return 0;
        int max = (int) mMediaPlayer.getLength();
        int time = (int) mMediaPlayer.getTime();
        mSeekBar.setMax(max);
        mSeekBar.setProgress(time);
        return time;
    }




    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (seekBar.getId() == R.id.seekbar) {
                if (fromUser) {
                    mMediaPlayer.setTime(progress);
                    setSeekProgress();
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        //音频进度条在0.5s调试完后会自动隐藏
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };








    //自适应屏幕大小
    private void setSize(int width, int height) {
        mVideoWidth = width;
        mVideoHeight = height;
        if (mVideoWidth * mVideoHeight <= 1)
            return;

        int w = getWindow().getDecorView().getWidth();
        int h = getWindow().getDecorView().getHeight();

        boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
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
    protected void onPause() {
        super.onPause();
        currentProgress = mMediaPlayer.getPosition();
        mMediaPlayer.pause();
        mBtnPlay.setBackgroundResource(android.R.drawable.ic_media_play);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        releasePlayer();
        timeHandler.removeCallbacks(timeRunnable);
        timeRunnable = null;
        HideControlsHandler.removeCallbacks(HideControlsRunnable);
        HideControlsRunnable = null;

    }

    private View.OnClickListener btnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_play:
                    if (mMediaPlayer.isPlaying()) {
                        mMediaPlayer.pause();
                        mBtnPlay.setBackgroundResource(android.R.drawable.ic_media_play);
                    } else {
                        mMediaPlayer.play();
                        mBtnPlay.setBackgroundResource(android.R.drawable.ic_media_pause);
                    }
                    break;

            }
        }
    };

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    //监听手机屏幕上的按键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {

                case KeyEvent.KEYCODE_VOLUME_UP:
                    break;

                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    break;

                case KeyEvent.KEYCODE_BACK:
                    break;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        videoPlayer.play();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }
}

package com.example.yumang.seekbardemo;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SeekBar;

public class MainActivity extends Activity implements SeekBar.OnSeekBarChangeListener {
    private SeekBar seekbar;

    private int lastProgress = 0;
    private int newProgress = 0;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupView();
    }

    public void setupView() {
//        seekbar = (SeekBar) findViewById(R.id.seekBar1);
//            mSeekBarValue = (TextView) findViewById(R.id.seekbar_value);
//            mSeekBarStatus = (TextView) findViewById(R.id.seekbar_status);

        //设置初值
        seekbar.setMax(100);
        seekbar.setProgress(0);
        seekbar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        // TODO Auto-generated method stub
//                mSeekBarValue.setText("current value:" + progress);

        if (progress > newProgress + 100 || progress < newProgress - 100)

        {
            newProgress = lastProgress;
            seekBar.setProgress(lastProgress);
            return;
        }
        newProgress = progress;
    }

    //开始拖动
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
//                mSeekBarStatus.setText("drag start");
    }

    //结束拖动
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
//                mSeekBarStatus.setText("drag end");
        if (newProgress < 10) {
            lastProgress = 0;
            newProgress = 0;
            seekBar.setProgress(0);
        } else if (newProgress > 10 && newProgress < 25) {
            lastProgress = 25;
            newProgress = 25;
            seekBar.setProgress(25);
        } else if (newProgress > 25 && newProgress < 50) {
            lastProgress = 50;
            newProgress = 50;
            seekBar.setProgress(50);

        } else if (newProgress > 50 && newProgress < 75) {
            lastProgress = 75;
            newProgress = 75;
            seekBar.setProgress(75);
        } else if (newProgress > 75 && newProgress < 100) {
            lastProgress = 100;
            newProgress = 100;
            seekBar.setProgress(100);
        }
        changeProgressStateImg(lastProgress);
    }

    private void changeProgressStateImg(int LastProgress) {


    }
}


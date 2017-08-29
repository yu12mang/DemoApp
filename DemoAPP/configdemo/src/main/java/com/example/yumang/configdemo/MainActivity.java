package com.example.yumang.configdemo;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout layout = null;
    private boolean isLayoutFull = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration configuration = getResources().getConfiguration();
        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_main_portrait);
            layout = (RelativeLayout) findViewById(R.id.view);
        } else if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_main_landscape);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_main_portrait);
            layout = (RelativeLayout) findViewById(R.id.view);
        } else {
            setContentView(R.layout.activity_main_landscape);
        }
    }

    public void viewFullScreen(View v) {

        ViewGroup.LayoutParams lp = layout.getLayoutParams();
        lp.height = isLayoutFull ? 300 : 1920;
        lp.width = isLayoutFull ? 300 : 1080;
        layout.setLayoutParams(lp);
        layout.invalidate();
        isLayoutFull = !isLayoutFull;


    }
}

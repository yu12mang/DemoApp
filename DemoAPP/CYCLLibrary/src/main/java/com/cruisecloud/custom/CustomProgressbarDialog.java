/*
 * Copyright 2017 CruiseCloud. All Rights Reserved.
 */
package com.cruisecloud.custom;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cruisecloud.library.R;

/**
 * Created by wand on 17/07/01.
 */
public class CustomProgressbarDialog extends Dialog implements View.OnClickListener {

    private TextView txt_progress, txt_index;
    private ProgressBar progressBar;

    private StringBuilder builder;

    private OnDialogClickLister singleClickLister;

    public CustomProgressbarDialog(Context context) {
        this(context, R.style.CustomDialog, context.getString(R.string.cancel));
    }

    public CustomProgressbarDialog(Context context, int theme, String btnSingle) {
        super(context, theme);

        setContentView(R.layout.view_progress_horizontal_dialog);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
//      getWindow().getAttributes().gravity = Gravity.CENTER;

        txt_progress = (TextView) findViewById(R.id.txt_progress);
        txt_index = (TextView) findViewById(R.id.txt_index);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setProgress(0);
        Button btn_single = (Button) findViewById(R.id.btn_single);

        if (btnSingle != null) {
            btn_single.setText(btnSingle);
        }
        btn_single.setOnClickListener(this);
    }

    public void updateProgress(String hasCompleted, String total, int percent, int index, int all){
        if(builder == null)
            builder = new StringBuilder();

        builder.delete(0, builder.length());
        builder.append(hasCompleted).append("M/").append(total).append("M (").append(percent).append("%)");
        txt_progress.setText(builder.toString());

        builder.delete(0, builder.length());
        builder.append(index).append("/").append(all);
        txt_index.setText(builder.toString());

        progressBar.setProgress(percent);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btn_single)
            single_Click(this);
    }

    private void single_Click(DialogInterface dialog) {
        dialog.dismiss();
        if (singleClickLister != null) {
            singleClickLister.single_Click(dialog);
        }
    }

    public void setOnClickListener(OnDialogClickLister clickListener) {
        singleClickLister = clickListener;
    }

    public interface OnDialogClickLister {
        void single_Click(DialogInterface dialog);
    }

}

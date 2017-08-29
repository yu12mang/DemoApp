/*
 * Copyright 2017 CruiseCloud. All Rights Reserved.
 */
package com.cruisecloud.custom;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;

import com.cruisecloud.library.R;
import com.cruisecloud.util.CCLog;

/**
 * Created by wand on 17/07/01.
 */
public class CustomProgressDialog extends Dialog {

    private TextView tv_msg;



    public CustomProgressDialog(Context context) {
        this(context, R.style.CustomDialog, true, true);
    }

    public CustomProgressDialog(Context context, boolean isVertical) {
        this(context, R.style.CustomDialog, isVertical, true);
    }

    public CustomProgressDialog(Context context, boolean isVertical, boolean cancelable) {
        this(context, R.style.CustomDialog, isVertical, cancelable);
    }

    public CustomProgressDialog(Context context, int theme, boolean isVertical, boolean cancelable) {
        super(context, theme);

        if(!isVertical) {
            setContentView(R.layout.view_progress_dialog);
        }else {
            setContentView(R.layout.view_progress_dialog_vertical);
        }
        getWindow().getAttributes().gravity = Gravity.CENTER;
        setCanceledOnTouchOutside(false);
        setCancelable(cancelable);

        tv_msg = (TextView) findViewById(R.id.tv_msg);
    }

    public void setMessage(String msg){
        if(tv_msg != null){
            tv_msg.setText(msg);
        }
    }

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        if(!hasFocus){
//            dismiss();
//        }
//    }
}

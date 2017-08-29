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
import android.widget.TextView;

import com.cruisecloud.library.R;

/**
 * Created by wand on 17/07/01.
 */
public class CustomAlertDialog extends Dialog implements View.OnClickListener {

    private OnDialogSingleButtonClickLister singleClickLister;
    private OnDialogButtonClickLister       clickLister;

    public CustomAlertDialog(Context context, String content) {
        this(context, R.style.CustomDialog, null, content, context.getString(R.string.ok));
    }

    public CustomAlertDialog(Context context, String content, String btnSingle) {
        this(context, R.style.CustomDialog, null, content, btnSingle);
    }

    public CustomAlertDialog(Context context, String title, String content, String btnSingle) {
        this(context, R.style.CustomDialog, title, content, btnSingle);
    }

    public CustomAlertDialog(Context context, String title, String content, String btnLeft, String btnRight) {
        this(context, R.style.CustomDialog, title, content, btnLeft, btnRight);
    }

    public CustomAlertDialog(Context context, int theme, String title, String content, String btnSingle) {
        super(context, theme);

        setContentView(R.layout.view_dialog_singlebtn);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
//      getWindow().getAttributes().gravity = Gravity.CENTER;


        TextView txt_title = (TextView) findViewById(R.id.txt_title);
        TextView txt_content = (TextView) findViewById(R.id.txt_content);
        Button btn_single = (Button) findViewById(R.id.btn_single);

        if (title != null) {
            txt_title.setVisibility(View.VISIBLE);
            txt_title.setText(title);
        } else {
            txt_title.setVisibility(View.GONE);
        }
        if (content != null) {
            txt_content.setText(content);
        }
        if (btnSingle != null) {
            btn_single.setText(btnSingle);
        }
        btn_single.setOnClickListener(this);
    }

    public CustomAlertDialog(Context context, int theme, String title, String content, String btnLeft, String btnRight) {
        super(context, theme);

        setContentView(R.layout.view_dialog);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
//      getWindow().getAttributes().gravity = Gravity.CENTER;


        TextView txt_title = (TextView) findViewById(R.id.txt_title);
        TextView txt_content = (TextView) findViewById(R.id.txt_content);
        Button btn_left = (Button) findViewById(R.id.btn_left);
        Button btn_right = (Button) findViewById(R.id.btn_right);

        if (title != null) {
            txt_title.setVisibility(View.VISIBLE);
            txt_title.setText(title);
        } else {
            txt_title.setVisibility(View.GONE);
        }
        if (content != null) {
            txt_content.setText(content);
        }
        if (btnLeft != null) {
            btn_left.setText(btnLeft);
        }
        btn_left.setOnClickListener(this);
        if (btnRight != null) {
            btn_right.setText(btnRight);
        }
        btn_right.setOnClickListener(this);
    }

//    public void setMessage(String content) {
//        if (content != null && txt_content != null) {
//            txt_content.setText(content);
//        }
//    }

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        if(!hasFocus){
//            dismiss();
//        }
//    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btn_single)
            single_Click(this);
        if (viewId == R.id.btn_left)
            left_Click(this);
        if (viewId == R.id.btn_right)
            right_Click(this);
    }

    private void single_Click(DialogInterface dialog) {
        dialog.dismiss();
        if (singleClickLister != null) {
            singleClickLister.single_Click(dialog);
        }
    }

    private void left_Click(DialogInterface dialog) {
        dialog.dismiss();
        if (clickLister != null) {
            clickLister.leftClick(dialog);
        }
    }

    private void right_Click(DialogInterface dialog) {
        dialog.dismiss();
        if (clickLister != null) {
            clickLister.rightClick(dialog);
        }
    }

    public void setOnDialogButtonClickListener(OnDialogButtonClickLister clickListener) {
        clickLister = clickListener;
    }

    public void setOnDialogSingleButtonClickListener(OnDialogSingleButtonClickLister clickListener) {
        singleClickLister = clickListener;
    }


    public interface OnDialogSingleButtonClickLister {
        void single_Click(DialogInterface dialog);
    }

    public interface OnDialogButtonClickLister {
        void leftClick(DialogInterface dialog);

        void rightClick(DialogInterface dialog);
    }
}

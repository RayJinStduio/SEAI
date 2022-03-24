package com.rayjin.seai;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 作者 Ray
 * 时间 2021/11/29 20:06
 * 名称 UpdateDialog.java
 */
public class InfoDialog extends Dialog
{
    private InfoDialog(Context context)
    {
        super(context);
    }

    private InfoDialog(Context context, int themeResId)
    {
        super(context, themeResId);
    }

    private InfoDialog(Context context, boolean cancelable, OnCancelListener cancelListener)
    {
        super(context, cancelable, cancelListener);
    }

    /* Builder */
    public static class Builder
    {
        private final View mLayout;
        private final InfoDialog mDialog;
        private Context con;

        public Builder(Context context)
        {
            mDialog = new InfoDialog(context, R.style.custom_dialog);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mLayout = inflater.inflate(R.layout.info, null, false);
            mDialog.addContentView(mLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        public InfoDialog create()
        {
            mDialog.setContentView(mLayout);
            mDialog.setCancelable(true);
            mDialog.setCanceledOnTouchOutside(true);
            return mDialog;
        }
    }
}
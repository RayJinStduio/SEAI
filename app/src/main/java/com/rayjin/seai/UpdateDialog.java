package com.rayjin.seai;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;

/**
 * 作者 Aaron Zhao
 * 时间 2015/9/16 11:21
 * 名称 CustomDialog.java 描述
 */
public class UpdateDialog extends Dialog {

    /* Constructor */
    private UpdateDialog(Context context) {
        super(context);
    }

    private UpdateDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    private UpdateDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    /* Builder */
    public static class Builder
    {
        private BmobFile file;
        private View mLayout;
        private View.OnClickListener mButtonCancelClickListener;
        private View.OnClickListener mButtonConfirmClickListener;
        private TextView new_version, old_version, content;
        private UpdateDialog mDialog;
        private Button btnConfirm;
        private TextView btnCancel;
        private Context con;

        public Builder(Context context)
        {
            mDialog = new UpdateDialog(context, R.style.custom_dialog);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // 加载布局文件
            mLayout = inflater.inflate(R.layout.update, null, false);
            // 添加布局文件到 Dialog
            mDialog.addContentView(mLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            new_version = mLayout.findViewById(R.id.update_new);
            old_version = mLayout.findViewById(R.id.update_old);
            btnConfirm = mLayout.findViewById(R.id.update_button);
            btnCancel = mLayout.findViewById(R.id.noupdate);
            content = mLayout.findViewById(R.id.update_content);
        }

        /**
         * 设置 Dialog 标题
         */
        public Builder setnewv(String newversion)
        {
            new_version.setText(newversion);
            //tvTitle.setVisibility(View.VISIBLE);
            return this;
        }

        public Builder setcontext(Context cont)
        {
            con = cont;
            //tvTitle.setVisibility(View.VISIBLE);
            return this;
        }

        public Builder setfile(BmobFile newfile)
        {
            file = newfile;
            //tvTitle.setVisibility(View.VISIBLE);
            return this;
        }

        /**
         * 设置 Warning
         */
        public Builder setoldv(String oldversion)
        {
            //tvWarning.setText(waring);
            old_version.setText(oldversion);
            return this;
        }

        /**
         * 设置 Info
         */
        public Builder setcontent(String message)
        {
            content.setText(message);
            return this;
        }


        /**
         * 设置取消按钮文字和监听
         */
        public Builder setButtonCancel(String text, View.OnClickListener listener)
        {
            // btnCancel.setText(text);
            //mButtonCancelClickListener = listener;
            return this;
        }

        /**
         * 设置确认按钮文字和监听
         */
        public Builder setButtonConfirm(String text, View.OnClickListener listener)
        {
            //btnConfirm.setText(text);
            //mButtonConfirmClickListener = listener;
            return this;
        }

        public UpdateDialog create()
        {
            btnCancel.setOnClickListener(new android.view.View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    mDialog.dismiss();
                    //mButtonCancelClickListener.onClick(view);
                }
            });

            btnConfirm.setOnClickListener(new android.view.View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    downloadFile(file);
                    mDialog.dismiss();
                    //mButtonConfirmClickListener.onClick(view);
                }
            });

            mDialog.setContentView(mLayout);
            mDialog.setCancelable(true);
            mDialog.setCanceledOnTouchOutside(false);
            return mDialog;
        }

        private void downloadFile(BmobFile file)
        {
            //允许设置下载文件的存储路径，默认下载文件的目录为：context.getApplicationContext().getCacheDir()+"/bmob/"
            File saveFile = new File(Environment.getExternalStorageDirectory(), file.getFilename());
            file.download(saveFile, new DownloadFileListener()
            {

                @Override
                public void onStart()
                {
                    ShowToast.showToast(con, "开始下载");
                }

                @Override
                public void done(String savePath, BmobException e)
                {
                    if (e == null)
                    {
                       ShowToast.showToast(con, "下载成功,保存路径:" + savePath);
                       installApk(savePath);
                    }
                    else
                    {
                        ShowToast.showToast(con,"下载失败：" + e.getErrorCode() + "," + e.getMessage());
                    }
                }

                @Override
                public void onProgress(Integer value, long newworkSpeed)
                {
                    Log.i("bmob", "下载进度：" + value + "," + newworkSpeed);
                }

            });
        }


        public void installApk(String filePath)
        {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            File apkFile = new File(filePath);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri uri = FileProvider.getUriForFile(con, con.getPackageName() + ".fileprovider", apkFile);
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
            }
            else
            {
                intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
            }
            con.startActivity(intent);
        }
    }
}
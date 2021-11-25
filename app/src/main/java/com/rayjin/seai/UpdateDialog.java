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
 * 作者 Ray
 * 时间 2021/11/8 8:06
 * 名称 UpdateDialog.java
 */
public class UpdateDialog extends Dialog
{

    /* Constructor */
    private UpdateDialog(Context context)
    {
        super(context);
    }

    private UpdateDialog(Context context, int themeResId)
    {
        super(context, themeResId);
    }

    private UpdateDialog(Context context, boolean cancelable, OnCancelListener cancelListener)
    {
        super(context, cancelable, cancelListener);
    }

    /* Builder */
    public static class Builder
    {
        private BmobFile file;
        private final View mLayout;
        private final TextView new_version;
        private final TextView old_version;
        private final TextView content;
        private final UpdateDialog mDialog;
        private final Button btnConfirm;
        private final TextView btnCancel;
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
        public Builder SetNewVersion(String NewVersion)
        {
            new_version.setText(NewVersion);
            return this;
        }

        public Builder SetContext(Context cont)
        {
            con = cont;
            return this;
        }

        public Builder SetFile(BmobFile NewFile)
        {
            file = NewFile;
            return this;
        }

        /**
         * 设置 OldVersion
         */
        public Builder SetOldVersion(String OldVersion)
        {
            old_version.setText(OldVersion);
            return this;
        }

        /**
         * 设置 content
         */
        public Builder SetContent(String message)
        {
            content.setText(message);
            return this;
        }

        public UpdateDialog create()
        {
            btnCancel.setOnClickListener(view ->
            {
                mDialog.dismiss();
                //mButtonCancelClickListener.onClick(view);
            });

            btnConfirm.setOnClickListener(view ->
            {
                downloadFile(file);
                mDialog.dismiss();
                //mButtonConfirmClickListener.onClick(view);
            });

            mDialog.setContentView(mLayout);
            mDialog.setCancelable(true);
            mDialog.setCanceledOnTouchOutside(false);
            return mDialog;
        }

        private void downloadFile(BmobFile file)
        {
            //允许设置下载文件的存储路径，默认下载文件的目录为：context.getApplicationContext().getCacheDir()
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
                public void onProgress(Integer value, long NewWorkSpeed)
                {
                    Log.i("BOMB", "下载进度：" + value + "," + NewWorkSpeed);
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
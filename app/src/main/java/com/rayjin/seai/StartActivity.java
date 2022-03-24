package com.rayjin.seai;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.appcompat.app.AppCompatActivity;

import com.rayjin.seai.BmobClass.logcat;
import com.rayjin.seai.Utils.ShowToast;

import java.io.File;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

public class StartActivity extends AppCompatActivity
{
    String filename;
    SharedPreferences mySharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
        mySharedPreferences= getSharedPreferences("crash", Activity.MODE_PRIVATE);
        Bmob.initialize(StartActivity.this, "83363ad99170ea39b0e92cea3f713137");
        if(mySharedPreferences.getBoolean("IsCrash",false))
        {
            filename=mySharedPreferences.getString("FileName","");
            ShowToast.showToast(StartActivity.this,"上次程序异常退出，正在收集错误数据...");
            upload_crash(getExternalCacheDir().getPath()+"/"+filename);
        }
        else
        {
            handler.sendEmptyMessageDelayed(0,1000);
        }
    }

    private Handler handler = new Handler(Looper.getMainLooper())
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            Intent intent1 = new Intent(StartActivity.this, MainActivity.class);
            startActivity(intent1);
            finish();
        }
    };

    /**
     * upload crash
     */
    public void upload_crash(String path)
    {
        BmobFile bmobFile = new BmobFile(new File(path));
        bmobFile.uploadblock(new UploadFileListener()
        {
            @Override
            public void done(BmobException e)
            {
                if(e==null)
                {
                    logcat e2 = new logcat();
                    e2.setLogcat(bmobFile);
                    e2.save(new SaveListener<String>()
                    {
                        @Override
                        public void done(String objectId, BmobException e)
                        {
                            if(e==null)
                            {

                                ShowToast.showToast(StartActivity.this, "已记录错误日志");
                                SharedPreferences.Editor editor = mySharedPreferences.edit();
                                //editor.putString("FileName", "");
                                editor.putBoolean("IsCrash",false);
                                editor.apply();
                            }
                            else
                            {
                                ShowToast.showToast(StartActivity.this, "日志反馈失败：" + e.getMessage());
                            }
                            Intent intent1 = new Intent(StartActivity.this, MainActivity.class);
                            startActivity(intent1);
                            finish();
                        }
                    });
                }
                else
                {
                    ShowToast.showToast(StartActivity.this, "日志反馈失败：" + e.getMessage());
                    Intent intent1 = new Intent(StartActivity.this, MainActivity.class);
                    startActivity(intent1);
                    finish();
                }
            }
            @Override
            public void onProgress(Integer value)
            {
                // 返回的上传进度（百分比）
                //Log.d("T",value.toString());
            }
        });
    }

}
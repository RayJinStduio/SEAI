package com.rayjin.seai;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
//import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;

public class MainActivity extends AppCompatActivity
{
    TextView BtnSet;
    SlidingMenu slideView;
    ImageView signIn;
    ImageView menuButton;
    ImageView closeButton;
    View Btnanimal,Btnplant;
    TextView Btnfeedback,Btnupdate;
    Boolean isLogin;
    CircleImageView avatar;
    CircleImageView avatar2;
    TextView welcome_user;
    TextView username_bar;
    Handler mHandler;
    View carema_1;
    Thread t1;
    int task_next=-1;
    boolean ischeaking=false;
    int mMenuWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sliding_menu);
        int test=0;
        BtnSet = findViewById(R.id.BtnSet);
        BtnSet.setOnClickListener(MainOnClickListener);
        signIn = findViewById(R.id.signIn);
        signIn.setOnClickListener(MainOnClickListener);
        Btnanimal = findViewById(R.id.view5);
        Btnanimal.setOnClickListener(MainOnClickListener);
        Btnplant = findViewById(R.id.view6);
        Btnplant.setOnClickListener(MainOnClickListener);
        slideView = findViewById(R.id.slideView);
        menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(MainOnClickListener);
        closeButton = findViewById(R.id.closeButton);
        closeButton.setOnClickListener(MainOnClickListener);
        Btnfeedback = findViewById(R.id.BtnFeedback);
        Btnfeedback.setOnClickListener(MainOnClickListener);
        Btnupdate=findViewById(R.id.BtnUpgrade);
        Btnupdate.setOnClickListener(MainOnClickListener);
        avatar = findViewById(R.id.signIn);
        welcome_user = findViewById(R.id.welcome_user);
        avatar2 = findViewById(R.id.avatar2);
        username_bar = findViewById(R.id.username_bar);
        carema_1 = findViewById(R.id.view4);
        carema_1.setOnClickListener(MainOnClickListener);

        Bmob.initialize(MainActivity.this, "83363ad99170ea39b0e92cea3f713137");

        if (RApplication.Rflag&&BmobUser.isLogin())
        {
            isLogin=true;
            final String strURL;
            User u = User.getCurrentUser(User.class);
            if(u.geticons()==null)
            {
                strURL = "https://i.loli.net/2021/10/29/rDN1TO9U5StL6ja.png";
            }
            else
            {
                strURL = u.geticons().getFileUrl();
                Log.e("",u.geticons().getFileUrl());
            }

            welcome_user.setText(u.getUsername());
            username_bar.setText(u.getUsername());

                t1=new Thread(){
                    public void run(){
                        try {
                            Bitmap bitmap = getBitmap(strURL);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    avatar.setImageBitmap(bitmap);
                                    avatar2.setImageBitmap(bitmap);
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };
                t1.start();
        }
        else
        {
            isLogin=false;
        }

        IntentFilter filter = new IntentFilter(SettingActivity.action);
        registerReceiver(broadcastReceiver, filter);
    }

    public Bitmap getBitmap(String path) throws IOException {
        try {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == 200) {
                InputStream inputStream = conn.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            }
        } catch (IOException e) {
            Log.e("error",e.getMessage());
        }
        return null;
    }


   View.OnClickListener MainOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.BtnSet:
                    Intent intent1 = new Intent(MainActivity.this, SettingActivity.class);
                    startActivity(intent1);
                    break;
                case R.id.signIn:
                    if(!isLogin)
                    {
                        Intent intent2 = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent2);
                    }
                    else
                    {

                    }
                    break;
                case R.id.menuButton:
                    slideView.smoothScrollTo(0, 0);
                    break;
                case R.id.closeButton:
                    slideView.smoothScrollTo(slideView.getMenuWidth(), 0);
                    break;
                case R.id.BtnFeedback:
                    Intent intent3 = new Intent(MainActivity.this, FeedbackActivity.class);
                    intent3.putExtra("from", 0);
                    startActivity(intent3);
                    break;
                case R.id.view4:
                    //Intent intent4 = new Intent(MainActivity.this, testDemo.class);
                    //startActivity(intent4);
                    ShowToast.showToast(MainActivity.this,"功能正在开发中...");
                    break;
                case R.id.view5:
                    task_next=0;
                    requestcarema();
                    break;
                case R.id.view6:
                    task_next=1;
                    requestcarema();
                    break;
                case R.id.BtnUpgrade:
                    task_next=2;
                    //RequestInstallApk();
                    requestsuccess();
                    break;
                default:
                    break;
            }
        }
    };

    public void requestsuccess()
    {
        if(task_next==0)
        {
            Intent intent5 = new Intent(MainActivity.this, CameraActivity2.class);
            intent5.putExtra("type", 1);
            startActivity(intent5);
        }
        else if(task_next==1)
        {
            Intent intent6 = new Intent(MainActivity.this, CameraActivity2.class);
            intent6.putExtra("type", 0);
            startActivity(intent6);
        }
        else if(task_next==2)
            if(!ischeaking)
                cheakupdate();
            else
                ShowToast.showToast(MainActivity.this,"正在检测更新");
    }

    private void requestcarema()
    {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},1);
        else requestsuccess();
    }

    private void RequestInstallApk()
    {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.REQUEST_INSTALL_PACKAGES) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES},1);
        else requestsuccess();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==1)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                requestsuccess();
            }
            else
            { //拒绝权限申请
                Toast.makeText(this,"权限被拒绝了",Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void loginByAccount() {
        //此处替换为你的用户名密码
        BmobUser.loginByAccount("RayJin", "123456", new LogInListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e == null) {
                    Toast toast = Toast.makeText(MainActivity.this, "登录成功：" + user.getUsername(), Toast.LENGTH_SHORT);

                    toast.show();

                    //Snackbar.make(view, "登录成功：" + user.getUsername(), Snackbar.LENGTH_LONG).show();
                } else {
                    Toast toast = Toast.makeText(MainActivity.this, "登录失败：" + e.getMessage(), Toast.LENGTH_SHORT);

                    toast.show();

                    //Snackbar.make(view, "登录失败：" + e.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    public boolean onKeyDown(int keyCode,KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
            if(slideView.isBar())   slideView.smoothScrollTo(slideView.getMenuWidth(), 0);
            else finish();
        }
        return false;

    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getExtras().getInt("data")==0)
            {
                welcome_user.setText("未登录");
                username_bar.setText("未登录");
                avatar.setImageResource(R.drawable.default_avatar2);
                avatar2.setImageResource(R.drawable.default_avatar2);
                isLogin = false;
                avatar.setClickable(true);
            }
            else if(intent.getExtras().getInt("data")==1)
            {
                final String strURL;
                User u = User.getCurrentUser(User.class);
                if(u.geticons()==null)
                {
                     strURL = "https://i.loli.net/2021/10/29/rDN1TO9U5StL6ja.png";
                }
                else
                {
                    Log.e("",u.geticons().getFileUrl());
                    strURL = u.geticons().getFileUrl();
                }

                welcome_user.setText(u.getUsername());
                username_bar.setText(u.getUsername());

                avatar.setClickable(false);

               new Thread(){
                    public void run(){
                        try {
                            Bitmap bitmap = getBitmap(strURL);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    avatar.setImageBitmap(bitmap);
                                    avatar2.setImageBitmap(bitmap);
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
            else if(intent.getExtras().getInt("data")==2)
            {
                User u = User.getCurrentUser(User.class);
                Log.e("",u.geticons().getFileUrl());
                final String strURL = u.geticons().getFileUrl();

                new Thread(){
                    public void run(){
                        try {
                            Bitmap bitmap = getBitmap(strURL);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    avatar.setImageBitmap(bitmap);
                                    avatar2.setImageBitmap(bitmap);
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
            else if(intent.getExtras().getInt("data")==3)
            {
                final  String strURL = "https://i.loli.net/2021/10/29/rDN1TO9U5StL6ja.png";

                new Thread(){
                    public void run(){
                        try {
                            Bitmap bitmap = getBitmap(strURL);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    avatar.setImageBitmap(bitmap);
                                    avatar2.setImageBitmap(bitmap);
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        }
    };

    public void cheakupdate()
    {
        ischeaking=true;
        BmobQuery<AppVersion> query = new BmobQuery<>();
        query.setLimit(1).setSkip(0).order("-createdAt")
                .findObjects(new FindListener<AppVersion>() {
                    @Override
                    public void done(List<AppVersion> object, BmobException e) {
                        if (e == null) {
                            try
                            {
                                int AppCode = MainActivity.this.getPackageManager().
                                        getPackageInfo(MainActivity.this.getPackageName(), 0).versionCode;
                                String version_old= MainActivity.this.getPackageManager().
                                        getPackageInfo(MainActivity.this.getPackageName(), 0).versionName;
                                if (object.get(0).getversion_i() > AppCode) {
                                    //检测到有更新比对版本
                                    updatedialog(version_old,object.get(0).getversion(),
                                            object.get(0).update_log,object.get(0).path);
                                }
                                else
                                {
                                    ShowToast.showToast(MainActivity.this,"当前已为最新版本");
                                    ischeaking=false;
                                }

                            }
                            catch (PackageManager.NameNotFoundException nameNotFoundException)
                            {
                                nameNotFoundException.printStackTrace();
                            }
                        } else {
                            // ...
                        }
                    }
                });
    }

    public void updatedialog(String old_v, String new_v, String content, BmobFile file)
    {
            UpdateDialog.Builder builder = new UpdateDialog.Builder(MainActivity.this);
            builder.SetNewVersion(new_v);
            builder.SetOldVersion(old_v);
            builder.SetContent(content);
            builder.SetFile(file);
            Context c=MainActivity.this;
            builder.SetContext(c);
            builder.create().show();
            ischeaking=false;
    }

    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    };
}
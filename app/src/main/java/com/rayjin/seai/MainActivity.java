package com.rayjin.seai;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
//import android.view.*;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

public class MainActivity extends AppCompatActivity
{
    Button BtnSet,Btnlogin;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sliding_menu);
        int test=0;
        BtnSet = findViewById(R.id.BtnSet);
        BtnSet.setOnClickListener(MainOnClickListener);
        Btnlogin = findViewById(R.id.Btnlogin);
        Btnlogin.setOnClickListener(MainOnClickListener);
        Bmob.initialize(MainActivity.this, "83363ad99170ea39b0e92cea3f713137");

    }

   View.OnClickListener MainOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.BtnSet:
                    Intent intent1 = new Intent(MainActivity.this, SettingActivity.class);
                    startActivity(intent1);
                    break;
                case R.id.Btnlogin:
                    loginByAccount();
                default:
                    break;
            }
        }
    };
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
}
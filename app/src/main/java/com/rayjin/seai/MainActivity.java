package com.rayjin.seai;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
//import android.view.*;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Text;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

public class MainActivity extends AppCompatActivity
{
    TextView BtnSet,signIn;
    SlidingMenu slideView;
    ImageView menuButton;
    ImageView closeButton;
    TextView Btnfeedback;
    View Btnanimal,Btnplant;
    int mMenuWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sliding_menu);
        int test=0;
        BtnSet = findViewById(R.id.BtnSet);
        BtnSet.setOnClickListener(MainOnClickListener);
        Btnanimal = findViewById(R.id.view5);
        Btnanimal.setOnClickListener(MainOnClickListener);
        Btnplant = findViewById(R.id.view6);
        Btnplant.setOnClickListener(MainOnClickListener);
        signIn = findViewById(R.id.signIn);
        signIn.setOnClickListener(MainOnClickListener);
        slideView = findViewById(R.id.slideView);
        menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(MainOnClickListener);
        closeButton = findViewById(R.id.closeButton);
        closeButton.setOnClickListener(MainOnClickListener);
        Btnfeedback = findViewById(R.id.BtnFeedback);
        Btnfeedback.setOnClickListener(MainOnClickListener);



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
                case R.id.signIn:
                    Intent intent2 = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent2);
                    break;
                case R.id.menuButton:
                    slideView.smoothScrollTo(0, 0);
                    break;
                case R.id.closeButton:
                    slideView.smoothScrollTo(slideView.getMenuWidth(), 0);
                    break;
                case R.id.BtnFeedback:
                    Intent intent3 = new Intent(MainActivity.this, FeedbackActivity.class);
                    startActivity(intent3);
                    break;
                case R.id.view5:
                    Intent intent4 = new Intent(MainActivity.this, CameraActivity.class);
                    startActivity(intent4);
                    break;
                case R.id.view6:
                    Intent intent5 = new Intent(MainActivity.this, CameraActivity.class);
                    startActivity(intent5);
                    break;
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

    public boolean onKeyDown(int keyCode,KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
            if(slideView.isBar())   slideView.smoothScrollTo(slideView.getMenuWidth(), 0);
            else finish();
        }
        return false;

    }
}
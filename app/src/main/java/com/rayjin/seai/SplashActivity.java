package com.rayjin.seai;

import android.app.ActivityOptions;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        handler.sendEmptyMessageDelayed(0,100);


    }
    private Handler handler = new Handler(Looper.getMainLooper())
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            ImageView img=findViewById(R.id.imageView10);
            Bundle bundle =
                    ActivityOptions.makeSceneTransitionAnimation(SplashActivity.this, img, "activityTransform")
                            .toBundle();
            Intent intent = new Intent(SplashActivity.this, StartActivity.class);
            startActivity(intent, bundle);
            finish();
        }
    };
}

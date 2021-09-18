package com.rayjin.seai;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class FeedbackActivity extends AppCompatActivity
{
    EditText et;
    Button push;
    int from;
    boolean isLogin;
    User user;
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback);
        et = findViewById(R.id.feed_et);
        push = findViewById(R.id.feed_push);
        push.setOnClickListener(FeedOnClickListener);
        from=0;
        if (BmobUser.isLogin())
        {
            isLogin = true;
            user = BmobUser.getCurrentUser(User.class);
        }
        else isLogin=false;
    }
    View.OnClickListener FeedOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (v.getId()==R.id.feed_push)
            {
                if (isLogin)
                {
                    String text=et.getText().toString();
                    if(text.length()>0)
                    {
                        if(from==0)
                        {
                            feedpush(text);
                        }
                    }
                    else
                    {
                        Toast toast = Toast.makeText(FeedbackActivity.this, "请输入内容", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
                else
                {
                    Toast toast = Toast.makeText(FeedbackActivity.this, "请先登录", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        }
    };

    public void feedpush(String content)
    {
        feedback p2 = new feedback();
        p2.setUsername(user.getUsername());
        p2.setContent(content);
        p2.save(new SaveListener<String>()
        {
            @Override
            public void done(String objectId, BmobException e)
            {
                Toast toast;
                if(e==null)
                {
                    toast = Toast.makeText(FeedbackActivity.this, "反馈成功", Toast.LENGTH_SHORT);
                }else
                {
                    toast = Toast.makeText(FeedbackActivity.this, "创建数据失败：" + e.getMessage(), Toast.LENGTH_SHORT);
                }
                toast.show();
            }
        });
    }
}

package com.rayjin.seai;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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
    String error;
    byte pic[];
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback);
        et = findViewById(R.id.feed_et);
        push = findViewById(R.id.feed_push);
        push.setOnClickListener(FeedOnClickListener);
        from=0;
        if (getIntent() != null)
        {
            from = getIntent().getIntExtra("from", 0);
            if (from == 1)
            {
                et.setHint("请输入正确的结果");
                error= getIntent().getStringExtra("error");
                pic =getIntent().getByteArrayExtra("pic");
            }
        }
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

    public void errorpush(String content)
    {
        getFile(pic,FeedbackActivity.this.getExternalCacheDir().getAbsolutePath(),"temp.png");

    }

    public static void getFile(byte[] bfile, String filePath,String fileName)
    {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try
        {
            File dir = new File(filePath);
            if(!dir.exists()&&dir.isDirectory())
            {//判断文件目录是否存在
                dir.mkdirs();
            }
            file = new File(filePath+"/"+fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (bos != null)
            {
                try
                {
                    bos.close();
                } catch (IOException e1)
                {
                    e1.printStackTrace();
                }
            }
            if (fos != null)
            {
                try
                {
                    fos.close();
                } catch (IOException e1)
                {
                    e1.printStackTrace();
                }
            }
        }
    }
}

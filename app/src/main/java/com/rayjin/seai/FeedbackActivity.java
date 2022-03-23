package com.rayjin.seai;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.rayjin.seai.BmobClass.User;
import com.rayjin.seai.BmobClass.error;
import com.rayjin.seai.BmobClass.feedback;
import com.rayjin.seai.Utils.ShowToast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

public class FeedbackActivity extends AppCompatActivity
{
    boolean isupload=false;
    EditText et;
    Button push;
    int from;
    boolean isLogin;
    User user;
    String error;
    String pic;
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
                pic =getIntent().getStringExtra("pic");
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
                if (isLogin&&!isupload)
                {
                    String text=et.getText().toString();
                    if(text.length()>0)
                    {
                        isupload=true;
                        if(from==0)
                        {
                            FeedPush(text);
                        }
                        else ErrorPush(text,pic,error);
                    }
                    else
                    {
                        ShowToast.showToast(FeedbackActivity.this, "请输入内容");
                    }
                }
                else if(isLogin)
                    ShowToast.showToast(FeedbackActivity.this, "正在上传信息");
                else
                    ShowToast.showToast(FeedbackActivity.this, "请先登录");
            }
        }
    };

    public void FeedPush(String content)
    {
        feedback p2 = new feedback();
        p2.setUsername(user.getUsername());
        p2.setContent(content);
        p2.save(new SaveListener<String>()
        {
            @Override
            public void done(String objectId, BmobException e)
            {
                if(e==null)
                {
                    ShowToast.showToast(FeedbackActivity.this, "反馈成功");
                }else
                {
                    ShowToast.showToast(FeedbackActivity.this, "创建数据失败：" + e.getMessage());
                }
                isupload=false;
            }
        });
    }

    public void ErrorPush(String content,String path,String error)
    {
        final User user = BmobUser.getCurrentUser(User.class);
        BmobFile bmobFile = new BmobFile(new File(path));
        bmobFile.uploadblock(new UploadFileListener()
        {
            @Override
            public void done(BmobException e)
            {
                if(e==null)
                {
                    com.rayjin.seai.BmobClass.error e2 = new error();
                    e2.setError(error);
                    e2.setRight(content);
                    e2.setPic(bmobFile);
                    e2.save(new SaveListener<String>()
                    {
                        @Override
                        public void done(String objectId, BmobException e)
                        {
                            if(e==null)
                            {
                                ShowToast.showToast(FeedbackActivity.this, "反馈成功");
                            }else
                            {
                                ShowToast.showToast(FeedbackActivity.this, "反馈失败：" + e.getMessage());
                            }
                        }
                    });
                }
                else
                {
                    ShowToast.showToast(FeedbackActivity.this, "反馈失败：" + e.getMessage());
                    //Log.e("error", e.getMessage());toast.show();
                }
                isupload=false;
            }
            @Override
            public void onProgress(Integer value)
            {
                // 返回的上传进度（百分比）
            }
        });

    }

    public static void getFile(byte[] bfile, String filePath,String fileName)
    {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file;
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

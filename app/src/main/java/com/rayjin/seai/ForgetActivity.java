package com.rayjin.seai;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;


public class ForgetActivity extends AppCompatActivity
{
    EditText old_forget,pass1,pass2;
    Button forget_bt;
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget);
        old_forget = findViewById(R.id.old_forget);
        pass1 = findViewById(R.id.password_et_forget);
        pass2 = findViewById(R.id.password2_et_forget);
        forget_bt = findViewById(R.id.forget_bt);
        forget_bt.setOnClickListener(ForgetOnClickListener);
    }
    private final View.OnClickListener ForgetOnClickListener = v ->
    {

        if (v.getId()==R.id.forget_bt)
        {
            String old_pw = old_forget.getText().toString();
            String pw1 = pass1.getText().toString();
            String pw2 = pass2.getText().toString();
            if(old_pw.length() == 0)
            {
                Toast toast = Toast.makeText(ForgetActivity.this, "请输入原始密码", Toast.LENGTH_SHORT);
                toast.show();
            }
            else if(pw1.length() == 0)
            {
                Toast toast = Toast.makeText(ForgetActivity.this, "请输入新的密码", Toast.LENGTH_SHORT);
                toast.show();
            }
            else if(pw2.length() == 0)
            {
                Toast toast = Toast.makeText(ForgetActivity.this, "请再次输入新密码", Toast.LENGTH_SHORT);
                toast.show();
            }
            else if(!pw2.equals(pw1))
            {
                Toast toast = Toast.makeText(ForgetActivity.this, "两次密码不一致", Toast.LENGTH_SHORT);
                toast.show();
            }
            else
            {
                BmobUser.updateCurrentUserPassword(old_pw, pw1, new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        Toast toast;
                        if (e == null) {
                            toast = Toast.makeText(ForgetActivity.this, "密码修改成功", Toast.LENGTH_SHORT);
                        } else {
                            toast = Toast.makeText(ForgetActivity.this, "修改失败： " + e.getMessage(), Toast.LENGTH_SHORT);
                        }
                        toast.show();
                    }
                });
            }
        }
    };
}

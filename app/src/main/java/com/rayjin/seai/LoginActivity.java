package com.rayjin.seai;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends AppCompatActivity
{
    private Button login_bt,signup_bt;
    private EditText login_user,login_pw,signup_user,signup_pw,signup_pw2;
    private TextView signup;
    private StackCard stack;
    private ImageView login_vis,signin_vis,signin_vis2;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        login_bt = findViewById(R.id.login_bt);
        login_user = findViewById(R.id.username_et);
        login_pw = findViewById(R.id.password_et);
        login_bt.setOnClickListener(LoginOnClickListener);
        signup_bt = findViewById(R.id.signup_bt);
        signup_bt.setOnClickListener(LoginOnClickListener);
        signup_user =  findViewById(R.id.username_et_signup);
        signup_pw = findViewById(R.id.password_et_signup);
        signup_pw2 = findViewById(R.id.password2_et_signup);
        signup = findViewById(R.id.textView9);
        signup.setOnClickListener(LoginOnClickListener);
        stack = findViewById(R.id.stack);
        login_vis=findViewById(R.id.login_vis);
        login_vis.setOnClickListener(LoginOnClickListener);
        signin_vis=findViewById(R.id.signin_vis);
        signin_vis.setOnClickListener(LoginOnClickListener);
        signin_vis2=findViewById(R.id.signin_vis2);
        signin_vis2.setOnClickListener(LoginOnClickListener);
    }

    private final View.OnClickListener LoginOnClickListener = v ->
    {
        if (v.getId()==R.id.login_bt)
        {
            String pw = login_pw.getText().toString();
            String user = login_user.getText().toString();
            if(user.isEmpty())
            {
                ShowToast.showToast(LoginActivity.this, "请输入账号");
            }
            else if(pw.isEmpty())
            {
                ShowToast.showToast(LoginActivity.this, "请输入密码");
            }
            else
            {
                BmobUser.loginByAccount(user, pw, new LogInListener<User>()
                {
                    @Override
                    public void done(User user, BmobException e)
                    {
                        if (e == null)
                        {
                            ShowToast.showToast(LoginActivity.this, "登录成功：" + user.getUsername());
                            Intent intent = new Intent("rayjin.broadcast.action");
                            intent.putExtra("data", 1);
                            sendBroadcast(intent);
                            finish();
                        }
                        else
                        {
                            ShowToast.showToast(LoginActivity.this, "登录失败：" + e.getMessage());
                        }
                    }
                });
            }
        }
        else if(v.getId() == R.id.signup_bt)
        {
            String user=signup_user.getText().toString();
            String pw=signup_pw.getText().toString();
            String pw2=signup_pw2.getText().toString();
            if(user.isEmpty())
            {
                ShowToast.showToast(LoginActivity.this, "请输入账号");
            }
            else if(pw.isEmpty())
            {
                ShowToast.showToast(LoginActivity.this, "请输入密码");
            }
            else if(pw2.isEmpty())
            {
                ShowToast.showToast(LoginActivity.this, "请再输入一次密码");
            }
            else if(!pw.equals(pw2))
            {
                ShowToast.showToast(LoginActivity.this, "两次密码输入不一致");
            }
            else
            {
                final User user2 = new User();
                user2.setUsername(user);
                user2.setPassword(pw);
                user2.signUp(new SaveListener<User>() {
                    @Override
                    public void done(User user, BmobException e) {
                        if (e == null) {
                            ShowToast.showToast(LoginActivity.this, "注册成功");
                            Intent intent = new Intent("rayjin.broadcast.action");
                            intent.putExtra("data", 1);
                            sendBroadcast(intent);
                            finish();
                        } else {
                            ShowToast.showToast(LoginActivity.this, "注册失败"+ e.getMessage());
                        }
                    }
                });
            }
        }
        else if(v.getId() == R.id.textView9)
        {
            stack.setCurrentPage(2);
        }
        else if(v.getId() == R.id.login_vis)
        {
            if(login_pw.getTransformationMethod()== PasswordTransformationMethod.getInstance())
            {
                login_pw.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                login_vis.setImageResource(R.drawable.vis);
            }
            else
            {
                login_pw.setTransformationMethod(PasswordTransformationMethod.getInstance());
                login_vis.setImageResource(R.drawable.invis);
            }
        }
        else if(v.getId() == R.id.signin_vis)
        {
            if(signup_pw.getTransformationMethod()== PasswordTransformationMethod.getInstance())
            {
                signup_pw.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                signin_vis.setImageResource(R.drawable.vis);
            }
            else
            {
                signup_pw.setTransformationMethod(PasswordTransformationMethod.getInstance());
                signin_vis.setImageResource(R.drawable.invis);
            }
        }
        else if(v.getId() == R.id.signin_vis2)
        {
            if(signup_pw2.getTransformationMethod()== PasswordTransformationMethod.getInstance())
            {
                signup_pw2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                signin_vis2.setImageResource(R.drawable.vis);
            }
            else
            {
                signup_pw2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                signin_vis2.setImageResource(R.drawable.invis);
            }
        }
    };
}
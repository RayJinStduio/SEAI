package com.rayjin.seai.BmobClass;

import cn.bmob.v3.BmobObject;

public class feedback extends BmobObject
{
    private String content;
    private String username;

    public void setUsername(String username)
    {
        this.username= username;
    }

    public void setContent(String content)
    {
        this.content = content;
    }
}
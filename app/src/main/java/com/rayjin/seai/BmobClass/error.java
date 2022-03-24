package com.rayjin.seai.BmobClass;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class error extends BmobObject
{
    private String right;
    private String error;
    private BmobFile pic;
    public void setError(String error)
    {
        this.error= error;
    }

    public void setRight(String right)
    {
        this.right = right;
    }

    public void setPic(BmobFile pic){this.pic=pic;}

}
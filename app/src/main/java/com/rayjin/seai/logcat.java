package com.rayjin.seai;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class logcat extends BmobObject
{
    public BmobFile logcat;

    public void setLogcat(BmobFile logcat)
    {
        this.logcat= logcat;
    }
}
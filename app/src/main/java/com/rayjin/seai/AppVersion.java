package com.rayjin.seai;

import cn.bmob.v3.datatype.BmobFile;

public class AppVersion extends  BmobFile
{
    Integer version_i;
    String version;
    BmobFile path;
    String update_log;
    public Integer getversion_i() {
        return version_i;
    }
    public String getversion()
    {
        return version;
    }
    public BmobFile getpath()
    {
        return  path;
    }
    public String getupdate_log()
    {
        return update_log;
    }
}

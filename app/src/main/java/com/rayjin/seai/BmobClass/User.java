package com.rayjin.seai.BmobClass;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

public class User extends BmobUser
{
    private BmobFile icons;

    public User seticons(BmobFile icons)
    {
        this.icons = icons;
        return this;
    }
    public BmobFile geticons()
    {
        return icons;
    }
}
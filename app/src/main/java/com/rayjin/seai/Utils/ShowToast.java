package com.rayjin.seai.Utils;

import android.content.Context;
import android.widget.Toast;

public class ShowToast
{
    private static Toast toast = null;
    public static void showToast(Context context, String str)
    {
        if (toast == null)
        {
            toast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
        }
        else
        {
            toast.setText(str);
        }
        toast.show();
    }
}
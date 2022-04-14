package com.rayjin.seai;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.util.Log;

import com.rayjin.seai.Utils.Discriminate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ObverseThread implements Runnable
{
    private Activity mActivity;
    private byte[] bytes;
    public void setTar(Activity a,byte[] b)
    {
        mActivity = a;
        bytes = b;
    }

    @Override
    public void run()
    {
        Discriminate d = new Discriminate();
        String res;
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        res = d.DcAnimal(mActivity,bitmap);
        res = getjson(res);
        Log.i("Ray",res);
        Message msg =Message.obtain();
        msg.obj = res;
        msg.what=1;   //标志消息的标志
        ObserveActivity.handler.sendMessage(msg);
    }

    private String getjson(String string)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(string);
            String result = jsonObject.getString("result");
            JSONArray array = new JSONArray(result);
            StringBuffer buffer = new StringBuffer();
            JSONObject object = array.getJSONObject(0);
            String name = object.getString("name");
            double score = object.optDouble("score");
            score*=100;
            String sscore = String.format("%.2f",score);
            //Log.e("1", "name：" + name + "  score：" + sscore + "%" );
            buffer.append( name + " score: " + sscore + "%" + "\n");
            return buffer.toString();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}

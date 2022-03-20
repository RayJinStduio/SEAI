package com.rayjin.seai;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.rayjin.seai.Utils.Base64Util;
import com.rayjin.seai.Utils.FileUtil;
import com.rayjin.seai.Utils.HttpUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;

public class Discriminate
{
    public byte[] PathToByte(String path) throws IOException
    {
        return FileUtil.readFileByBytes(path);
    }

    public Bitmap PathtoBitmap(String path) throws FileNotFoundException
    {
        FileInputStream fis = new
                FileInputStream(path);
        Bitmap bitmap= BitmapFactory.decodeStream(fis);
        return bitmap;
    }

    public String DcAnimal(byte [] imgData)
    {
        String url = "https://aip.baidubce.com/rest/2.0/image-classify/v1/animal";
        try {
            // 本地文件路径
            String imgStr = Base64Util.encode(imgData);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");
            String param = "image=" + imgParam;
            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = AuthService.getAuth();
            //System.out.println(result);
            return HttpUtil.post(url, accessToken, param);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;

    }

//    public String DcAnimal(Bitmap img, Context c)
//    {
//        TrackingMobile t=new TrackingMobile(c);
//        String s=t.execute(img);
//        return s;
//
//    }

    public String DcAnimal(Context context,Bitmap img)
    {
        try
        {
            String res=RApplication.RClassifier.predictByModel(img);
            return res;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;

    }

    public String DcPlant(byte [] imgData)
    {
        String url = "https://aip.baidubce.com/rest/2.0/image-classify/v1/plant";
        try
        {
            // 本地文件路径
            String imgStr = Base64Util.encode(imgData);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");
            String param = "image=" + imgParam;
            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = AuthService.getAuth();
            //System.out.println(result);
            return HttpUtil.post(url, accessToken, param);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}

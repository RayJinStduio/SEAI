package com.rayjin.seai;

import java.io.IOException;
import java.net.URLEncoder;

public class Discriminate
{
    public byte[] PathToByte(String path) throws IOException
    {
        return FileUtil.readFileByBytes(path);
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

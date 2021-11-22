package com.rayjin.seai;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ResultActivity extends Activity {
    String path=null;
    TextView accuracy,res_tv;
    ProgressBar pb;
    int type;
    double Accuracy=0;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);
        path=getIntent().getStringExtra("picpath");//通过值"picpath"得到照片路径
        type=getIntent().getIntExtra("type",0);
        ImageView imageview=findViewById(R.id.pic);
        try{FileInputStream fis=new FileInputStream(path);//通过path把照片读到文件输入流中
            Bitmap bitmap=BitmapFactory.decodeStream(fis);//将输入流解码为bitmap
            Matrix matrix=new Matrix();//新建一个矩阵对象
            matrix.setRotate(90);//矩阵旋转操作让照片可以正对着你。但是还存在一个左右对称的问题
//新建位图，第2个参数至第5个参数表示位图的大小，matrix中是旋转后的位图信息，并使bitmap变量指向新的位图对象
            bitmap=Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
            //将位图展示在imageview上
            imageview.setImageBitmap(bitmap);}
        catch (FileNotFoundException e){e.printStackTrace();}
        //Bitmap bitmap=BitmapFactory.decodeFile(path);
        res_tv = findViewById(R.id.textview_2);
        accuracy = findViewById(R.id.textview1);
        pb=findViewById(R.id.pb);
        pb.setVisibility(View.VISIBLE);
        if(type==1) b_res_an();
        else b_res_p();
    }
    public void b_res_an()
    {
        final String[] res = new String[1];
        if(path!=null)
        {
            Thread t1 = new Thread()
            {
                public void run()
                {
                    Discriminate d = new Discriminate();
                    try
                    {
                        res[0] = d.DcAnimal(d.pathtobyte(path));

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(getjson(res[0])!=null)
                                    res_tv.setText(getjson(res[0]));
                                else res_tv.setText(res[0]);
                                accuracy.setText("准确率:"+(int)(Accuracy*100)+"%");
                                pb.setVisibility(View.GONE);
                            }
                        });
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                }
            };
            t1.start();

        }
    }
    public void b_res_p()
    {
        final String[] res = new String[1];
        if(path!=null)
        {
            Thread t1 = new Thread()
            {
                public void run()
                {
                    Discriminate d = new Discriminate();
                    try
                    {
                        res[0] = d.DcPlant(d.pathtobyte(path));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(getjson(res[0])!=null)
                                res_tv.setText(getjson(res[0]));
                                else res_tv.setText(res[0]);
                                accuracy.setText("准确率:"+(int)(Accuracy*100)+"%");
                            }
                        });
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                }
            };
            t1.start();

        }
    }
    private String getjson(String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            String result = jsonObject.getString("result");
            JSONArray array = new JSONArray(result);
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String name = object.getString("name");
                double score = object.optDouble("score");
                if(score>Accuracy) Accuracy=score;
                score*=100;
                String sscore = String.format("%.2f",score);
                Log.e("1", "name：" + name + "  score：" + sscore + "%" );
                buffer.append( name + " score: " + sscore + "%" + "\n");

            }
            return buffer.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }
}
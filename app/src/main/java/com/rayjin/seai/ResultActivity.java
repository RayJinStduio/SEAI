package com.rayjin.seai;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
    String res_main;
    double Accuracy=0;
    TextView fanKui;

    @Override
    protected void onCreate( Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);
        fanKui = findViewById(R.id.fanKui);
        fanKui.setOnClickListener(ResultOnClickListener);
        path=getIntent().getStringExtra("picpath");//通过值"picpath"得到照片路径
        type=getIntent().getIntExtra("type",0);
        ImageView imageview=findViewById(R.id.pic);
        try{FileInputStream fis=new FileInputStream(path);//通过path把照片读到文件输入流中
            Bitmap bitmap=BitmapFactory.decodeStream(fis);//将输入流解码为bitmap
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
    View.OnClickListener ResultOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fanKui:
                    Intent intent1 = new Intent(ResultActivity.this, FeedbackActivity.class);
                    intent1.putExtra("from", 1);
                    intent1.putExtra("error", res_main);
                    intent1.putExtra("pic",path);
                    startActivity(intent1);
                    break;
            }
        }
    };

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
                        res[0] = d.DcAnimal(d.PathToByte(path));

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
            //t1.start();
            if(path!=null)
            {
                Thread t2 = new Thread()
                {
                    public void run()
                    {
                        Discriminate d = new Discriminate();
                        try
                        {
                            Bitmap b = d.PathtoBitmap(path);
                            res[0] = d.DcAnimal(ResultActivity.this,b);
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
                        catch (FileNotFoundException e)
                        {

                        }
                    }
                };
                t2.start();
            }
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
                        res[0] = d.DcPlant(d.PathToByte(path));
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

    private String getjson(String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            String result = jsonObject.getString("result");
            JSONArray array = new JSONArray(result);
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String name = object.getString("name");
                if(i==0) res_main=name;
                double score = object.optDouble("score");
                if(score>Accuracy) Accuracy=score;
                score*=100;
                String sscore = String.format("%.2f",score);
                //Log.e("1", "name：" + name + "  score：" + sscore + "%" );
                buffer.append( name + " score: " + sscore + "%" + "\n");

            }
            return buffer.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
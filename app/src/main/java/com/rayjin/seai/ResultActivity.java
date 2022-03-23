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

import com.rayjin.seai.Utils.Discriminate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ResultActivity extends Activity {
    String path=null;
    TextView accuracy,res_tv,baike_tv;
    ProgressBar pb;
    int type;
    String res_main;
    double Accuracy=0;
    TextView fanKui;
    String res = "";

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
        baike_tv = findViewById(R.id.baike_tv);
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
//        if(path!=null)
//        {
//            Thread t1 = new Thread()
//            {
//                public void run()
//                {
//                    Discriminate d = new Discriminate();
//                    try
//                    {
//                        res = d.DcAnimal(d.PathToByte(path));
//
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                if(getjson(res)!=null)
//                                    res_tv.setText(getjson(res));
//                                else res_tv.setText(res);
//                                accuracy.setText("准确率:"+(int)(Accuracy*100)+"%");
//                                pb.setVisibility(View.GONE);
//                            }
//                        });
//                    }
//                    catch (IOException e)
//                    {
//                        e.printStackTrace();
//                    }
//                    String answer="";
//                    try
//                    {
//                        answer=Baike(res_main);
//                    }
//                    catch (Exception e)
//                    {
//                        e.printStackTrace();
//                    }
//                    final String ans = answer;
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            baike_tv.setText(ans);
//                        }
//                    });
//                }
//            };
//            //t1.start();
            if(path!=null)
            {
                Thread t1 = new Thread()
                {
                    public void run()
                    {
                        Discriminate d = new Discriminate();
                        try
                        {
                            //res = d.DcAnimal(d.PathToByte(path));

                            Bitmap b = d.PathtoBitmap(path);
                            res= d.DcAnimal(ResultActivity.this,b);


                            if(getjson(res)!=null) res=getjson(res);
                            else res="";
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    res_tv.setText(res);
                                    accuracy.setText("准确率:"+(int)(Accuracy*100)+"%");
                                    pb.setVisibility(View.GONE);
                                }
                            });
                        }
                        catch (Exception e)
                        {

                        }


                        if(!res_main.equals("非动物"))
                        {
                            String answer = "";
                            try
                            {
                                answer = Baike(res_main);
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                            final String ans = answer;
                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    baike_tv.setText(ans);
                                }
                            });
                        }
                    }
                };
                t1.start();
            }
    }
    public void b_res_p()
    {

        if(path!=null)
        {
            Thread t1 = new Thread()
            {
                public void run()
                {
                    Discriminate d = new Discriminate();
                    try
                    {
                        res = d.DcPlant(d.PathToByte(path));
                        if(getjson(res)!=null) res=getjson(res);
                        else res="";
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                res_tv.setText(res);
                                accuracy.setText("准确率:"+(int)(Accuracy*100)+"%");
                                pb.setVisibility(View.GONE);

                            }
                        });
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                    if(!res_main.equals("非植物"))
                    {
                        String answer = "";
                        try
                        {
                            answer = Baike(res_main);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        final String ans = answer;
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                baike_tv.setText(ans);
                            }
                        });
                    }
                }
            };
            t1.start();

        }
    }

    private String Baike(String q) throws Exception
    {
        String ques= URLEncoder.encode(q,"UTF-8");
        String url = "http://baike.baidu.com/api/openapi/BaikeLemmaCardApi?scope=103&format=json&appid=379020&bk_key="+ques+"&bk_length=600";
        String res = sendGet(url);
        JSONObject jsonObject = new JSONObject(res);
        String abstr = jsonObject.getString("abstract");
        return  abstr;
    }


    private String sendGet(String url) throws Exception {

        //System.out.println(url);
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //默认值我GET
        con.setRequestMethod("GET");

        //添加请求头
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.74 Safari/537.36 Edg/99.0.1150.46");

        int responseCode = con.getResponseCode();
        //System.out.println("\nSending 'GET' request to URL : " + url);
        //System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //打印结果
        return response.toString();

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
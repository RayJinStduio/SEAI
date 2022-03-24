package com.rayjin.seai.Classifier;


import android.graphics.Bitmap;


import org.pytorch.IValue;
import org.pytorch.LiteModuleLoader;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class Classifier {
    private static final String TAG = "Pytorch";
    private Module model;
    private float[] mean = {0.485F, 0.456F, 0.406F};
    private float[] std = {0.229F, 0.224F, 0.225F};
    int ModelId=-1;
    Thread LoadModel;
    ArrayList<String> cres=new ArrayList<String>();

    public Classifier() {

    }

    public void init(String modelPath,String filepath,int type)
    {
        LoadModel = new Thread()
        {
            public void run()
            {
                if (type != ModelId)
                {
                    ModelId = type;
                    model = LiteModuleLoader.load(modelPath);
                    readClass(filepath);
                }
            }
        };
        LoadModel.start();
    }

    public void setMeanAndStd(float[] mean, float[] std) {
        this.mean = mean;
        this.std = std;
    }

    public Tensor preprocess(Bitmap bitmap, int size) {
        bitmap = Bitmap.createScaledBitmap(bitmap, size, size, false);
        return TensorImageUtils.bitmapToFloat32Tensor(bitmap, this.mean, this.std);
    }

    public int argMax(float[] inputs) {
        int maxIndex = -1;
        float maxvalue = 0.0f;

        for (int i = 0; i < inputs.length; i++) {
            if (inputs[i] > maxvalue) {
                maxIndex = i;
                maxvalue = inputs[i];
            }
        }
        return maxIndex;
    }

    public int[] argTop5(float[] inputs) {
        int []TopIndex=new int[5];
        boolean []visited=new boolean[inputs.length];
        for(int k=0;k<5;k++)
        {
            int maxIndex = -1;
            float maxvalue = 0.0f;

            for (int i = 0; i < inputs.length; i++) {
                if (!visited[i]&&inputs[i] > maxvalue) {
                    maxIndex = i;
                    maxvalue = inputs[i];
                }
            }
            TopIndex[k]=maxIndex;
            visited[maxIndex]=true;
        }
        return TopIndex;
    }

    public String predictByModel(Bitmap bitmap) {
        try
        {
            LoadModel.join();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        Tensor tensor = preprocess(bitmap, 224);

        IValue inputs = IValue.from(tensor);
        Tensor outputs = model.forward(inputs).toTensor();
        float[] scores = outputs.getDataAsFloatArray();

        double[] scores_softmax = softmax(scores);
        //int classIndex = argMax(scores);
        //Log.i(TAG,classIndex+" "+scores[classIndex]);

        int []classIndex=argTop5(scores);
        String res="{\"result\":[";
        for(int i=0;i<classIndex.length;i++)
        {

            res += "{\"name\":\"" + cres.get(classIndex[i]) + "\",\"score\":\"" + scores_softmax[classIndex[i]] + "\"}";
            if(i!=classIndex.length-1) res+=",";
        }
        res+="]}";
        return res;
    }

    public void readClass(String fileName) {

        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            System.out.println("以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
                cres.add(tempString);
                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }

    public double[] softmax(float[] z)
    {
        float sum = 0f;
        for(int c=0;c<z.length;c++)
            sum+=Math.exp(z[c]);
        double[] res = new double[z.length];
        for(int i=0;i<z.length;i++)
            res[i]=Math.exp(z[i])/sum;
        return res;
    }


//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public String predictByBD(String path) {
//        Map<String, String> params = new HashMap<>(2);
//
//        byte[] bytes;
//        String imgParam = "";
//        try {
//            bytes = Utils.readFileToBytes(path);
//            imgParam = URLEncoder.encode(Base64.getEncoder().encodeToString(bytes), "UTF-8");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        params.put("image", imgParam);
//        params.put("top_num", "3");
//
//        JSONObject res = JSON.parseObject(OkHttpUtils.getInstance()
//                .doPostForm(Constants.BD_AI_URL, params));
//        if (res.getString("error_msg") != null) {
//            Log.e("BD_ERROR", res.getString("error_msg"));
//            return null;
//        }
//        JSONObject result = res.getJSONArray("result").getJSONObject(0);
//
//        String year = result.getString("year");
//        String name = result.getString("name");
//        return year + "-" + name;
//    }


}

package com.rayjin.seai;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.media.ImageReader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.nio.ByteBuffer;

public class CameraActivity2 extends AppCompatActivity {
   Camera2SurfaceView surfaceView;
    private Camera2Proxy mCameraProxy;
    CameraManager cameraManager;
    CameraDevice.StateCallback cam_stateCallback;
    CameraDevice opened_camera;
    Surface texture_surface;
    CameraCaptureSession.StateCallback cam_capture_session_stateCallback;
    CameraCaptureSession.CaptureCallback still_capture_callback;
    CameraCaptureSession cameraCaptureSession;
    CaptureRequest.Builder requestBuilder;
    CaptureRequest.Builder requestBuilder_image_reader;
    ImageReader imageReader;
    Surface imageReaderSurface;
    Bitmap bitmap;
    CaptureRequest request;
    CaptureRequest takephoto_request;
    Button takephoto_btn;
    ImageView takephoto_imageView;
    OverCameraView2 overCameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.takephoto2);
        takephoto_btn=findViewById(R.id.takephoto);
        takephoto_imageView= findViewById(R.id.imageView2);
        surfaceView=findViewById(R.id.preview);
        overCameraView=findViewById(R.id.over);
        overCameraView.setOnTouchListener(mOnTouchListener);
        mCameraProxy = surfaceView.getCameraProxy();
//        surfaceTextureListener=new TextureView.SurfaceTextureListener() {
//            @Override
//            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
//
//                //imageReader = ImageReader.newInstance(width  ,height, ImageFormat.JPEG,2);
//                //imageReaderSurface = imageReader.getSurface();
//
//            }
//            @Override
//            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
//            }
//            @Override
//            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
//                return false;
//            }
//            @Override
//            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
//            }
//        };
        //textureView.setSurfaceTextureListener(surfaceTextureListener);
        //B1.

        //B2. 准备工作：设置ImageReader收到图片后的回调函数


        //B4. 相机点击事件
        takephoto_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCameraProxy.setImageAvailableListener(mOnImageAvailableListener);
                mCameraProxy.captureStillPicture();
            }
        });
    }


    final View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                //获取点击屏幕的位置，作为焦点位置，用于计算对焦区域
                float x = event.getX();
                float y = event.getY();

                //对焦并绘制对焦矩形框
                overCameraView.setTouchFoucusRect( x, y);
            }
            return false;
        }
    };

    private void checkPermission() {
        // 检查是否申请了权限
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA)){

            }else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},1);
            }
        }
    }

    private ImageReader.OnImageAvailableListener mOnImageAvailableListener =
            new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    //new ImageSaveTask().execute(reader.acquireNextImage()); // 保存图片
                    Image image= reader.acquireLatestImage();
                    ByteBuffer buffer= image.getPlanes()[0].getBuffer();
                    int length= buffer.remaining();
                    byte[] bytes= new byte[length];
                    buffer.get(bytes);
                    image.close();
                    bitmap = BitmapFactory.decodeByteArray(bytes,0,length);
                    //B2.2 显示图片
                    takephoto_imageView.setVisibility(View.VISIBLE);
                    takephoto_imageView.setImageBitmap(bitmap);
                }
            };
    private class ImageSaveTask extends AsyncTask<Image, Void, Void>
    {

        @Override
        protected Void doInBackground(Image ... images) {
            ByteBuffer buffer = images[0].getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);

            long time = System.currentTimeMillis();
            if (mCameraProxy.isFrontCamera()) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                Log.d("TAG", "BitmapFactory.decodeByteArray time: " + (System.currentTimeMillis() - time));
                time = System.currentTimeMillis();
                // 前置摄像头需要左右镜像
                Bitmap rotateBitmap = ImageUtils.rotateBitmap(bitmap, 0, true, true);
                Log.d("TAG", "rotateBitmap time: " + (System.currentTimeMillis() - time));
                time = System.currentTimeMillis();
                ImageUtils.saveBitmap(rotateBitmap);
                Log.d("TAG", "saveBitmap time: " + (System.currentTimeMillis() - time));
                rotateBitmap.recycle();
            } else {
                //ImageUtils.saveImage(bytes);
                Log.d("TAG", "saveBitmap time: " + (System.currentTimeMillis() - time));
            }
            images[0].close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //takephoto_imageView.setImageBitmap(ImageUtils.getLatestThumbBitmap());
        }
    }
}
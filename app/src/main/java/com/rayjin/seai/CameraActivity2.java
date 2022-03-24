package com.rayjin.seai;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.rayjin.seai.Utils.ImageUtils;
import com.rayjin.seai.Utils.ToolUtils;
import com.rayjin.seai.View.Camera2TextureView;
import com.rayjin.seai.View.OverCameraView2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class CameraActivity2 extends AppCompatActivity {
    Camera2TextureView mCameraView;
    private Camera2Proxy mCameraProxy;
    public int type;
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
    ImageView takephoto_imageView,takephoto_album;
    OverCameraView2 overCameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.takephoto2);
        type= getIntent().getIntExtra("type",0);
        takephoto_btn=findViewById(R.id.takephoto);
        takephoto_imageView= findViewById(R.id.imageView2);
        mCameraView=findViewById(R.id.preview);
        overCameraView=findViewById(R.id.over);
        overCameraView.setOnTouchListener(mOnTouchListener);
        takephoto_album=findViewById(R.id.takephoto_album);
        takephoto_album.setOnClickListener(TakephotoOnClickListener);
        takephoto_btn.setOnClickListener(TakephotoOnClickListener);
        mCameraProxy = mCameraView.getCameraProxy();
        //requestcarema();
        if (type == 1)
        {
            try
            {
                RApplication.RClassifier.init(ToolUtils.assetFilePath(CameraActivity2.this, "test.ptl"),
                        ToolUtils.assetFilePath(CameraActivity2.this, "imagenet_classes.txt"),1);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

    }

    View.OnClickListener TakephotoOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.takephoto:
                    mCameraProxy.setImageAvailableListener(mOnImageAvailableListener);
                    mCameraProxy.captureStillPicture();
                    break;
                case R.id.takephoto_album:
                    launchAlbum();
            }
        }
    };

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
            new ImageReader.OnImageAvailableListener()
            {
                @Override
                public void onImageAvailable(ImageReader reader)
                {
                    //new ImageSaveTask().execute(reader.acquireNextImage()); // 保存图片
                    Image image = reader.acquireLatestImage();
                    ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                    int length = buffer.remaining();
                    byte[] bytes = new byte[length];
                    buffer.get(bytes);
                    image.close();
                    // bitmap = BitmapFactory.decodeByteArray(bytes,0,length);
                    //B2.2 显示图片
                    // takephoto_imageView.setVisibility(View.VISIBLE);
                    // takephoto_imageView.setImageBitmap(bitmap);

                    File tempfile = new File(CameraActivity2.this.getExternalCacheDir().toString() + "/temp.png");//新建一个文件对象tempfile，并保存在某路径中
                    try
                    {
                        FileOutputStream fos = new FileOutputStream(tempfile);
                        fos.write(bytes);//将照片放入文件中
                        fos.close();//关闭文件
                        Intent intent = new Intent(CameraActivity2.this, ResultActivity.class);//新建信使对象
                        intent.putExtra("picpath", tempfile.getAbsolutePath());//打包文件给信使
                        intent.putExtra("type", type);
                        startActivity(intent);//打开新的activity，即打开展示照片的布局界面
                        CameraActivity2.this.finish();//关闭现有界面
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
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

    public void launchAlbum()
    {
        mLauncherAlbum.launch("image/*");
    }

    private final ActivityResultLauncher<String> mLauncherAlbum = registerForActivityResult
            (
                    new ActivityResultContracts.GetContent(), result ->
                    {
                        if(result!=null)
                        {
                            String path = CUriToPath(result);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                                path=getPath(CameraActivity2.this,result);
                            Intent intent = new Intent(CameraActivity2.this, ResultActivity.class);//新建信使对象
                            intent.putExtra("picpath", path);//打包文件给信使
                            intent.putExtra("type", type);
                            startActivity(intent);//打开新的activity，即打开展示照片的布局界面
                            CameraActivity2.this.finish();//关闭现有界面

                        }
                        else
                        {
                            Toast toast = Toast.makeText(CameraActivity2.this, "选择已取消", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
            );
    public String CUriToPath(Uri uri)
    {
        return uri.getPath().replace("/files_root/cache",CameraActivity2.this.getExternalCacheDir().toString());
    }
    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @author paulburke
     */
    public static String getPath(final Context context, final Uri uri) {

        //final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
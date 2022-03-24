package com.rayjin.seai;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.rayjin.seai.BmobClass.User;
import com.rayjin.seai.Utils.CropImageResult;
import com.rayjin.seai.Utils.ShowToast;

import java.io.File;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class SettingActivity extends AppCompatActivity
{
    public TextView set_logout;
    public RelativeLayout set_Avatar,cpw;
    public Group menu;
    public Boolean isLogin;
    public int task_next=-1;
    public static final String action = "rayjin.broadcast.action";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        set_logout = findViewById(R.id.logout);
        set_logout.setOnClickListener(SetOnClickListener);
        set_Avatar =findViewById(R.id.protrait_rl);
        set_Avatar.setOnClickListener(SetOnClickListener);
        cpw = findViewById(R.id.cpw_rl);
        cpw.setOnClickListener(SetOnClickListener);
        menu = findViewById(R.id.group);
        if (BmobUser.isLogin())
        {
            isLogin=true;
        }
        else
        {
            isLogin=false;
            set_logout.setVisibility(View.INVISIBLE);
        }
    }

    public void DoTask()
    {
        if(task_next==0)
            showPopFormBottom();
        else if(task_next==1)
            launchCameraUri();
    }

    private void RequestWrite()
    {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        else DoTask();
    }

    private void RequestCamera()
    {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},1);
        else DoTask();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==1)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                DoTask();
            }
            else
            {
                //拒绝权限申请
                Toast.makeText(this,"权限被拒绝了",Toast.LENGTH_SHORT).show();
            }
        }
    }

    View.OnClickListener SetOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (v.getId()==R.id.logout)
            {
                if (isLogin) {
                    BmobUser.logOut();
                    Toast toast = Toast.makeText(SettingActivity.this, "账户已退出", Toast.LENGTH_SHORT);
                    toast.show();
                    set_logout.setVisibility(View.INVISIBLE);
                    BmobUser.logOut();
                    Intent intent = new Intent(action);
                    intent.putExtra("data", 0);
                    sendBroadcast(intent);
                    finish();
                }
            }
            else if(v.getId()==R.id.protrait_rl)
            {
                if (isLogin)
                {
                    task_next=0;
                    RequestWrite();
                }
                else
                {
                    Toast toast = Toast.makeText(SettingActivity.this, "用户未登录", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
            else if(v.getId() == R.id.cpw_rl)
            {
                if (isLogin)
                {
                    Intent intent1 = new Intent(SettingActivity.this, ForgetActivity.class);
                    startActivity(intent1);
                }
                else
                {
                    Toast toast = Toast.makeText(SettingActivity.this, "用户未登录", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        }
    };

    public void showPopFormBottom()
    {
        SelectPhoto takePhotoPopWin = new SelectPhoto(this, onClickListener);
        takePhotoPopWin.showAtLocation(findViewById(R.id.set_main), Gravity.CENTER, 0, 0);
    }

    private final View.OnClickListener onClickListener = v ->
    {
        if (v.getId()==R.id.take)
        {
            task_next=1;
            RequestCamera();
        }
        else if(v.getId()==R.id.album)
        {
            launchAlbum();
        }
    };


    private void ModifyAvatar(String path)
    {
        final User user = BmobUser.getCurrentUser(User.class);
        BmobFile bmobFile = new BmobFile(new File(path));
        bmobFile.uploadblock(new UploadFileListener()
        {
            @Override
            public void done(BmobException e)
            {
                if(e==null)
                {
                    user.seticons(bmobFile);
                    user.update(new UpdateListener()
                    {
                        @Override
                        public void done(BmobException e)
                        {
                            if (e == null)
                            {
                                ShowToast.showToast(SettingActivity.this, "头像修改成功");
                                Intent intent = new Intent("rayjin.broadcast.action");
                                intent.putExtra("data", 2);
                                sendBroadcast(intent);
                            }
                            else
                            {
                                ShowToast.showToast(SettingActivity.this, "头像修改失败：" + e.getMessage());
                                //Log.e("error", e.getMessage());toast.show();
                            }
                        }
                    });
                }
                else
                {
                    ShowToast.showToast(SettingActivity.this, "头像上传失败：" + e.getMessage());
                    //Log.e("error", e.getMessage());toast.show();
                }
            }
            @Override
            public void onProgress(Integer value)
            {
                // 返回的上传进度（百分比）
            }
        });
    }

    private final ActivityResultLauncher<String> mLauncherAlbum = registerForActivityResult
    (
        new ActivityResultContracts.GetContent(), result ->
        {
            if(result!=null)
                launchImageCrop(result);
            else
               ShowToast.showToast(SettingActivity.this, "选择已取消");
        }
    );

    //注册调用
    private final ActivityResultLauncher<Object> mLauncherCameraUri = registerForActivityResult
    (
        new TakeCameraUri(), result ->
        {
            if(result!=null)
                launchImageCrop(result);
            else
                ShowToast.showToast(SettingActivity.this, "拍照已取消");
        }
    );

    //裁剪图片注册
    private final ActivityResultLauncher<CropImageResult> mActLauncherCrop =
            registerForActivityResult(new CropImage(), result ->
            {
                //裁剪之后的图片Uri，接下来可以进行压缩处理
                if(result!=null)
                {
                    String path = CUriToPath(result);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                        path=getPath(SettingActivity.this,result);
                    ModifyAvatar(path);
                }
                else
                {
                    ShowToast.showToast(SettingActivity.this, "裁剪已取消");
                }
            });



    public void launchAlbum()
    {
        mLauncherAlbum.launch("image/*");
    }

    private void launchCameraUri()
    {
        mLauncherCameraUri.launch(null);
    }


    /**
     * 开启裁剪图片
     *
     * @param sourceUri 原图片uri
     */
    private void launchImageCrop(Uri sourceUri)
    {
        mActLauncherCrop.launch(new CropImageResult(sourceUri, 1, 1));
    }

    public String CUriToPath(Uri uri)
    {
        return uri.getPath().replace("/files_root/cache",SettingActivity.this.getExternalCacheDir().toString());
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

                // TODO handle non-primary volumes
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

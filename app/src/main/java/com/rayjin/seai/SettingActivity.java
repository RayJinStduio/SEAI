package com.rayjin.seai;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.SettingInjectorService;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;


import java.io.File;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class SettingActivity extends AppCompatActivity
{

    TextView set_logout;
    RelativeLayout set_Avatar;
    Group menu;
    View close;
    Boolean isLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        set_logout = findViewById(R.id.logout);
        set_logout.setOnClickListener(SetOnClickListener);
        set_Avatar =findViewById(R.id.protrait_rl);
        set_Avatar.setOnClickListener(SetOnClickListener);
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

    View.OnClickListener SetOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.logout:
                    if(isLogin)
                    {
                        BmobUser.logOut();
                        Toast toast = Toast.makeText(SettingActivity.this, "账户已退出", Toast.LENGTH_SHORT);
                        toast.show();
                        isLogin=false;
                        set_logout.setVisibility(View.INVISIBLE);
                    }
                    break;
                case R.id.protrait_rl:
                    if(isLogin) //menu.setVisibility(View.VISIBLE);
                        showPopFormBottom();
                    else
                    {
                    Toast toast = Toast.makeText(SettingActivity.this, "用户未登录", Toast.LENGTH_SHORT);
                    toast.show();
                    }
                    break;
                case R.id.album:
                    launchAlbum();
                    break;
                case R.id.take:
                    launchCameraUri();
                    break;
                default:
                    break;
            }
        }
    };

    public void showPopFormBottom() {
        SelectPhoto takePhotoPopWin = new SelectPhoto(this, onClickListener);
        //showAtLocation(View parent, int gravity, int x, int y)
        takePhotoPopWin.showAtLocation(findViewById(R.id.set_main), Gravity.CENTER, 0, 0);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.take:
                    launchCameraUri();
                    break;
                case R.id.album:
                    launchAlbum();
                    break;
            }
        }
    };


    private void motiAvatar(String path)
    {
        final User user = BmobUser.getCurrentUser(User.class);
        String picPath = path;
        BmobFile bmobFile = new BmobFile(new File(picPath));
        bmobFile.uploadblock(new UploadFileListener()
        {
            @Override
            public void done(BmobException e)
            {
                if(e==null)
                {
                    //bmobFile.getFileUrl()--返回的上传文件的完整地址
                    //user.setEmail("1234567@qq.com");
                    user.seticons(bmobFile);
                    user.update(new UpdateListener()
                    {
                        @Override
                        public void done(BmobException e)
                        {
                            //Toast toast;
                            Toast toast;
                            if (e == null)
                            {
                                toast = Toast.makeText(SettingActivity.this, "头像修改成功", Toast.LENGTH_SHORT);
                                //Snackbar.make(view, "更新用户信息成功：" + user.getAge(), Snackbar.LENGTH_LONG).show();
                            }
                            else
                            {
                                toast = Toast.makeText(SettingActivity.this, "头像修改失败：" + e.getMessage(), Toast.LENGTH_SHORT);
                                //Log.e("error", e.getMessage());toast.show();
                            }
                            toast.show();
                        }
                    });
                }
                else
                {
                    Toast toast = Toast.makeText(SettingActivity.this, "头像上传失败：" + e.getMessage(), Toast.LENGTH_SHORT);
                    //Log.e("error", e.getMessage());toast.show();
                    toast.show();
                }
            }
            @Override
            public void onProgress(Integer value)
            {
                // 返回的上传进度（百分比）
            }
        });
        //final BmobFile bmobFile2 = new BmobFile(new File(bmobFile.getFileUrl()));
        //user.seticons(bmobFile);
    }

    private final ActivityResultLauncher<String> mLauncherAlbum = registerForActivityResult
    (
        new ActivityResultContracts.GetContent(), result ->
        {
            if(result!=null)
            {
                launchImageCrop(result);
            }
            else
            {
                Toast toast = Toast.makeText(SettingActivity.this, "选择已取消", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    );

    //注册调用
    private final ActivityResultLauncher<Object> mLauncherCameraUri = registerForActivityResult
    (
        new TakeCameraUri(), new ActivityResultCallback<Uri>()
        {
            @Override
            public void onActivityResult(Uri result)
            {
                if(result!=null)
                {
                    launchImageCrop(result);
                }
                else
                {
                    Toast toast = Toast.makeText(SettingActivity.this, "拍照已取消", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
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
                    motiAvatar(path);
                }
                else
                {
                    Toast toast = Toast.makeText(SettingActivity.this, "裁剪已取消", Toast.LENGTH_SHORT);
                    toast.show();
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

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
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
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

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

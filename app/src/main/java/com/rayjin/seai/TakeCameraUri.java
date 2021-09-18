package com.rayjin.seai;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import java.io.File;

/**
 * 拍照返回存储的图片的uri
 */
public class TakeCameraUri extends ActivityResultContract<Object, Uri> {

    //拍照返回的uri
    private Uri uri;

    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, Object input) {

        String mimeType = "image/jpeg";
        String fileName = System.currentTimeMillis() + ".jpg";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/SEAI");
            uri = context.getContentResolver()
                    .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        } else {
            String authorities = "com.rayjin.seai.fileProvider";
            uri = FileProvider.getUriForFile(context, authorities,
                    new File(context.getExternalCacheDir().getAbsolutePath(), fileName));
        }
        return new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, uri);
    }

    @Override
    public Uri parseResult(int resultCode, @Nullable Intent intent)
    {
        if(resultCode==-1) return uri;
        else return null;
    }
}
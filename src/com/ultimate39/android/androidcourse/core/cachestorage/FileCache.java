package com.ultimate39.android.androidcourse.core.cachestorage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 Save bitmap files on disk (external of internal)
 */
public class FileCache {
    private File mCacheDirectory;

    public FileCache(Context context, String nameOfCacheDirectory) {
        if (isExternalStorageAvailable()) {
            mCacheDirectory = new File(android.os.Environment.getExternalStorageDirectory(), nameOfCacheDirectory);
        } else {
            mCacheDirectory = context.getCacheDir();
        }

        if (!mCacheDirectory.exists())
            mCacheDirectory.mkdirs();
    }

    private boolean isExternalStorageAvailable() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    public Bitmap getBitmap(String name) {
        File file = new File(mCacheDirectory, name);
        Bitmap bitmap = decodeFileToBitmap(file);
        return bitmap;
    }

    private Bitmap decodeFileToBitmap(File file) {
        try {
            FileInputStream stream = new FileInputStream(file);
            Bitmap bitmap = BitmapFactory.decodeStream(stream);
            return bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void clearCache() {
        File[] files = mCacheDirectory.listFiles();
        if (files != null) {
            for (File f : files)
                f.delete();
        }
    }


}

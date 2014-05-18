package com.ultimate39.android.androidcourse.core.cachestorage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import com.ultimate39.android.androidcourse.ui.vacancy.ActivityVacancies;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Save bitmap files on disk (external of internal)
 */
public class FileCache {
    private File mCacheDirectory;

    public FileCache(Context context, String nameOfCacheDirectory) {
        mCacheDirectory = context.getCacheDir();
        if (!mCacheDirectory.exists())
            mCacheDirectory.mkdirs();
    }

    public Bitmap getBitmap(String name) {
        File file = new File(mCacheDirectory, name);
        Bitmap bitmap = null;
        if (file.length() != 0) {
            bitmap = decodeFileToBitmap(file);
        }
        return bitmap;
    }

    public void putBitmap(String name, Bitmap bitmap) {
        File file = new File(mCacheDirectory, name);
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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

    public void printCacheList() {
        File[] files = mCacheDirectory.listFiles();
        Log.d(ActivityVacancies.LOG_TAG,"------------------------------");
        for(File file:files) {
            Log.d(ActivityVacancies.LOG_TAG, file.getName());
        }
    }
}

package com.ultimate39.android.androidcourse.core.cachestorage;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import com.ultimate39.android.androidcourse.ui.vacancy.ActivityVacancies;

/**
 * Created by Влад on 16.05.14.
 */
public class CacheStorage {
    private MemoryCache mMemoryCache;
    private FileCache mFileCache;

    public CacheStorage(Context context, String nameOfCacheDirectory) {
        mMemoryCache = new MemoryCache();
        mFileCache = new FileCache(context, nameOfCacheDirectory);
    }

    public void printCacheList() {
        //mFileCache.printCacheList();
        mMemoryCache.printCacheList();
    }

    public Bitmap getBitmap(String name) {
        Bitmap bitmap = getBitmapFromMemoryCache(name);
        Log.d(ActivityVacancies.LOG_TAG, "NAME OF FILE!!!!!!:" + name);
        if (bitmap == null) {
            bitmap = getBitmapFromDisk(name);
        }
        return bitmap;
    }

    public Bitmap getBitmapFromDisk(String name) {
        Bitmap bitmap = mFileCache.getBitmap(name);
        return bitmap;
    }

    public Bitmap getBitmapFromMemoryCache(String name) {
        Bitmap bitmap = mMemoryCache.getBitmapFromMemoryCache(name);
        return bitmap;
    }

    public void putBitmap(String name, Bitmap bitmap) {
        mMemoryCache.addBitmapToMemoryCache(name, bitmap);
        mFileCache.putBitmap(name, bitmap);
    }

    public void clearCache() {
        mFileCache.clearCache();
    }

}

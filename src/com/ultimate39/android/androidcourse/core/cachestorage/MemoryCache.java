package com.ultimate39.android.androidcourse.core.cachestorage;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;
import com.ultimate39.android.androidcourse.ui.MainActivity;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
  Save bitmap files in heap
 */
public class MemoryCache {
    private LruCache<String, Bitmap> mMemoryCache;

    public MemoryCache(){
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize);
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemoryCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemoryCache(String key) {
        Bitmap bitmap = null;
        try {
        bitmap = mMemoryCache.get(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  bitmap;
    }

}

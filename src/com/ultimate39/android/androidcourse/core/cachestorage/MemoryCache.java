package com.ultimate39.android.androidcourse.core.cachestorage;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
  Save bitmap files in heap
 */
public class MemoryCache {

    private Map<String, Bitmap> cache = Collections.synchronizedMap(new LinkedHashMap<String, Bitmap>(10, 0.75f, true));
    private long mCurrentCacheSizeInBytes = 0;
    private long limitCacheSizeInBytes;

    public void setLimiCacheSize(long bytes) {
        limitCacheSizeInBytes = bytes;
    }

    public Bitmap getBitmap(String id) {
        try {
            if (!cache.containsKey(id))
                return null;
            return cache.get(id);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void putBitmap(String id, Bitmap bitmap) {
        try {
            if (cache.containsKey(id))
                mCurrentCacheSizeInBytes -= getSizeInBytes(cache.get(id));
            cache.put(id, bitmap);
            mCurrentCacheSizeInBytes += getSizeInBytes(bitmap);
            checkSize();
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    private void checkSize() {
        if (mCurrentCacheSizeInBytes > limitCacheSizeInBytes) {
            Iterator<Map.Entry<String, Bitmap>> iter = cache.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, Bitmap> entry = iter.next();
                mCurrentCacheSizeInBytes -= getSizeInBytes(entry.getValue());
                iter.remove();
                if (mCurrentCacheSizeInBytes <= limitCacheSizeInBytes)
                    break;
            }
        }
    }

    public void clearCache() {
        try {
            cache.clear();
            mCurrentCacheSizeInBytes = 0;
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    long getSizeInBytes(Bitmap bitmap) {
        if (bitmap == null)
            return 0;
        return bitmap.getRowBytes() * bitmap.getHeight();
    }


}

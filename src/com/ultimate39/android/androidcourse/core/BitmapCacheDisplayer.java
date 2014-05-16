package com.ultimate39.android.androidcourse.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;
import com.ultimate39.android.androidcourse.core.cachestorage.CacheStorage;

import java.io.File;

/**
 * Created by Влад on 16.05.14.
 */
public class BitmapCacheDisplayer {
    private CacheStorage mCacheStorage;
    private int mDefaultImageId = -1;
    private final long DEFAULT_SIZE_CACHE_IN_BYTES = 5000;

    public BitmapCacheDisplayer(Context context, String nameOfCacheDirectory) {
        mCacheStorage = new CacheStorage(context, nameOfCacheDirectory);
        mCacheStorage.setLimitCacheSizeInMemory(DEFAULT_SIZE_CACHE_IN_BYTES);
    }

    public void displayImage(ImageView imageView, String url) {

    }

    public BitmapCacheDisplayer setLimiCacheSizeInBytes(long bytes) {
        mCacheStorage.setLimitCacheSizeInMemory(bytes);
        return this;
    }

    public BitmapCacheDisplayer setIdForDefaultImage(int id) {
        mDefaultImageId = id;
        return this;
    }


    public void clearCache() {
        mCacheStorage.clearCache();
    }

    private class LazyImageView{
        String url;
        ImageView view;
    }

    private class ImageLoader extends AsyncTask<LazyImageView, Long, String> {

        @Override
        protected String doInBackground(LazyImageView... params) {
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

        }

        private Bitmap downloadImageFromInternet (String url){
            File f = fileCache.getFile(url);

            //from SD cache
            Bitmap b = decodeFile(f);
            if(b!=null)
                return b;

            //from web
            try {
                Bitmap bitmap=null;
                URL imageUrl = new URL(url);
                HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
                conn.setConnectTimeout(30000);
                conn.setReadTimeout(30000);
                conn.setInstanceFollowRedirects(true);
                InputStream is=conn.getInputStream();
                OutputStream os = new FileOutputStream(f);
                Utils.CopyStream(is, os);
                os.close();
                conn.disconnect();
                bitmap = decodeFile(f);
                return bitmap;
            } catch (Throwable ex){
                ex.printStackTrace();
                if(ex instanceof OutOfMemoryError)
                    memoryCache.clear();
                return null;
            }
        }
    }
}

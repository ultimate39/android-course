package com.ultimate39.android.androidcourse.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.ImageView;
import com.ultimate39.android.androidcourse.R;
import com.ultimate39.android.androidcourse.core.cachestorage.CacheStorage;
import com.ultimate39.android.androidcourse.ui.vacancy.ActivityVacancies;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;


/**
 * Created by Влад on 16.05.14.
 */
public class BitmapCacheDisplayer {
    private CacheStorage mCacheStorage;
    private int mDefaultImageId = -1;
    private ArrayList<ImageLoader> mPoolTasks;
    private boolean mIsCancelDownload = false;
    private static BitmapCacheDisplayer mInstance;

    private BitmapCacheDisplayer(Context context, String nameOfCacheDirectory) {
        mCacheStorage = new CacheStorage(context, nameOfCacheDirectory);
        mPoolTasks = new ArrayList<ImageLoader>(5);
    }

    public static BitmapCacheDisplayer getInstance(Context context, String nameOfCacheDirectory) {
        if (mInstance == null) {
            mInstance = new BitmapCacheDisplayer(context, nameOfCacheDirectory);
        }
        return mInstance;
    }

    public void displayImage(ImageView imageView, String url) {
        mCacheStorage.printCacheList();
        mIsCancelDownload = false;
        if (url != null) {
            Bitmap bitmap = mCacheStorage.getBitmapFromMemoryCache(encodeUrl(url));
            if (bitmap == null) {
                LazyImageView lazyImageView = new LazyImageView(imageView, url);
                imageView.setImageResource(R.drawable.default_thumb);
                ImageLoader imageLoader = new ImageLoader();
                if (Build.VERSION.SDK_INT >= 11) {
                    imageLoader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, lazyImageView);
                }
                mPoolTasks.add(imageLoader);
            } else {
                imageView.setImageBitmap(bitmap);
            }
        } else {
            imageView.setImageResource(R.drawable.default_thumb);
        }
    }

    private String encodeUrl(String url) {
        String encodedUrl = null;
        try {
            if (url != null)
                encodedUrl = URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodedUrl;
    }

    public BitmapCacheDisplayer setIdForDefaultImage(int id) {
        mDefaultImageId = id;
        return this;
    }

    public void stopDisplayImages() {
        mIsCancelDownload = true;
        for (ImageLoader imageLoader : mPoolTasks) {
            imageLoader.cancel(true);
        }
        mPoolTasks.clear();
    }

    public void clearCache() {
        mCacheStorage.clearCache();
    }

    private class LazyImageView {
        String url;
        ImageView view;

        public LazyImageView(ImageView view, String url) {
            this.url = url;
            this.view = view;
        }
    }

    private class ImageLoader extends AsyncTask<LazyImageView, Long, Bitmap> {
        private LazyImageView mLazyImageView;

        @Override
        protected Bitmap doInBackground(LazyImageView... params) {
            mLazyImageView = params[0];
            Bitmap bitmap = mCacheStorage.getBitmap(encodeUrl(mLazyImageView.url));
            if (bitmap == null && mLazyImageView.url != null) {
                bitmap = downloadImageFromInternet(mLazyImageView.url);
                if (bitmap != null) {
                    mCacheStorage.putBitmap(encodeUrl(mLazyImageView.url), bitmap);
                }
            }
            if (bitmap != null)
                mCacheStorage.putBitmap(encodeUrl(mLazyImageView.url), bitmap);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap s) {
            if (s != null) {
                mLazyImageView.view.setImageBitmap(s);
            } else {
                mLazyImageView.view.setImageResource(R.drawable.default_thumb);
            }
        }

        private Bitmap downloadImageFromInternet(String url) {
            Bitmap bitmap = null;
            try {
                Log.d(ActivityVacancies.LOG_TAG, "Start download:" + url);
                HttpParams httpParameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, 10000);
                HttpConnectionParams.setSoTimeout(httpParameters, 7000);
                HttpClient client = new DefaultHttpClient(httpParameters);
                HttpResponse response = client.execute(new HttpGet(url));
                HttpEntity entity = response.getEntity();
                if (entity.getContentLength() > 400 * 1000) {
                    return null;
                }
                InputStream is = entity.getContent();
                Log.d(ActivityVacancies.LOG_TAG, "FinishDownload");
                Log.d(ActivityVacancies.LOG_TAG, "Image is decoded:" + url);
                if (!isCancelled()) {
                    bitmap = BitmapFactory.decodeStream(is);
                }
                is.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        public byte[] streamToBytes(InputStream input) throws IOException {
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = input.read(buffer)) != -1) {

                output.write(buffer, 0, bytesRead);
            }
            return output.toByteArray();
        }

        public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;
            if (height > reqHeight || width > reqWidth) {
                final int halfHeight = height / 2;
                final int halfWidth = width / 2;
                while ((halfHeight / inSampleSize) > reqHeight
                        && (halfWidth / inSampleSize) > reqWidth) {
                    inSampleSize *= 2;
                }
            }

            return inSampleSize;
        }

        public Bitmap decodeSampledBitmapFromBytes(byte[] bytes, int reqWidth, int reqHeight) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        }
    }
}

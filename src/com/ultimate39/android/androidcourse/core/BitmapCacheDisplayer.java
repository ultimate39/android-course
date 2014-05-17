package com.ultimate39.android.androidcourse.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import com.ultimate39.android.androidcourse.R;
import com.ultimate39.android.androidcourse.core.cachestorage.CacheStorage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;


/**
 * Created by Влад on 16.05.14.
 */
public class BitmapCacheDisplayer {
    private CacheStorage mCacheStorage;
    private int mDefaultImageId = -1;

    public BitmapCacheDisplayer(Context context, String nameOfCacheDirectory) {
        mCacheStorage = new CacheStorage(context, nameOfCacheDirectory);
    }

    public void displayImage(ImageView imageView, String url) {
        Bitmap bitmap = mCacheStorage.getBitmapFromMemoryCache(encodeUrl(url));
        if (bitmap == null) {
            LazyImageView lazyImageView = new LazyImageView(imageView, url);
            imageView.setImageResource(R.drawable.default_thumb);
            new ImageLoader().execute(lazyImageView);
        } else {
            imageView.setImageBitmap(bitmap);
        }
    }

    private String encodeUrl(String url) {
        String encodedUrl = null;
        try {
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
                URL imageUrl = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
                conn.setConnectTimeout(30000);
                conn.setReadTimeout(30000);
                conn.setInstanceFollowRedirects(true);
                InputStream is = conn.getInputStream();
                bitmap = decodeSampledBitmapFromResource(readFully(is), 50, 50);
                is.close();
                conn.disconnect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        public byte[] readFully(InputStream input) throws IOException
        {
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = input.read(buffer)) != -1)
            {
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

        public Bitmap decodeSampledBitmapFromResource(byte[] bytes , int reqWidth, int reqHeight) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(bytes,0, bytes.length, options);
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeByteArray(bytes,0, bytes.length, options);
        }
    }
}

package com.ultimate39.android.androidcourse.core;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Влад on 15.05.14.
 */
public class InternetUtils {

    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public File downloadFile(Context context, String urlOfFile, String fileSavePath) {
        File file = context.getFileStreamPath(fileSavePath);
        try {
            URL url = new URL(urlOfFile);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            byte data[] = new byte[4096];
            int count;
            InputStream input;
            input = connection.getInputStream();
            OutputStream output;
            output = context.openFileOutput(fileSavePath, Context.MODE_PRIVATE);
            while ((count = input.read(data)) != -1) {
                output.write(data, 0, count);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}




package com.ultimate39.android.androidcourse.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import com.ultimate39.android.androidcourse.R;

public class MainActivity extends Activity {
    public static final String LOG_TAG = "HHClient";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    public void test (View view) {
        AsyncTaskVacancyDownloader task = new AsyncTaskVacancyDownloader();
        task.execute("android");
    }
}

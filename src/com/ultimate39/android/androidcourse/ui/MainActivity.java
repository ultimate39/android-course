package com.ultimate39.android.androidcourse.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import com.ultimate39.android.androidcourse.R;

public class MainActivity extends ActionBarActivity {
    public static final String LOG_TAG = "androidcourse";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacancy_listview);
    }


}

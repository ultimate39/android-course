package com.ultimate39.android.androidcourse.ui.vacancy;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import com.ultimate39.android.androidcourse.R;

public class ActivityVacancies extends ActionBarActivity {
    public static final String LOG_TAG = "androidcourse";
    public static final String KEY_VACANCY_ID = "vacancy_id";
    public static final String KEY_LOGO_URL = "vacancy_logo_url";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacancy_listview);

    }


}

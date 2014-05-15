package com.ultimate39.android.androidcourse.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import com.ultimate39.android.androidcourse.R;
import com.ultimate39.android.androidcourse.core.vacancy.Vacancy;
import com.ultimate39.android.androidcourse.ui.vacancy.ListViewAdapterVacancy;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends Activity {
    public static final String LOG_TAG = "HHClient";
    private ListView mListViewVacancies;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mListViewVacancies = (ListView) findViewById(R.id.lv_vacancies);
    }

    public void test (View view) {
        AsyncTaskVacancyDownloader task = new AsyncTaskVacancyDownloader();
        try {
            ArrayList<Vacancy> vacancyArrayList = task.execute("android").get();
            mListViewVacancies.setAdapter(new ListViewAdapterVacancy(getApplicationContext(), vacancyArrayList));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}

package com.ultimate39.android.androidcourse.ui.vacancy;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import com.ultimate39.android.androidcourse.R;
import com.ultimate39.android.androidcourse.core.BitmapCacheDisplayer;
import com.ultimate39.android.androidcourse.core.vacancy.JsonVacancyParser;
import com.ultimate39.android.androidcourse.core.vacancy.Vacancy;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;

/**
 * Created by Влад on 18.05.14.
 */
public class ActivityDetailedVacancy extends ActionBarActivity {
    private View mProgressBar;
    private View mContent;
    private String mIdVacancy;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacancy_detail);
        mProgressBar = findViewById(R.id.progressbar);
        mContent = findViewById(R.id.content);
        Intent intent = getIntent();
        mIdVacancy = intent.getStringExtra(ActivityVacancies.KEY_VACANCY_ID);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        new AsyncTaskVacancyDownloader().execute(mIdVacancy);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private class ViewHolderVacancy {
        private TextView mVacancyName;
        private TextView mVacancySalary;
        private TextView mVacancyEmployer;
        private TextView mVacancyPublishedAt;
        private WebView mVacancyDescription;
        private ImageView mVacancyLogo;
        private BitmapCacheDisplayer mDisplayer = BitmapCacheDisplayer.getInstance(getApplicationContext(), "images");

        public ViewHolderVacancy() {
            mVacancyName = (TextView) findViewById(R.id.tv_vacancy_detailed_name);
            mVacancySalary = (TextView) findViewById(R.id.tv_vacancy_detailed_salary);
            mVacancyEmployer = (TextView) findViewById(R.id.tv_vacancy_detailed_employer);
            mVacancyPublishedAt = (TextView) findViewById(R.id.tv_vacancy_detailed_published_at);
            mVacancyDescription = (WebView) findViewById(R.id.wv_vacancy_detailed_description);
            mVacancyLogo = (ImageView) findViewById(R.id.iv_vacancy_detailed_logo);
        }

        public void setContent(Vacancy vacancy) {
            mVacancyName.setText(vacancy.getName());
            if(vacancy.getSalary() != null) {
             mVacancySalary.setText(vacancy.getSalary());
            }
            mVacancyEmployer.setText(vacancy.getEmployerName());
            mVacancyPublishedAt.setText(vacancy.getTimePublished());
            mDisplayer.displayImage(mVacancyLogo, vacancy.getLogoUrl());
            setDescription(vacancy.getDescription());
        }

        private void setDescription (String description){
            final String mimeType = "text/html";
            final String encoding = "UTF-8";
           // description = "<body style=\"background-color:transparent;>" + description + "</body>";
            mVacancyDescription.loadDataWithBaseURL("", description, mimeType, encoding, "");
            mVacancyDescription.setBackgroundColor(0x00000000);
            mVacancyDescription.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        }
    }

    private void showProgressBar(boolean isShow) {
        if (isShow) {
            mProgressBar.setVisibility(View.VISIBLE);
            mContent.setVisibility(View.GONE);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mContent.setVisibility(View.VISIBLE);
        }
    }


    class AsyncTaskVacancyDownloader extends AsyncTask<String, String, Vacancy> {

        @Override
        protected void onPreExecute() {
            showProgressBar(true);
        }

        @Override
        protected Vacancy doInBackground(String... params) {
            String source = makeRequestForDetailedVacancy(params[0]);
            Log.d(ActivityVacancies.LOG_TAG, "Step 1 - Download JSON text");
            JsonVacancyParser vacancyParser = new JsonVacancyParser(ActivityDetailedVacancy.this);
            Vacancy vacancy = vacancyParser.parseDetailedVacancy(source);
            Log.d(ActivityVacancies.LOG_TAG, "Step 1 - Finished");
            Log.d(ActivityVacancies.LOG_TAG, "Step 2 - Create ListView");
            return vacancy;
        }


        private String makeRequestForDetailedVacancy(String textSearch) {
            final String URL = "https://api.hh.ru/vacancies/"+textSearch;
            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(URL);
            String result = null;
            try {
                HttpResponse response = client.execute(get);
                result = EntityUtils.toString(response.getEntity());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Vacancy vacancy) {
            ViewHolderVacancy holder = new ViewHolderVacancy();
            holder.setContent(vacancy);
            showProgressBar(false);

        }

        private void printVacancy(Vacancy vacancy) {
            Log.d(ActivityVacancies.LOG_TAG, "-------------------------------");
            Log.d(ActivityVacancies.LOG_TAG, "Name:" + vacancy.getName() + "\n" +
                    "TimePublished:" + vacancy.getTimePublished() + "\n" +
                    "ID:" + vacancy.getId() + "\n" +
                    "LogoUrl:" + vacancy.getLogoUrl() + "\n" +
                    "EmployerName:" + vacancy.getEmployerName() + "\n");
            Log.d(ActivityVacancies.LOG_TAG, "-------------------------------");
        }
    }
}

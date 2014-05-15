package com.ultimate39.android.androidcourse.ui;

import android.os.AsyncTask;
import android.util.Log;
import com.ultimate39.android.androidcourse.core.vacancy.JsonVacancyParser;
import com.ultimate39.android.androidcourse.core.vacancy.Vacancy;
import com.ultimate39.android.androidcourse.core.vacancy.VacancyParser;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;

/**
 * Created by Влад on 15.05.14.
 */
public class AsyncTaskVacancyDownloader extends AsyncTask<String, String, ArrayList<Vacancy>> {

    @Override
    protected ArrayList<Vacancy> doInBackground(String... params) {
        String source = makeRequestForVacancies("android");
        VacancyParser parser = new JsonVacancyParser();
        ArrayList<Vacancy> vacancies = parser.parseVacancies(source);
        for (Vacancy vacancy : vacancies) {
            printVacancy(vacancy);
        }
        return vacancies;
    }


    private String makeRequestForVacancies(String textSearch) {
        HttpClient client = new DefaultHttpClient();
        String url = String.format("https://api.hh.ru/vacancies?text=%s", textSearch);
        HttpGet get = new HttpGet(url);
        String result = null;
        try {
            HttpResponse response = client.execute(get);
            result = EntityUtils.toString(response.getEntity());
            Log.d(MainActivity.LOG_TAG,  result);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private void printVacancy(Vacancy vacancy) {
        Log.d(MainActivity.LOG_TAG, "-------------------------------");
        Log.d(MainActivity.LOG_TAG, "Name:" + vacancy.getName() + "\n" +
                "TimePublished:" + vacancy.getTimePublished() + "\n" +
                "EmployerName:" + vacancy.getEmployerName() + "\n");
        Log.d(MainActivity.LOG_TAG, "-------------------------------");
    }

}

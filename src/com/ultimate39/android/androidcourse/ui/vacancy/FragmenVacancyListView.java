package com.ultimate39.android.androidcourse.ui.vacancy;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import com.ultimate39.android.androidcourse.R;
import com.ultimate39.android.androidcourse.core.vacancy.JsonVacancyParser;
import com.ultimate39.android.androidcourse.core.vacancy.Vacancy;
import com.ultimate39.android.androidcourse.core.vacancy.VacancyParser;
import com.ultimate39.android.androidcourse.ui.MainActivity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by Влад on 17.05.14.
 */
public class FragmenVacancyListView extends Fragment {
    private ListView mListViewVacancies;
    private EditText mEditTextSearch;
    private VacancyParser mVacancyParser;
    private Button mButton;
    private CachedAdapterVacancy mVacanciesAdapter;
    private ActionBar mActionBar;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
        mVacancyParser = new JsonVacancyParser();
        mActionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        mActionBar.setDisplayShowTitleEnabled(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_vacancy_listview, container, false);
        mListViewVacancies = (ListView) root.findViewById(R.id.lv_vacancies);
        mEditTextSearch = (EditText) root.findViewById(R.id.et_search_text);
        mButton = (Button) root.findViewById(R.id.btn_search);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmenVacancyListView.this.test(v);
            }
        });

        if (mVacanciesAdapter != null) {
            mListViewVacancies.setAdapter(mVacanciesAdapter);
        } else {
            mVacanciesAdapter = new CachedAdapterVacancy(getActivity(), new ArrayList<Vacancy>());
            mListViewVacancies.setAdapter(mVacanciesAdapter);
        }
        return root;
    }

    public void test(View view) {
        AsyncTaskVacancyDownloader task = new AsyncTaskVacancyDownloader();
        try {
            mVacanciesAdapter.stopDisplayImages();
            ArrayList<Vacancy> vacancyArrayList = task.execute(mEditTextSearch.getText().toString()).get();
            Log.d(MainActivity.LOG_TAG, "Step 2 - Create ListView");
            mVacanciesAdapter.mVacancies = vacancyArrayList;
            mActionBar.setSubtitle(getActivity().getResources().getString(R.string.title_vacancy)+":"+mVacanciesAdapter.getCount());
            Log.d(MainActivity.LOG_TAG, "Step 2 - Finished");
            mVacanciesAdapter.notifyDataSetChanged();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    class AsyncTaskVacancyDownloader extends AsyncTask<String, String, ArrayList<Vacancy>> {

        @Override
        protected ArrayList<Vacancy> doInBackground(String... params) {
            String source = makeRequestForVacancies(params[0]);
            VacancyParser parser = new JsonVacancyParser();

            Log.d(MainActivity.LOG_TAG, "Step 1 - Download JSON text");
            ArrayList<Vacancy> vacancies = parser.parseVacancies(source);
            Log.d(MainActivity.LOG_TAG, "Step 1 - Finished");
           // for (Vacancy vacancy : vacancies) {printVacancy(vacancy);}
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
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        private void printVacancy(Vacancy vacancy) {
            Log.d(MainActivity.LOG_TAG, "-------------------------------");
            Log.d(MainActivity.LOG_TAG, "Name:" + vacancy.getName() + "\n" +
                    "TimePublished:" + vacancy.getTimePublished() + "\n" +
                    "ID:" + vacancy.getId() + "\n" +
                    "LogoUrl:" + vacancy.getLogoUrl() + "\n" +
                    "EmployerName:" + vacancy.getEmployerName() + "\n");
            Log.d(MainActivity.LOG_TAG, "-------------------------------");
        }
    }
}

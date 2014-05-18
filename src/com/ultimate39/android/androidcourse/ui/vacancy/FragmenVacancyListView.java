package com.ultimate39.android.androidcourse.ui.vacancy;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.ultimate39.android.androidcourse.core.BitmapCacheDisplayer;
import com.ultimate39.android.androidcourse.ui.MainActivity;
import com.ultimate39.android.androidcourse.R;
import com.ultimate39.android.androidcourse.core.vacancy.JsonVacancyParser;
import com.ultimate39.android.androidcourse.core.vacancy.Vacancy;
import com.ultimate39.android.androidcourse.core.vacancy.VacancyParser;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

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
    private String mSearchText;
    private String mSearchRegion;
    private View mProgressBar;
    private View mListViewContent;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        mVacancyParser = new JsonVacancyParser(getActivity());
        mActionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        mActionBar.setDisplayShowTitleEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        Intent intent = getActivity().getIntent();
        mSearchText = intent.getStringExtra(MainActivity.KEY_TEXT);
        mSearchRegion = intent.getStringExtra(MainActivity.KEY_REGION);
        displayVacancies(mSearchText, mSearchRegion);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_vacancy_listview, container, false);
        mListViewVacancies = (ListView) root.findViewById(R.id.lv_vacancies);        mListViewVacancies.setOnScr
        //mEditTextSearch = (EditText) root.findViewById(R.id.et_search_text);
        //mButton = (Button) root.findViewById(R.id.btn_search);
        /*
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           //     FragmenVacancyListView.this.test(v);
            }
        });
        */

        mListViewContent = root.findViewById(R.id.vacancies_content);
        mProgressBar = root.findViewById(R.id.progressbar);

        if (mVacanciesAdapter != null) {
            mListViewVacancies.setAdapter(mVacanciesAdapter);
        } else {
            mVacanciesAdapter = new CachedAdapterVacancy(getActivity(), new ArrayList<Vacancy>());
            mListViewVacancies.setAdapter(mVacanciesAdapter);
        }
        mListViewVacancies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ActivityDetailedVacancy.class);
                Vacancy vacancy = ((Vacancy)mVacanciesAdapter.getItem(position));
                intent.putExtra(ActivityVacancies.KEY_VACANCY_ID, vacancy.getId());
                intent.putExtra(ActivityVacancies.KEY_LOGO_URL, vacancy.getLogoUrl());
                Log.d(ActivityVacancies.LOG_TAG, "Vacancy ID:" + vacancy.getId() + " " + vacancy.getName());
                BitmapCacheDisplayer.getInstance(getActivity(), "images").stopDisplayImages();
                startActivity(intent);
            }
        });
        return root;
    }

    public void displayVacancies(String searchText, String searchRegion) {
        AsyncTaskVacancyDownloader task = new AsyncTaskVacancyDownloader();
        mVacanciesAdapter.stopDisplayImages();
        task.execute(searchText);

    }

    private void showProgressBar(boolean isShow) {
        if(isShow){
            mProgressBar.setVisibility(View.VISIBLE);
            mListViewContent.setVisibility(View.GONE);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mListViewContent.setVisibility(View.VISIBLE);
        }
    }

    class AsyncTaskVacancyDownloader extends AsyncTask<String, String, ArrayList<Vacancy>> {

        @Override
        protected void onPreExecute() {
            showProgressBar(true);
        }

        @Override
        protected ArrayList<Vacancy> doInBackground(String... params) {
            String source = makeRequestForVacancies(params[0]);
            Log.d(ActivityVacancies.LOG_TAG, "Step 1 - Download JSON text");
            ArrayList<Vacancy> vacancies = mVacancyParser.parseVacancies(source);
            Log.d(ActivityVacancies.LOG_TAG, "Step 1 - Finished");
            Log.d(ActivityVacancies.LOG_TAG, "Step 2 - Create ListView");
            mVacanciesAdapter.mVacancies = vacancies;
            return vacancies;
        }


        private String makeRequestForVacancies(String textSearch) {

            HttpClient client = new DefaultHttpClient();
            String url = "";
            try {
                 url = "https://api.hh.ru/vacancies?text=" + URLEncoder.encode(textSearch, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            Log.d(ActivityVacancies.LOG_TAG, "DASDSDADA" + url);
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

        @Override
        protected void onPostExecute(ArrayList<Vacancy> vacancies) {
            showProgressBar(false);
            mActionBar.setTitle(mSearchText);
            mActionBar.setSubtitle(getActivity().getResources().getString(R.string.title_vacancy)+":"+mVacancyParser.getFoundedVacancies());
            Log.d(ActivityVacancies.LOG_TAG, "Step 2 - Finished");
            mVacanciesAdapter.notifyDataSetChanged();
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

    private class SwipeDownToRefresh implements AbsListView.OnScrollListener {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if(firstVisibleItem+visibleItemCount == totalItemCount && totalItemCount!=0)
            {
                if(flag_loading == false)
                {
                    flag_loading = true;
                    additems();
                }
            }
        }
        }
    }
}

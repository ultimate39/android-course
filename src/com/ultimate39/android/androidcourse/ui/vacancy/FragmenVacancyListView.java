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
import com.ultimate39.android.androidcourse.R;
import com.ultimate39.android.androidcourse.core.BitmapCacheDisplayer;
import com.ultimate39.android.androidcourse.core.InternetUtils;
import com.ultimate39.android.androidcourse.core.vacancy.JsonVacancyParser;
import com.ultimate39.android.androidcourse.core.vacancy.Vacancy;
import com.ultimate39.android.androidcourse.core.vacancy.VacancyParser;
import com.ultimate39.android.androidcourse.ui.MainActivity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Влад on 17.05.14.
 */
public class FragmenVacancyListView extends Fragment {
    private ListView mListViewVacancies;
    private VacancyParser mVacancyParser;
    private CachedAdapterVacancy mVacanciesAdapter;
    private ActionBar mActionBar;
    private String mSearchText;
    private String mSearchRegion;
    private View mProgressBar;
    private View mListViewContent;
    private View mContentError;
    private int mPageOfVacancies = 0;
    boolean isLoaded = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVacancyParser = new JsonVacancyParser(getActivity());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        mActionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        mActionBar.setDisplayShowTitleEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setSubtitle(getResources().getString(R.string.title_vacancy) + ":" + mVacancyParser.getFoundedVacancies());
        Intent intent = getActivity().getIntent();
        mSearchText = intent.getStringExtra(MainActivity.KEY_TEXT);
        mSearchRegion = intent.getStringExtra(MainActivity.KEY_AREA);
        if (mVacanciesAdapter.getCount() == 0) {
            displayVacancies(mSearchText, mSearchRegion, mPageOfVacancies);
        }
        isLoaded = false;
    }

    private void initializeContentError(View view) {
        Button repeatButton = (Button) view.findViewById(R.id.btn_service_fail);
        mContentError = view.findViewById(R.id.content_error);
        repeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (InternetUtils.isInternetAvailable(getActivity())) {
                    mPageOfVacancies = 0;
                    mVacanciesAdapter.mVacancies.clear();
                    showProgressBar(true);
                    showErrorContent(false);
                    displayVacancies(mSearchText, mSearchRegion, mPageOfVacancies);
                } else {
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.message_text_internet_unvailable), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showErrorContent(boolean isShow) {
        if (isShow) {
            mContentError.setVisibility(View.VISIBLE);
            mListViewContent.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
        } else {
            mContentError.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_vacancy_listview, container, false);
        mListViewVacancies = (ListView) root.findViewById(R.id.lv_vacancies);
        initializeContentError(root);
        mListViewContent = root.findViewById(R.id.vacancies_content);
        mProgressBar = root.findViewById(R.id.progressbar);
        mListViewVacancies.setOnScrollListener(new SwipeDownToRefresh());
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
                Vacancy vacancy = ((Vacancy) mVacanciesAdapter.getItem(position));
                intent.putExtra(ActivityVacancies.KEY_VACANCY_ID, vacancy.getId());
                intent.putExtra(ActivityVacancies.KEY_LOGO_URL, vacancy.getLogoUrl());
                BitmapCacheDisplayer.getInstance(getActivity(), "images").stopDisplayImages();
                startActivity(intent);
            }
        });
        return root;
    }

    public void displayVacancies(String searchText, int page) {
        displayVacancies(searchText, null, page);
    }

    public void displayVacancies(String searchText, String searchRegion, int page) {
        AsyncTaskVacancyDownloader task = new AsyncTaskVacancyDownloader();
        mVacanciesAdapter.stopDisplayImages();
        task.execute(searchText, searchRegion, Integer.toString(page));
    }

    private void showProgressBar(boolean isShow) {
        if (mVacanciesAdapter.mVacancies.size() == 0) {
            if (isShow) {
                showProgressBar(false);
                mProgressBar.setVisibility(View.VISIBLE);
                mListViewContent.setVisibility(View.GONE);
            } else {
                mProgressBar.setVisibility(View.GONE);
                mListViewContent.setVisibility(View.VISIBLE);
            }
        }
    }

    class AsyncTaskVacancyDownloader extends AsyncTask<String, String, ArrayList<Vacancy>> {
        boolean isError = false;

        @Override
        protected void onPreExecute() {
            showProgressBar(true);
        }

        @Override
        protected ArrayList<Vacancy> doInBackground(String... params) {
            ArrayList<Vacancy> vacancies = null;
            try {
                String source = makeRequestForVacancies(params[0], params[1], params[2]);
                Log.d(ActivityVacancies.LOG_TAG, "Step 1 - Download JSON text");
                vacancies = mVacancyParser.parseVacancies(source);
                Log.d(ActivityVacancies.LOG_TAG, "Step 1 - Finished");
                Log.d(ActivityVacancies.LOG_TAG, "Step 2 - Create ListView");
            } catch (Exception e) {
                isError = true;
                e.printStackTrace();
            }
            return vacancies;
        }


        private String makeRequestForVacancies(String textSearch, String area, String page) {

            HttpClient client = new DefaultHttpClient();
            String url = "";
            try {
                url = "https://api.hh.ru/vacancies?text=" + URLEncoder.encode(textSearch, "UTF-8") + "&page=" + page;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            if (area != null) {
                url += "&area=" + area;
            }

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
            if (!isError) {
                try {
                    showProgressBar(false);
                    showErrorContent(false);
                    mActionBar.setTitle(mSearchText);
                    mActionBar.setSubtitle(getActivity().getResources().getString(R.string.title_vacancy) + ":" + mVacancyParser.getFoundedVacancies());
                    Log.d(ActivityVacancies.LOG_TAG, "Step 2 - Finished");
                    if (mVacanciesAdapter.mVacancies.size() > 0) {
                        mVacanciesAdapter.mVacancies.addAll(vacancies);
                    } else {
                        mVacanciesAdapter.mVacancies = vacancies;
                    }
                    isLoaded = false;
                    if (mPageOfVacancies == mVacancyParser.getPages()) {
                        mVacanciesAdapter.stopShowLoading();
                    }
                    mVacanciesAdapter.notifyDataSetChanged();
                    mVacanciesAdapter.notifyDataSetInvalidated();
                } catch (Exception e) {
                    showErrorContent(true);
                    e.printStackTrace();
                }
            } else {
                showErrorContent(true);
            }
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
            Log.d(ActivityVacancies.LOG_TAG, firstVisibleItem + visibleItemCount + " " + totalItemCount + mPageOfVacancies);
            if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0) {
                Log.d(ActivityVacancies.LOG_TAG, mVacancyParser.getPages() + " " + mPageOfVacancies);
                if (!isLoaded && (mVacancyParser.getPages() >= mPageOfVacancies)) {
                    displayVacancies(mSearchText, mSearchRegion, ++mPageOfVacancies);
                    isLoaded = true;
                }
            }
        }

    }

}

package com.ultimate39.android.androidcourse.ui.vacancy;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import org.apache.http.util.EntityUtils;

/**
 * Created by Влад on 18.05.14.
 */
public class FragmentDetailedVacancy extends Fragment {
    private View mProgressBar;
    private View mContent;
    private String mIdVacancy;
    private String mLogoUrl;
    private Vacancy mVacancy;
    private ViewHolderVacancy mViewHolder;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        Intent intent = getActivity().getIntent();
        mIdVacancy = intent.getStringExtra(ActivityVacancies.KEY_VACANCY_ID);
        mLogoUrl = intent.getStringExtra(ActivityVacancies.KEY_LOGO_URL);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_vacancy_detail, container, false);
        mProgressBar = root.findViewById(R.id.progressbar);
        mContent = root.findViewById(R.id.content);
        if (mVacancy == null) {
            new AsyncTaskVacancyDownloader().execute(mIdVacancy);
        } else {

            new ViewHolderVacancy(root).setContent(mVacancy);
        }
        return root;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
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
        private BitmapCacheDisplayer mDisplayer = BitmapCacheDisplayer.getInstance(getActivity(), "images");

        public ViewHolderVacancy() {
            initialize(getView());
        }

        public ViewHolderVacancy(View root) {
            initialize(root);
        }

        private void initialize(View root) {
            mVacancyName = (TextView) root.findViewById(R.id.tv_vacancy_detailed_name);
            mVacancySalary = (TextView) root.findViewById(R.id.tv_vacancy_detailed_salary);
            mVacancyEmployer = (TextView) root.findViewById(R.id.tv_vacancy_detailed_employer);
            mVacancyPublishedAt = (TextView) root.findViewById(R.id.tv_vacancy_detailed_published_at);
            mVacancyDescription = (WebView) root.findViewById(R.id.wv_vacancy_detailed_description);
            mVacancyLogo = (ImageView) root.findViewById(R.id.iv_vacancy_detailed_logo);
        }

        public void setContent(Vacancy vacancy) {
            mVacancyName.setText(Html.fromHtml(textToLink(vacancy.getName(), vacancy.getAlternateUrl())));
            mVacancyName.setMovementMethod(LinkMovementMethod.getInstance());

            if (vacancy.getSalary() != null) {
                mVacancySalary.setText(vacancy.getSalary());
            } else {
                mVacancySalary.setVisibility(View.GONE);
            }
            mVacancyEmployer.setText(vacancy.getEmployerName());
            mVacancyPublishedAt.setText(vacancy.getTimePublished());
            mDisplayer.displayImage(mVacancyLogo, mLogoUrl);
            setDescription(vacancy.getDescription());
        }

        private String textToLink (String text, String link) {
            String result = String.format("<a href=\"%s\">%s</a>", link, text);
            Log.d(ActivityVacancies.LOG_TAG, "REsult:" + result);
            return  result;
        }


        private void setDescription(String description) {
            final String mimeType = "text/html";
            final String encoding = "UTF-8";
            mVacancyDescription.loadDataWithBaseURL("", description, mimeType, encoding, "");
            mVacancyDescription.setBackgroundColor(0x00000000);
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
            JsonVacancyParser vacancyParser = new JsonVacancyParser(getActivity());
            Vacancy vacancy = vacancyParser.parseDetailedVacancy(source);
            Log.d(ActivityVacancies.LOG_TAG, "Step 1 - Finished");
            Log.d(ActivityVacancies.LOG_TAG, "Step 2 - Create ListView");
            return vacancy;
        }


        private String makeRequestForDetailedVacancy(String textSearch) {
            final String URL = "https://api.hh.ru/vacancies/" + textSearch;
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
            mVacancy = vacancy;
            mViewHolder = new ViewHolderVacancy();
            mViewHolder.setContent(vacancy);
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

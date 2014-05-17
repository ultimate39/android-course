package com.ultimate39.android.androidcourse.ui.vacancy;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import com.ultimate39.android.androidcourse.R;
import com.ultimate39.android.androidcourse.core.vacancy.Vacancy;
import com.ultimate39.android.androidcourse.ui.AsyncTaskVacancyDownloader;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by Влад on 17.05.14.
 */
public class FragmenVacancyListView extends Fragment {
    private ListView mListViewVacancies;
    private EditText mEditTextSearch;
    private Button mButton;
    private ListViewAdapterVacancy mVacanciesAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
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
            mVacanciesAdapter = new ListViewAdapterVacancy(getActivity(), new ArrayList<Vacancy>());
            mListViewVacancies.setAdapter(mVacanciesAdapter);
        }
        mListViewVacancies.setDrawingCacheEnabled(false);
        return root;
    }

    public void test(View view) {
        AsyncTaskVacancyDownloader task = new AsyncTaskVacancyDownloader();
        try {
            ArrayList<Vacancy> vacancyArrayList = task.execute(mEditTextSearch.getText().toString()).get();
            mVacanciesAdapter.mVacancies = vacancyArrayList;
            mVacanciesAdapter.notifyDataSetChanged();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

}

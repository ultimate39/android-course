package com.ultimate39.android.androidcourse.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.ultimate39.android.androidcourse.R;

/**
 * Created by Влад on 17.05.14.
 */
public class MainActivity extends Activity {
    public static final String KEY_TEXT = "text";
    public static final String KEY_REGION = "region";
    private EditText mSearchText;
    private EditText mSearchRegion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSearchText = (EditText) findViewById(R.id.et_vacancy_text_search);
        mSearchRegion = (EditText) findViewById(R.id.et_vacancy_region_search);
    }

    public void findVacancies(View view) {
        if(!mSearchText.getText().toString().isEmpty()) {
         Intent intent = new Intent(this, ActivityVacancies.class);
         intent.putExtra(KEY_TEXT, mSearchText.getText().toString());
         intent.putExtra(KEY_REGION, mSearchRegion.getText().toString());
         startActivity(intent);
        } else {
            Toast.makeText(this, getResources().getString(R.string.message_text_search_null), Toast.LENGTH_SHORT);
        }
    }
}

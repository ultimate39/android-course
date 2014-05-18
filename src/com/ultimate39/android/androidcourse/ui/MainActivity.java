package com.ultimate39.android.androidcourse.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;
import com.ultimate39.android.androidcourse.R;
import com.ultimate39.android.androidcourse.core.InternetUtils;
import com.ultimate39.android.androidcourse.core.area.Area;
import com.ultimate39.android.androidcourse.core.area.AreaDictionaryParser;
import com.ultimate39.android.androidcourse.core.area.XmlAreaDictionaryParser;
import com.ultimate39.android.androidcourse.ui.area.AreaAutoCompleteAdapter;
import com.ultimate39.android.androidcourse.ui.vacancy.ActivityVacancies;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Влад on 17.05.14.
 */
public class MainActivity extends Activity {
    public static final String KEY_TEXT = "text";
    public static final String KEY_AREA = "region";
    private EditText mSearchText;
    private AutoCompleteTextView mSearchRegion;
    private Area mArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSearchText = (EditText) findViewById(R.id.et_vacancy_text_search);
        mSearchRegion = (AutoCompleteTextView) findViewById(R.id.et_vacancy_region_search);
        AreaDictionaryParser parser = new XmlAreaDictionaryParser();
        ArrayList<Area> areas = null;
        try {
            areas = parser.parseAreas(getAssets().open("areas.xml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        AreaAutoCompleteAdapter areadAdapter = new AreaAutoCompleteAdapter(this, areas);
        mSearchRegion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mArea = (Area) view.getTag();
            }
        });
        mSearchRegion.setAdapter(areadAdapter);
    }

    public void findVacancies(View view) throws IOException {
        if (!InternetUtils.isInternetAvailable(getApplicationContext())) {
            Toast toast = Toast.makeText(this, getResources().getString(R.string.message_text_internet_unvailable), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else if (mSearchText.getText().toString().equals("null")) {
            Toast.makeText(this, getResources().getString(R.string.message_text_search_null), Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this, ActivityVacancies.class);
            intent.putExtra(KEY_TEXT, mSearchText.getText().toString());
            if (mArea != null) {
                intent.putExtra(KEY_AREA, mArea.getId());
            }
            startActivity(intent);
        }
    }
}

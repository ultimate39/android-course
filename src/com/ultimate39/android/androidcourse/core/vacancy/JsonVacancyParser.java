package com.ultimate39.android.androidcourse.core.vacancy;

import android.content.Context;
import android.util.Log;
import com.ultimate39.android.androidcourse.ui.vacancy.ActivityVacancies;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Влад on 15.05.14.
 */
public class JsonVacancyParser implements VacancyParser {
    private int foundedVacancies = 0;
    private Context mContext;

    public JsonVacancyParser(Context context) {
        mContext = context;
    }

    public Vacancy parseVacancy(JSONObject jsonVacancy) {
        Vacancy vacancy = new Vacancy();
        try {
            String name = jsonVacancy.getString("name");
            String id = jsonVacancy.getString("id");
            String publishedAt = jsonVacancy.getString("published_at");
            JSONObject jsonEmployer = (JSONObject) jsonVacancy.get("employer");
            String employerName = jsonEmployer.getString("name");
            String logoUrl = null;
            if (!jsonEmployer.isNull("logo_urls")) {
                logoUrl = jsonEmployer.getJSONObject("logo_urls").getString("240");
            }

            vacancy.setName(name);
            vacancy.setTimePublished(publishedAt);
            vacancy.setEmployerName(employerName);
            vacancy.setLogoUrl(logoUrl);
            vacancy.setId(id);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return vacancy;
    }

    @Override
    public ArrayList<Vacancy> parseVacancies(String source) {
        ArrayList<Vacancy> vacancies = new ArrayList<Vacancy>();
        try {
            JSONObject object = new JSONObject(source);
            System.out.println("TEXT REQUEST:" + source);
            foundedVacancies = object.getInt("found");
            JSONArray jsonVacancies = (JSONArray) object.get("items");
            for (int item = 0; item < jsonVacancies.length(); item++) {
                object = (JSONObject) jsonVacancies.get(item);
                vacancies.add(parseVacancy(object));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return vacancies;
    }

    @Override
    public Vacancy parseDetailedVacancy(String source) {
        Vacancy vacancy = new Vacancy();
        try {
            JSONObject jsonVacancy = new JSONObject(source);
            String name = jsonVacancy.getString("name");
            String description = jsonVacancy.getString("description");
            String alternateUrl = jsonVacancy.getString("alternate_url");
            String publishedAt = jsonVacancy.getString("published_at");
            String id = jsonVacancy.getString("id");

            JSONObject jsonEmployer = (JSONObject) jsonVacancy.get("employer");
            String employerName = jsonEmployer.getString("name");
            String logoUrl = null;
            if (!jsonEmployer.isNull("logo_urls")) {
                logoUrl = jsonEmployer.getJSONObject("logo_urls").getString("240");
            }

            String salary = "";
            if (!jsonVacancy.isNull("salary")) {
                salary = jsonSalaryToString(jsonVacancy.getJSONObject("salary"));
            }

            vacancy.setName(name);
            vacancy.setTimePublished(publishedAt);
            vacancy.setEmployerName(employerName);
            vacancy.setLogoUrl(logoUrl);
            vacancy.setId(id);
            vacancy.setDescription(description);
            vacancy.setSalary(salary);
            vacancy.setAlternateUrl(alternateUrl);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return vacancy;
    }

    @Override
    public int getFoundedVacancies() {
        return foundedVacancies;
    }

    private String jsonSalaryToString(JSONObject jsonSalary) {
        String salary = "";
        try {
            String from = jsonSalary.getString("from");
            String to = jsonSalary.getString("to");
            String currency = currencyConverter(jsonSalary.getString("currency"));
            if (!from.equals("null")) {
                salary += from;
                if (!to.equals("null")) {
                    salary += " - " + to + " " + currency;
                } else {
                    salary += " " + currency;
                }
            } else if (!to.equals("null")) {
                salary = " до " + to + currency;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return salary;
    }

    private String currencyConverter(String currency) {
        try {
            return mContext.getResources().getString(mContext.getResources().getIdentifier(currency, "string", mContext.getPackageName()));
        } catch (Exception e) {
            Log.e(ActivityVacancies.LOG_TAG, "Resource not found:" + currency);
            e.printStackTrace();
        }
        return null;
    }
}

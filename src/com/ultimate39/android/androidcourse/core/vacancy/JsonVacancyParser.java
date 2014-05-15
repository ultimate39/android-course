package com.ultimate39.android.androidcourse.core.vacancy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Влад on 15.05.14.
 */
public class JsonVacancyParser implements VacancyParser {


    public Vacancy parseVacancy(JSONObject jsonVacancy) {
        Vacancy vacancy = new Vacancy();

        try {
            String name = jsonVacancy.getString("name");
            String publishedAt = jsonVacancy.getString("published_at");
            JSONObject jsonEmployer = (JSONObject) jsonVacancy.get("employer");
            String employerName = jsonEmployer.getString("name");

            vacancy.setName(name);
            vacancy.setTimePublished(publishedAt);
            vacancy.setEmployerName(employerName);
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
}

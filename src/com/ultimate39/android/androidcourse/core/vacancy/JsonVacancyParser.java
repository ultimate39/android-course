package com.ultimate39.android.androidcourse.core.vacancy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Влад on 15.05.14.
 */
public class JsonVacancyParser implements VacancyParser {
    private int foundedVacancies = 0;

    public Vacancy parseVacancy(JSONObject jsonVacancy) {
        Vacancy vacancy = new Vacancy();
        try {
            String name = jsonVacancy.getString("name");
            String publishedAt = jsonVacancy.getString("published_at");
            JSONObject jsonEmployer = (JSONObject) jsonVacancy.get("employer");
            String employerName = jsonEmployer.getString("name");
            String id = jsonEmployer.getString("id");
            String logoUrl = null;
            if (!jsonEmployer.isNull("logo_urls")) {
                logoUrl = jsonEmployer.getJSONObject("logo_urls").getString("90");
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
            String publishedAt = jsonVacancy.getString("published_at");
            JSONObject jsonEmployer = (JSONObject) jsonVacancy.get("employer");
            String employerName = jsonEmployer.getString("name");
            String id = jsonEmployer.getString("id");
            String logoUrl = null;

            if (!jsonEmployer.isNull("logo_urls")) {
                logoUrl = jsonEmployer.getJSONObject("logo_urls").getString("240");
            }

            vacancy.setName(name);
            vacancy.setTimePublished(publishedAt);
            vacancy.setEmployerName(employerName);
            vacancy.setLogoUrl(logoUrl);
            vacancy.setId(id);
            vacancy.setDescription(description);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return vacancy;
    }

    @Override
    public int getFoundedVacancies() {
        return foundedVacancies;
    }
}

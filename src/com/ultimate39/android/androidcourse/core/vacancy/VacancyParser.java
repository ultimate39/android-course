package com.ultimate39.android.androidcourse.core.vacancy;

import java.util.ArrayList;

/**
 * Created by Влад on 15.05.14.
 */
public interface VacancyParser {

    public ArrayList<Vacancy> parseVacancies(String source);

    public Vacancy parseDetailedVacancy(String source);

    public int getFoundedVacancies();


}

package com.ultimate39.android.androidcourse.core.vacancy;

/**
 * Created by Влад on 15.05.14.
 */
public class Vacancy {
    private String name;
    private String timePublished;
    private String employerName;
    //private String logoUrl;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimePublished() {
        return timePublished;
    }

    public void setTimePublished(String timePublished) {
        this.timePublished = timePublished;
    }

    public String getEmployerName() {
        return employerName;
    }

    public void setEmployerName(String employerName) {
        this.employerName = employerName;
    }
}

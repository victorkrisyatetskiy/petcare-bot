package org.example.models;

import java.time.LocalDate;

public class Vaccination {

    private String name;
    private String date;
    private String nextDate;

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getNextDate() {
        return nextDate;
    }

    public Vaccination(String name, String date, String nextDate) {
        this.name = name;
        this.date = date;
        this.nextDate = nextDate;


    }
}

package org.example.models;

import java.time.LocalDate;
import java.util.UUID;

public class Vaccination {

    private String id;
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

    public String getId(){ return id;}

    public Vaccination(String name, String date, String nextDate) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.date = date;
        this.nextDate = nextDate;


    }
}

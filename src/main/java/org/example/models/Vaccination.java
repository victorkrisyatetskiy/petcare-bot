package org.example.models;

import java.time.LocalDate;

public class Vaccination {

    private String name;
    private LocalDate date;
    private LocalDate nextDate;

    public String getName() {
        return name;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalDate getNextDate() {
        return nextDate;
    }

    public Vaccination(String name, LocalDate date, LocalDate nextDate) {
        this.name = name;
        this.date = date;
        this.nextDate = nextDate;


    }
}

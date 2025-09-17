package org.example.models;

import java.time.LocalDate;

public class Medicine {

    private String name;
    private String dosage;
    private LocalDate schedule;
    private LocalDate nextDate;

    public Medicine(String name, String dosage, LocalDate schedule, LocalDate nextDate) {
        this.name = name;
        this.dosage = dosage;
        this.schedule = schedule;
        this.nextDate = nextDate;
    }

    public String getName() {
        return name;
    }

    public String getDosage() {
        return dosage;
    }

    public LocalDate getSchedule() {
        return schedule;
    }

    public LocalDate getNextDate() {
        return nextDate;
    }
}

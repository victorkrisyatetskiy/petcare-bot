package org.example.models;

import java.time.LocalDate;
import java.util.UUID;

public class Medicine {

    private String id;
    private String name;
    private String dosage;
    private String schedule;
    private String nextDate;

    public Medicine(String name, String dosage, String schedule, String nextDate) {
        this.id = UUID.randomUUID().toString();
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

    public String getSchedule() {
        return schedule;
    }

    public String getNextDate() {
        return nextDate;
    }

    public String getId() { return id;}
}

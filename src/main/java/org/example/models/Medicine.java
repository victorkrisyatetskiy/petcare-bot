package org.example.models;

public class Medicine {

    private String id;
    private String name;
    private String dosage;
    private String schedule;
    private String nextDate;

    public Medicine(String name, String dosage, String schedule, String nextDate) {
        this.name = name;
        this.dosage = dosage;
        this.schedule = schedule;
        this.nextDate = nextDate;
    }

    public Medicine(String id, String name, String dosage, String schedule, String nextDate) {
        this.id = id;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

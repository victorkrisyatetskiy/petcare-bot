package org.example.models;

import java.time.LocalDate;
import java.util.UUID;

public class Pet {
    private String id;
    private String name;
    private String type;
    private String breed;
    private String birthDate;

    public Pet(String name, String type, String breed, String birthDate) {
        this.name = name;
        this.type = type;
        this.breed = breed;
        this.birthDate = birthDate;
    }

    public Pet(String id, String name, String type, String breed, String birthDate) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.breed = breed;
        this.birthDate = birthDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getBreed() {
        return breed;
    }

    public String getBirthDate() {
        return birthDate;
    }
}

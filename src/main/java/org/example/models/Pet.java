package org.example.models;

import java.time.LocalDate;

public class Pet {
    private String name;
    private String type;
    private String breed;
    private LocalDate birthDate;

    public Pet(String name, String type, String breed, LocalDate birthDate) {
        this.name = name;
        this.type = type;
        this.breed = breed;
        this.birthDate = birthDate;
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

    public LocalDate getBirthDate() {
        return birthDate;
    }
}

package org.example;

import org.example.models.Medicine;
import org.example.models.Pet;
import org.example.models.Vaccination;
import org.example.services.DatabaseService;

public class TestDB {
    public static void main(String[] args) {
        DatabaseService db = new DatabaseService();

        Pet testPet = new Pet("Pet", "Dog", "Terrier", "2022-02-02");
        db.savePet(123456, testPet);
        System.out.println("Pet saved");

        Pet retrievedPet = db.getPet(123456);
        if (retrievedPet != null) {
            System.out.println("Pet retrieved: " + retrievedPet.getName());
        } else {
            System.out.println("Pet not found");
        }

        Medicine testMed = new Medicine("TestMed", "1 pill", "10:00", "2024-12-31");
        db.saveMedicine(123456789L, testMed);
        System.out.println("Medicine saved");

        var medicines = db.getMedicine(123456789L);
        System.out.println("Medicines count: " + medicines.size());

        Vaccination testVacc = new Vaccination("TestVacc", "2024-01-01", "2025-01-01");
        db.saveVaccination(123456789L, testVacc);
        System.out.println("Vaccination saved");

        db.deleteMedicine(123456789L, 0);
        System.out.println("Medicine deletion attempted");

        System.out.println("All tests completed!");
    }
}

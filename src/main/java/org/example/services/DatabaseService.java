package org.example.services;

import org.example.models.Pet;

import java.sql.*;

public class DatabaseService {
    private static final String DB_URL = "jdbc:sqlite:viktor_pet_care_bot.db";

    public DatabaseService() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        try (Connection con = DriverManager.getConnection(DB_URL);
             Statement statement = con.createStatement()) {
            String createPetsTable = """
                    CREATE TABLE IF NOT EXIST pets (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        chat_id INTEGER NOT NULL,
                        name TEXT NOT NULL,
                        type TEXT NOT NULL,
                        breed TEXT NOT NULL,
                        birth_date TEXT NOT NULL
                        )
                    """;

            String createMedicineTable = """
                    CREATE TABLE IF NOT EXIST medicines (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        chat_id INTEGER NOT NULL,
                        name TEXT NOT NULL,
                        dosage TEXT NOT NULL,
                        time TEXT NOT NULL,
                        next_date TEXT NOT NULL
                        )
                    """;

            String createVaccinationTable = """
                    CREATE TABLE IF NOT EXIST vaccinations (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        chat_id INTEGER NOT NULL,
                        name TEXT NOT NULL,
                        date TEXT NOT NULL,
                        next_date TEXT NOT NULL
                         )
                    """;

            statement.execute(createPetsTable);
            statement.execute(createMedicineTable);
            statement.execute(createVaccinationTable);

        } catch (SQLException e){
            e.getMessage();
        }
    }

    public void savePet(long chatId, Pet pet){
        String sql = "INSERT INTO pets (chat_id, name, type, breed, birth_date) VALUES(?, ?, ?, ?, ?)";
        try(Connection con = DriverManager.getConnection(DB_URL);
            PreparedStatement preparedStatement = con.prepareStatement(sql)){
            preparedStatement.setLong(1, chatId);
            preparedStatement.setString(2, pet.getName());
            preparedStatement.setString(3, pet.getType());
            preparedStatement.setString(4, pet.getBreed());
            preparedStatement.setString(5, pet.getBirthDate());
            preparedStatement.executeUpdate();
        }catch (SQLException e){
            e.getMessage();
        }
    }
}

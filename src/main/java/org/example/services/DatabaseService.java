package org.example.services;

import org.example.models.Medicine;
import org.example.models.Pet;
import org.example.models.Vaccination;

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
                        schedule TEXT NOT NULL,
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

        } catch (SQLException e) {
            e.getMessage();
        }
    }

    public void savePet(long chatId, Pet pet) {
        String sql = "INSERT INTO pets (chat_id, name, type, breed, birth_date) VALUES(?, ?, ?, ?, ?)";
        try (Connection con = DriverManager.getConnection(DB_URL);
             PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setLong(1, chatId);
            preparedStatement.setString(2, pet.getName());
            preparedStatement.setString(3, pet.getType());
            preparedStatement.setString(4, pet.getBreed());
            preparedStatement.setString(5, pet.getBirthDate());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.getMessage();
        }
    }

    public Pet getPet(long chatId) {
        String sql = "SELECT * FROM pets WHERE chat_id = ?";
        try (
                Connection con = DriverManager.getConnection(DB_URL);
                PreparedStatement preparedStatement = con.prepareStatement(sql)) {

            preparedStatement.setLong(1, chatId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return new Pet(
                        resultSet.getString("name"),
                        resultSet.getString("type"),
                        resultSet.getString("breed"),
                        resultSet.getString("birth_date")
                );
            }
        } catch (SQLException e) {
            e.getMessage();
        }
        return null;
    }

    public void saveMedicine(long chatId, Medicine medicine) {
        String sql = "INSERT INTO medicines (chat_id, name, dosage, schedule, next_date) VALUES(?, ?, ?, ?, ?)";
        try (Connection con = DriverManager.getConnection(DB_URL);
             PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setLong(1, chatId);
            preparedStatement.setString(2, medicine.getName());
            preparedStatement.setString(3, medicine.getDosage());
            preparedStatement.setString(4, medicine.getSchedule());
            preparedStatement.setString(5, medicine.getNextDate());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.getMessage();
        }
    }

    public Medicine getMedicine(long chatId) {
        String sql = "SELECT * FROM medicines WHERE chat_id = ?";
        try (Connection con = DriverManager.getConnection(DB_URL);
             PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setLong(1, chatId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new Medicine(
                        resultSet.getString("name"),
                        resultSet.getString("dosage"),
                        resultSet.getString("schedule"),
                        resultSet.getString("next_date")
                );
            }
        } catch (SQLException e) {
            e.getMessage();
        }
        return null;
    }

    public void saveVaccination(long chatId, Vaccination vaccination){
        String sql = "SELECT * FROM vaccinations WHERE chat_id = ?";
        try (Connection con = DriverManager.getConnection(DB_URL);
             PreparedStatement preparedStatement = con.prepareStatement(sql)) {
                 preparedStatement.setLong(1, chatId);
                 preparedStatement.setString(2, vaccination.getName());
                 preparedStatement.setString(3, vaccination.getDate());
                 preparedStatement.setString(4, vaccination.getNextDate());
                 preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.getMessage();
        }
    }

    public Vaccination getVaccination(long chatId){
        String sql = "INSERT INTO medicines (chat_id, name, date, next_date) VALUES(?, ?, ?, ?)";
        try (Connection con = DriverManager.getConnection(DB_URL);
             PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setLong(1, chatId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new Vaccination(
                        resultSet.getString("name"),
                        resultSet.getString("date"),
                        resultSet.getString("next_date")
                );
            }
        } catch (SQLException e) {
            e.getMessage();
        }
        return null;
    }
}

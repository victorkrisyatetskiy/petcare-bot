package org.example.services;

import org.example.models.Medicine;
import org.example.models.Pet;
import org.example.models.Vaccination;

import javax.management.DescriptorRead;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseService {
    private static final String DB_URL = "jdbc:sqlite:viktor_pet_care_bot.db";

    public DatabaseService() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        try (Connection con = DriverManager.getConnection(DB_URL);
             Statement statement = con.createStatement()) {
            String createPetsTable = """
                    CREATE TABLE IF NOT EXISTS pets (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        chat_id INTEGER NOT NULL,
                        name TEXT NOT NULL,
                        type TEXT NOT NULL,
                        breed TEXT NOT NULL,
                        birth_date TEXT NOT NULL
                        )
                    """;

            String createMedicineTable = """
                    CREATE TABLE IF NOT EXISTS medicines (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        chat_id INTEGER NOT NULL,
                        name TEXT NOT NULL,
                        dosage TEXT NOT NULL,
                        schedule TEXT NOT NULL,
                        next_date TEXT NOT NULL
                        )
                    """;

            String createVaccinationTable = """
                    CREATE TABLE IF NOT EXISTS vaccinations (
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }

    public List<Medicine> getMedicine(long chatId) {
        List<Medicine> medicines = new ArrayList<>();
        String sql = "SELECT * FROM medicines WHERE chat_id = ? ORDER BY id";
        try (Connection con = DriverManager.getConnection(DB_URL);
             PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setLong(1, chatId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
              Medicine medicine = new Medicine(
                      resultSet.getString("id"),
                        resultSet.getString("name"),
                        resultSet.getString("dosage"),
                        resultSet.getString("schedule"),
                        resultSet.getString("next_date")
                );
              medicines.add(medicine);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return medicines;
    }

    public void saveVaccination(long chatId, Vaccination vaccination){
        String sql = "INSERT INTO vaccinations (chat_id, name, date, next_date) VALUES(?, ?, ?, ?)";
        try (Connection con = DriverManager.getConnection(DB_URL);
             PreparedStatement preparedStatement = con.prepareStatement(sql)) {
                 preparedStatement.setLong(1, chatId);
                 preparedStatement.setString(2, vaccination.getName());
                 preparedStatement.setString(3, vaccination.getDate());
                 preparedStatement.setString(4, vaccination.getNextDate());
                 preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Vaccination> getVaccination(long chatId){
        List<Vaccination> vaccinations = new ArrayList<>();
        String sql = "SELECT * FROM vaccinations WHERE chat_id = ? ORDER BY id";
        try (Connection con = DriverManager.getConnection(DB_URL);
             PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setLong(1, chatId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Vaccination vaccination = new Vaccination(
                        resultSet.getString("id"),
                        resultSet.getString("name"),
                        resultSet.getString("date"),
                        resultSet.getString("next_date")
                );
                vaccinations.add(vaccination);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vaccinations;
    }

    public void deletePet(long chatId){
        String sql = "DELETE FROM pets WHERE chat_id = ?";
        try(Connection con = DriverManager.getConnection(DB_URL);
        PreparedStatement preparedStatement = con.prepareStatement(sql)){
            preparedStatement.setLong(1, chatId);
            int rowDeleted = preparedStatement.executeUpdate();
            if (rowDeleted > 0){
                System.out.println("Pet deleted");
            }else {
                System.out.println("No pet of user");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteMedicine(long chatId, int index){
        String sql = "DELETE FROM medicines WHERE chat_id = (" +
                "SELECT id FROM medicines " +
                "WHERE chat_id = ? " +
                "ORDER BY id " +
                "LIMIT 1 OFFSET ?" +
                ")";

        try(Connection con = DriverManager.getConnection(DB_URL);
        PreparedStatement preparedStatement = con.prepareStatement(sql)){

            preparedStatement.setLong(1, chatId);
            preparedStatement.setInt(2, index);
            int rowDeleted = preparedStatement.executeUpdate();
            if(rowDeleted > 0){
                System.out.println("Medicine deleted");
            }else {
                System.out.println("Mo medicine with index " + index);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteVaccination(long chatId, int index){
        String sql = "DELETE FROM vaccinations WHERE chat_id = (" +
                "SELECT id FROM vaccinations " +
                "WHERE chat_id = ? " +
                "ORDER BY id " +
                "LIMIT 1 OFFSET ?" +
                ")";
        try(Connection con = DriverManager.getConnection(DB_URL);
        PreparedStatement preparedStatement = con.prepareStatement(sql)){
            preparedStatement.setLong(1, chatId);
            preparedStatement.setInt(2, index);
            int rowDeleted = preparedStatement.executeUpdate();
            if (rowDeleted > 0){
                System.out.println("Vaccination deleted");
            }else {
                System.out.println("No vaccination with index " + index);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

package org.example;

import org.example.models.Medicine;
import org.example.models.Pet;
import org.example.models.Vaccination;
import org.example.services.DatabaseService;
import org.example.services.ReminderServices;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class PetCareBot extends TelegramLongPollingBot {


    private Pet currentPet;
    private Vaccination currentVaccination;
    private Medicine currentMedicine;
    private DatabaseService databaseService;

    private String botToken;
    private String botUsername;


    private ReminderServices reminderServices;


    public PetCareBot() {
        try {
            loadConfig();
            this.databaseService = new DatabaseService();
            this.reminderServices = new ReminderServices(this, databaseService);
            reminderServices.start();
            System.out.println("PetCareBot started successfully!");
        } catch (Exception e) {
            System.err.println("Failed to start PetCareBot: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }


    private void loadConfig() {
        this.botToken = System.getenv("BOT_TOKEN");
        this.botUsername = System.getenv("BOT_USERNAME");
        if (this.botToken == null || this.botUsername == null) {
            try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
                if (input == null) {
                    throw new RuntimeException("Config file not found and no environment variables set");
                }

                Properties prop = new Properties();
                prop.load(input);

                this.botToken = prop.getProperty("bot.token");
                this.botUsername = prop.getProperty("bot.username");

                System.out.println("Config loaded from file");
            } catch (Exception e) {
                throw new RuntimeException("Error loading config: " + e.getMessage());
            }
        } else {
            System.out.println("Config loaded from environment variables");
        }
        if (this.botToken == null || this.botUsername == null) {
            throw new RuntimeException("Bot token and username must be set via environment variables or config file");
        }
        System.out.println("Bot configured: " + this.botUsername);
    }


    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();


            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(chatId));
            message.setParseMode("HTML");

            if (messageText.startsWith("/start")) {
                    message.setText("<b>Welcome! I'm your Pet Care Assistant!</b>\n\n" +
                            "I can help you to manage your pet's health.\n\n" +
                            "<b>What would you like to do</b>\n\n" +
                            "Add your pet -> /addnewpet\n" +
                            "Add medicine -> /addmedicine\n" +
                            "Add vaccination -> /addvaccination\n" +
                            "See all commands -> /help"
                    );

            } else if (messageText.equals("/help")) {
                message.setText("<b>Pet Care Assistant - Command list</b>\n\n" +
                        "Main commands:\n\n" +
                        "/start - start the work\n" +
                        "/help\n\n" +
                        "<b>Pet commands</b>\n" +
                        "/addnewpet - add new pet\n" +
                        "/mypet - information about your pet\n\n" +
                        "<b>Vaccinations</b>\n" +
                        "/addvaccination - add date of new vaccination\n" +
                        "/myvaccinations - show vaccinations\n\n" +
                        "<b>Medicines</b>\n" +
                        "/addmedicine - add new Medicine\n" +
                        "/mymedicines - show medicines\n" +
                        "<b>Delete</b>\n" +
                        "/deletemedicine - remove medicine\n" +
                        "/deletevaccination - remove vaccination");
            } else if (messageText.equals("/addnewpet")) {
                message.setText("Enter our pet's details in format:\n" +
                        "<code>Name, Type, Breed, YYYY-MM-DD</code>\n\n");
            } else if (messageText.equals("/addvaccination")) {
                message.setText("Enter vaccination details in format:\n" +
                        "<code>Name, YYYY-MM-DD, YYYY-MM-DD</code>\n\n");
            } else if (messageText.equals("/addmedicine")) {
                message.setText("Enter medicine details in format:\n" +
                        "<code>Name, dosage, HH:MM, YYYY-MM-DD</code>\n\n");
            } else if (
                    messageText.matches(".+, .+, \\d{2}:\\d{2}, \\d{4}-\\d{2}-\\d{2}")) {
                try {
                    String[] parts = messageText.split(", ");
                    String name = parts[0];
                    String dosage = parts[1];
                    String schedule = parts[2];
                    String nextDate = parts[3];

                    currentMedicine = new Medicine(name, dosage, schedule, nextDate);

                    message.setText("Medicine data received!\n\n" +
                            "Name: " + name + "\n" +
                            "Dosage: " + dosage + "\n" +
                            "Time: " + schedule + "\n" +
                            "Next time: " + nextDate + "\n" +
                            "For saving use: /savemed");


                } catch (Exception e) {
                    message.setText("Mistake. Use format: /addnewpet");
                }
            } else if (messageText.matches(".+, \\d{4}-\\d{2}-\\d{2}, \\d{4}-\\d{2}-\\d{2}")) {
                try {
                    String[] parts = messageText.split(", ");
                    String name = parts[0];
                    String date = parts[1];
                    String nextDate = parts[2];

                    currentVaccination = new Vaccination(name, date, nextDate);

                    message.setText("Vaccination data received!\n\n" +
                            "Name: " + name + "\n" +
                            "Date: " + date + "\n" +
                            "Next date: " + nextDate + "\n" +
                            "For saving use: /savevacc");
                } catch (Exception e) {
                    message.setText("Mistake. Use format: /addvaccination");
                }
            } else if (messageText.matches(".+, .+, .+, \\d{4}-\\d{2}-\\d{2}")) {
                try {
                    String[] parts = messageText.split(", ");
                    String name = parts[0];
                    String type = parts[1];
                    String breed = parts[2];
                    String birthDate = parts[3];

                    currentPet = new Pet(name, type, breed, birthDate);

                    message.setText("Pet data received!\n\n" +
                            "Name: " + name + "\n" +
                            "Type: " + type + "\n" +
                            "Breed: " + breed + "\n" +
                            "Date of birth: " + birthDate + "\n\n" +
                            "For saving use: /savepet");
                } catch (Exception e) {
                    message.setText("Mistake. Use format: /addmedicine");
                }
            } else if (messageText.equals("/savepet")) {
                if (currentPet != null) {
                    databaseService.savePet(chatId, currentPet);
                    message.setText("Pet saved! Use /mypet to view");
                    currentPet = null;
                } else {
                    message.setText("No pet data to save.");
                }
            } else if (messageText.equals("/mypet")) {
                Pet pet = databaseService.getPet(chatId);
                if (pet != null) {
                    message.setText("Information about your pet:\n\n" +
                            "Name: " + pet.getName() + "\n" +
                            "Type: " + pet.getType() + "\n" +
                            "Breed: " + pet.getBreed() + "\n" +
                            "Date of birth: " + pet.getBirthDate());
                } else {
                    message.setText("No pets with your ID. Use /addnewpet");
                }
            } else if (messageText.equals("/savevacc")) {
                if (currentVaccination != null) {
                    databaseService.saveVaccination(chatId, currentVaccination);
                    message.setText("Vaccination saved!");
                    currentVaccination = null;
                } else {
                    message.setText("No vaccination data to save.");
                }
            } else if (messageText.equals("/savemed")) {
                if (currentMedicine != null) {
                    databaseService.saveMedicine(chatId, currentMedicine);
                    message.setText("Medicine saved!");
                    currentMedicine = null;
                } else {
                    message.setText("No medicine data to save.");
                }

            } else if (messageText.equals("/myvaccinations")) {
                List<Vaccination> vaccinations = databaseService.getVaccination(chatId);
                if (vaccinations != null && !vaccinations.isEmpty()) {
                    StringBuilder response = new StringBuilder("Your vaccinations:\n\n");
                    for (Vaccination vacc : vaccinations) {
                        response.append("*").append(vacc.getName()).append(" - ")
                                .append(vacc.getDate()).append("\n").append(" next: ")
                                .append(vacc.getNextDate()).append("\n");
                    }
                    message.setText(response.toString());
                } else {
                    message.setText("No vaccinations with your ID. Use /addvaccination");
                }

            } else if (messageText.equals("/mymedicines")) {
                List<Medicine> medicines = databaseService.getMedicine(chatId);
                if (medicines != null && !medicines.isEmpty()) {
                    StringBuilder response = new StringBuilder("Your medicines:\n\n");
                    for (Medicine medicine : medicines) {
                        response.append("*").append(medicine.getName()).append(" - ")
                                .append(medicine.getDosage()).append(" at ")
                                .append(medicine.getSchedule()).append("\n").append(" next: ")
                                .append(medicine.getNextDate()).append("\n");
                    }
                    message.setText(response.toString());
                } else {
                    message.setText("No medicines with your ID. Use /addmedicine");
                }

            } else if (messageText.equals("/deletemedicine")) {
                List<Medicine> medicines = databaseService.getMedicine(chatId);
                if (medicines != null && !medicines.isEmpty()) {
                    StringBuilder response = new StringBuilder("Select medicine fo delete: \n\n");
                    for (int i = 0; i < medicines.size(); i++) {
                        Medicine med = medicines.get(i);
                        response.append(i + 1).append(". ").append(med.getName()).append("\n");
                    }
                    response.append("\nReply with number to delete");
                    message.setText(response.toString());
                } else {
                    message.setText("No medicine to delete");
                }

            } else if (messageText.equals("/deletevaccination")) {
                List<Vaccination> vaccinations = databaseService.getVaccination(chatId);
                if (vaccinations != null && !vaccinations.isEmpty()) {
                    StringBuilder response = new StringBuilder("Select vaccination to delete: \n\n");
                    for (int i = 0; i < vaccinations.size(); i++) {
                        Vaccination vacc = vaccinations.get(i);
                        response.append(i + 1).append(". ").append(vacc.getName());

                    }
                    response.append("\nReply with the number to delete");
                    message.setText(response.toString());
                } else {
                    message.setText("No vaccination to delete");
                }

            } else if (messageText.matches("\\d+")) {
                {
                    try {
                        int index = Integer.parseInt(messageText) - 1;
                        System.out.println("Attempting to delete item at index: " + index);
                        List<Medicine> medicines = databaseService.getMedicine(chatId);
                        if (medicines != null && index >= 0 && index < medicines.size()) {
                            String medName = medicines.get(index).getName();
                            databaseService.deleteMedicine(chatId, index);
                            message.setText("Medicine: " + medName + " deleted");
                            execute(message);
                            return;
                        }

                        List<Vaccination> vaccinations = databaseService.getVaccination(chatId);
                        if (vaccinations != null && index >= 0 && index < vaccinations.size()) {
                            String vacName = vaccinations.get(index).getName();
                            databaseService.deleteVaccination(chatId, index);
                            message.setText("Vaccination: " + vacName + " deleted.");
                            execute(message);
                            return;
                        }
                        message.setText("Invalid number");
                        execute(message);

                    } catch (NumberFormatException e) {
                        message.setText("Please enter a number");
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }

            } else {
                message.setText("I don't understand this command yet");
            }


            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }
}

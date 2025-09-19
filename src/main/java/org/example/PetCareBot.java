package org.example;

import org.example.models.Medicine;
import org.example.models.Pet;
import org.example.models.Vaccination;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class PetCareBot extends TelegramLongPollingBot {

    private HashMap<Long, Pet> usersPets = new HashMap<>();
    private HashMap<Long, ArrayList<Vaccination>> usersVacc = new HashMap<>();
    private HashMap<Long, ArrayList<Medicine>> usersMeds = new HashMap<>();

    private Pet currentPet;
    private Vaccination currentVaccination;
    private Medicine currentMedicine;

    private String botToken;
    private String botUsername;

    public PetCareBot() {
        loadConfig();
    }

    private void loadConfig() {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config.properties");
            Properties prop = new Properties();
            prop.load(inputStream);

            botToken = prop.getProperty("bot.token");
            botUsername = prop.getProperty("bot.username");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

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

            if (messageText.equals("/start")) {
                message.setText("Hello! I am your bot for pet care!");
            } else if (messageText.equals("/help")) {
                message.setText("<b>Pet Care Assistant</b>\n\n" +
                        "Available commands:\n\n" +
                        "/start - start the work\n" +
                        "/help\n\n" +
                        "<b>Pet</b>\n" +
                        "/addnewpet - add new pet\n" +
                        "/mypet - information about your pet\n\n" +
                        "<b>Reminders</b>\n" +
                        "/addreminder - add new reminder\n" +
                        "*/addvaccination  - add date of new vaccination\n" +
                        "*/addmedecine - add new Medicine");
            } else if (messageText.equals("/addnewpet")) {
                message.setText("Enter our pet's details in format:\n" +
                        "<code>Name, Type, Breed, YYYY-MM-DD</code>\n\n");
            } else if (messageText.equals("/mypet")) {
                Pet pet = usersPets.get(chatId);
                if (pet != null){
                message.setText("Information about your pet:\n\n" +
                        "Name: " + pet.getName() + "\n" +
                        "Type: " + pet.getType() + "\n" +
                        "Breed: " + pet.getBreed() + "\n" +
                        "Date of birth: " + pet.getBirthDate());
                }else {
                    message.setText("No pets with your ID. Use /addnewpet");
                }
            } else if (messageText.equals("/addvaccination")) {
                message.setText("Enter vaccination details in format:\n" +
                        "<code>Name, YYYY-MM-DD, YYYY-MM-DD</code>\n\n");
            } else if (messageText.equals("/addmedecine")) {
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
                    usersPets.put(chatId, currentPet);
                    message.setText("Pet saved! Use /mypet to view");
                    currentPet = null;
                } else {
                    message.setText("No pet data to save.");
                }
            } else if (messageText.equals("/savevacc")) {
                if (currentVaccination != null) {
                    usersVacc.putIfAbsent(chatId, new ArrayList<>());
                    usersVacc.get(chatId).add(currentVaccination);
                    message.setText("Vaccination saved!");
                    currentVaccination = null;
                } else {
                    message.setText("No vaccination data to save.");
                }
            } else if (messageText.equals("/savemed")) {
                if (currentMedicine != null) {
                    usersMeds.putIfAbsent(chatId, new ArrayList<>());
                    usersMeds.get(chatId).add(currentMedicine);
                    message.setText("Medicine saved!");
                    currentMedicine = null;
                } else {
                    message.setText("No medicine data to save.");
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

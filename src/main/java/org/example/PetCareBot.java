package org.example;

import org.example.models.Medicine;
import org.example.models.Pet;
import org.example.models.Vaccination;
import org.example.services.ReminderServices;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.InputStream;
import java.util.*;

public class PetCareBot extends TelegramLongPollingBot {

    private Map<Long, Pet> usersPets = new HashMap<>();
    private Map<Long, List<Vaccination>> usersVacc = new HashMap<>();
    private Map<Long, List<Medicine>> usersMeds = new HashMap<>();

    private Pet currentPet;
    private Vaccination currentVaccination;
    private Medicine currentMedicine;

    private String botToken;
    private String botUsername;

    private ReminderServices reminderServices;

    public PetCareBot() {
        loadConfig();
        this.reminderServices = new ReminderServices(this, usersMeds, usersVacc);
        reminderServices.start();
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
                        "<b>Vaccinations</b>\n" +
                        "/addvaccination  - add date of new vaccination\n" +
                        "/myvaccinations - show vaccinations\n" +
                        "<b>Medicines</b>\n" +
                        "/addmedecine - add new Medicine\n" +
                        "/mymedicines - show medicines\n" +
                        "<b>Delete</b>\n" +
                        "/deletemedicine - remove medicine\n" +
                        "/deletevacination - remove vaccination");
            } else if (messageText.equals("/addnewpet")) {
                message.setText("Enter our pet's details in format:\n" +
                        "<code>Name, Type, Breed, YYYY-MM-DD</code>\n\n");
            } else if (messageText.equals("/mypet")) {
                Pet pet = usersPets.get(chatId);
                if (pet != null) {
                    message.setText("Information about your pet:\n\n" +
                            "Name: " + pet.getName() + "\n" +
                            "Type: " + pet.getType() + "\n" +
                            "Breed: " + pet.getBreed() + "\n" +
                            "Date of birth: " + pet.getBirthDate());
                } else {
                    message.setText("No pets with your ID. Use /addnewpet");
                }
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

            } else if (messageText.equals("/myvaccinations")) {
                List<Vaccination> vaccinations = usersVacc.get(chatId);
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
                List<Medicine> medicines = usersMeds.get(chatId);
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
                List<Medicine> medicines = usersMeds.get(chatId);
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
                List<Vaccination> vaccinations = usersVacc.get(chatId);
                if (vaccinations != null && !vaccinations.isEmpty()) {
                    StringBuilder response = new StringBuilder("Select vaccination to delete: \n\n");
                    for (int i = 0; i < vaccinations.size(); i++) {
                        Vaccination vacc = vaccinations.get(i);
                        response.append(i + 1).append(". ").append(vacc.getNextDate());

                    }
                    response.append("\nReply with the number to delete");
                    message.setText(response.toString());
                } else {
                    message.setText("No vaccination to delete");
                }

            } else if (messageText.matches("\\d+") && usersMeds.containsKey(chatId)) {
                {
                    try {
                        int index = Integer.parseInt(messageText) - 1;
                        List<Medicine> medicines = usersMeds.get(chatId);
                        if (index >= 0 && index < medicines.size()) {
                            Medicine removed = medicines.remove(index);
                            message.setText("Removed: " + removed.getName());
                        } else {
                            message.setText("Invalid number");
                        }
                    } catch (NumberFormatException e) {
                        message.setText("Please enter a number");
                    }
                }

            } else if (messageText.matches("\\d+") && usersVacc.containsKey(chatId)) {
                {
                    try{
                        int index = Integer.parseInt(messageText) - 1;
                        List<Vaccination> vaccinations = usersVacc.get(chatId);
                        if (index >= 0 && index < vaccinations.size()){
                            Vaccination removed = vaccinations.remove(index);
                            message.setText("Removed: " + removed.getName());
                        } else {
                            message.setText("Invalid number");
                        }
                    } catch (NumberFormatException){
                        message.setText("Please enter a number");
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

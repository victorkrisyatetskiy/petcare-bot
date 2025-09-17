package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class PetCareBot extends TelegramLongPollingBot {
    @Override
    public String getBotToken() {
        return "8080774321:AAEgCZFf4g4sVxF3-sVAWCHbR_2SfUJSCV0";
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
                message.setText("Information about your pet:\n\n" +
                        "<code>Name: Name\nType: Type\nBreed: Breed\nDate of birth: 2022-22-22</code>");
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
                message.setText("Pet saved! Use /mypet to view");
            } else if (messageText.equals("/savevacc")) {
                message.setText("Vaccination saved!");
            } else if (messageText.equals("/savemed")) {
                message.setText("Medicine saved!");
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
        return "viktor_pet_care_bot";
    }
}

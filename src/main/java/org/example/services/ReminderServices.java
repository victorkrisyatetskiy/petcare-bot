package org.example.services;

import org.example.PetCareBot;
import org.example.models.Medicine;
import org.example.models.Vaccination;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ReminderServices {

    private final PetCareBot bot;
    private final DatabaseService databaseService;
    private Timer timer;


    public ReminderServices(PetCareBot bot, DatabaseService databaseService) {
        this.bot = bot;
        this.databaseService = databaseService;
    }

    public void start() {
        timer = new Timer();
        timer.schedule(new ReminderTask(), 0, 86400000);
    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
        }
    }

    private class ReminderTask extends TimerTask {
        @Override
        public void run() {
            checkReminders();
        }

        private void checkReminders() {
            try {
                LocalDate today = LocalDate.now();
                System.out.println("Checking reminders for: "  + today);
                List<Long> allUsers = databaseService.getAllUsersWithData();
                for (Long chatId: allUsers){
                    checkUserReminders(chatId, today);
                }
                System.out.println("Checked reminders for " + allUsers.size() + " users.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void checkUserReminders(Long chatId, LocalDate today) {
            try {
                checkMedicines(chatId, today);
                checkVaccinations(chatId, today);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void checkVaccinations(Long chatId, LocalDate today) {
            List<Vaccination> vaccinations = databaseService.getVaccination(chatId);
            if (vaccinations != null){
                for (Vaccination vacc : vaccinations){
                    LocalDate nextDay = LocalDate.parse(vacc.getNextDate());
                    if (nextDay.equals(today) || nextDay.isBefore(today)){
                        sendReminder(chatId, "Time for vaccination" + vacc.getName());
                    }
                }
            }
        }

        private void checkMedicines(Long chatId, LocalDate today) {

            List<Medicine> medicines = databaseService.getMedicine(chatId);
            if (medicines != null){
                for (Medicine med: medicines){
                    LocalDate nextDay = LocalDate.parse(med.getNextDate());
                    if (nextDay.equals(today) || nextDay.isBefore(today)){
                        sendReminder(chatId,"Time for medicine: " + med.getName() + med.getDosage() + "at " + med.getSchedule());
                    }
                }
            }
        }


        private void sendReminder(Long chatId, String text) {
            try {
                SendMessage message = new SendMessage();
                message.setChatId(chatId.toString());
                message.setText("Reminder: " + text);
                bot.execute(message);
            }catch (TelegramApiException e){
                e.getMessage();
            }
        }
    }

}

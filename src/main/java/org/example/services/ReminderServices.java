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
    private final Map<Long, List<Medicine>> usersMedicines;
    private final Map<Long, List<Vaccination>> usersVaccinations;
    private Timer timer;


    public ReminderServices(PetCareBot bot, Map<Long, List<Medicine>> usersMedicines, Map<Long, List<Vaccination>> usersVaccinations) {
        this.bot = bot;
        this.usersMedicines = usersMedicines;
        this.usersVaccinations = usersVaccinations;
    }

    public void start() {
        timer = new Timer();
        timer.schedule(new ReminderTask(), 0, 60000);
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
                for (Long chatId : usersMedicines.keySet()) {
                    checkMedicineReminders(chatId, today);
                }
                for (Long chatId : usersVaccinations.keySet()){
                    checkVaccinationsReminders(chatId, today);
                }
            } catch (Exception e) {
                e.getMessage();
            }
        }

        private void checkMedicineReminders(Long chatId, LocalDate today) {
            List<Medicine> medicines = usersMedicines.get(chatId);
            if (medicines != null) {
                for (Medicine med : medicines) {
                    LocalDate nextDate = LocalDate.parse(med.getNextDate());
                    if (!nextDate.isAfter(today)){
                        sendReminder(chatId, "Time for medicine: " +
                                med.getName() + med.getDosage());
                    }
                }
            }
        }

        private void checkVaccinationsReminders(Long chatId, LocalDate today){
            List<Vaccination> vaccinations = usersVaccinations.get(chatId);
            if (vaccinations != null){
                for (Vaccination vacc : vaccinations){
                    LocalDate nextDate = LocalDate.parse(vacc.getNextDate());
                    if (!nextDate.isAfter(today)){
                        sendReminder(chatId, "Vaccine day soon: "+
                                vacc.getName() );
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

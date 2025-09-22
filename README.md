# Pet Care Telegram Bot
Telegram bot for pet care. Assist to track vaccinations, medicine and reminds about important events.
## Opportunities
- Add information about pet
- Control medicine and dosage
- Track vaccination
- Reminders
- Deleting records
## Quick start
### Requirements
- Java 17+
- Maven 3.6+
- Telegram Bot Token from [@BorFather]
### Local installation
```bash
git clone https://github.com/victorkrisyatetskiy/petcare-bot.git
cd petcare-bot
```

# Settings configuration
cp src/main/resources/config.example.properties src/main/resources/config.properties
# Bot commands
/start - start bot's work  
/help - show all commands  
/addnewpet - add pet  
/addvaccination - add vaccination to the list
/addmedicine - add medicine to the list
/myvaccinations - list of the vaccinations  
/mymedicine - list of medicines  
/deletevaccination - delete vaccination from the list
/deletemedicine - delete medicine from the list  
## Technologies
- Java 17 - language
- Maven - project assembly
- SQLite - Data base
- Telegram Bot API - integration with Telegram
- Quartz Scheduler - system of reminders
## Project structure
```petcare-bot/
├── src/main/java/org/example/
│   ├── App.java              # Entry point
│   ├── PetCareBot.java       # Main
│   ├── models/               # Models
│   └── services/             # Services (DB, reminders)
├── src/main/resources/
│   └── config.properties     # Configuration
├── pom.xml                   # Configuration Maven
└── README.md                 # Documentation
```

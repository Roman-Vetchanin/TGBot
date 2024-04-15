package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            String text = update.message().text();
            long chatId = update.message().chat().id();
            String name = update.message().chat().firstName();
            String responseText;
            if (text.equals("/start")) {
                responseText = String.format("Привет %s! Я телеграм бот", name);
                SendMessage message = new SendMessage(chatId, responseText);
                SendResponse res = telegramBot.execute(message);
            } else if (text.equals("/stop")) {
                responseText = String.format("До свидания %s", name);
                SendMessage message = new SendMessage(chatId, responseText);
                SendResponse res = telegramBot.execute(message);
            } else {
                responseText = new String("Я не знаю что делать");
                SendMessage message = new SendMessage(chatId, responseText);
                SendResponse res = telegramBot.execute(message);
            }
            // Process your updates here
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

}

package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import pro.sky.telegrambot.service.TGBotService;
import pro.sky.telegrambot.service.TGbotMessageService;

import javax.annotation.PostConstruct;
import java.util.List;


@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);


    private final TGBotService tgBotService;
    private final TGbotMessageService messageService;

    @Autowired
    private TelegramBot telegramBot;

    public TelegramBotUpdatesListener(TGBotService tgBotService, TGbotMessageService messageService) {
        this.tgBotService = tgBotService;
        this.messageService = messageService;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }


    @Override
    public int process(List<Update> updates) {
        try {
            updates.forEach(update -> {
                logger.info("Processing update: {}", update);
                String text = update.message().text();
                long chatId = update.message().from().id();
                if ("/start".equals(text)) {
                    messageService.sendMessage(chatId, "Для записи задачи отправь ее в формате: 01.01.2020 20:00 Встреча");
                } else if (text != null) {
                    tgBotService.createTask(chatId, text);
                } else {
                    messageService.sendMessage(chatId, "Ой что то пошло не так");
                }
            });
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}

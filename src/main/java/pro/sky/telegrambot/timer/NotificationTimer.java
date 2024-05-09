package pro.sky.telegrambot.timer;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.service.TGBotService;
import pro.sky.telegrambot.service.TGbotMessageService;

import java.util.concurrent.TimeUnit;

@Component
public class NotificationTimer {
    private final TGBotService tgBotService;
    private final TGbotMessageService messageService;

    public NotificationTimer(TGBotService tgBotService, TGbotMessageService messageService) {
        this.tgBotService = tgBotService;
        this.messageService = messageService;
    }

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    public void taskTimer() {
        tgBotService.findTaskForNotifying().forEach(task -> {
            messageService.sendMessage(task.getChatId(), task.getMessage());
            tgBotService.deleteTask(task);
        });
    }
}

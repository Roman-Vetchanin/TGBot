package pro.sky.telegrambot.service;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.sky.telegrambot.model.Task;
import pro.sky.telegrambot.repositiryes.NotificationTask;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TGBotService {

    private static final Pattern PATTERN = Pattern.compile("(\\d{2}\\.\\d{2}\\.\\d{4} \\d{2}:\\d{2})(\\s)([А-яA-z\\s\\d]+)");


    private final NotificationTask notificationTask;
    private final TGbotMessageService messageService;

    public TGBotService(NotificationTask notificationTask, TGbotMessageService messageService) {
        this.notificationTask = notificationTask;
        this.messageService = messageService;
    }
    @Transactional
    public void createTask(Long chatId, String message) {
        Matcher matcher = PATTERN.matcher(message);
        if (!matcher.find()) {
            messageService.sendMessage(chatId,"Вы отправили текст не шаблону: 01.01.2020 20:00 Встреча");
        } else if (matcher.group(1) != null) {
            Task task = new Task();
            task.setChatId(chatId);
            task.setDate(LocalDateTime.parse(matcher.group(1), DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")).truncatedTo(ChronoUnit.MINUTES));
            task.setMessage(matcher.group(3));
            messageService.sendMessage(chatId, "Событие добавлено");
            notificationTask.save(task);
        }
    }
    @Transactional (readOnly = true)
    public List<Task> findTaskForNotifying() {
        return notificationTask.findByDate(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
    }

    @Transactional
    public void deleteTask(Task task) {
        notificationTask.delete(task);
    }
}

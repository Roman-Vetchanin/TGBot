package pro.sky.telegrambot.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.sky.telegrambot.model.Task;
import pro.sky.telegrambot.repositiryes.NotificationTaskRepository;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TGBotService {

    private static final Pattern PATTERN = Pattern.compile("(\\d{2}\\.\\d{2}\\.\\d{4} \\d{2}:\\d{2})(\\s)([А-яA-z\\s\\d]+)");
    private static final Logger LOG = LoggerFactory.getLogger(TGBotService.class);
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");



    private final NotificationTaskRepository notificationTaskRepository;
    private final TGbotMessageService messageService;

    public TGBotService(NotificationTaskRepository notificationTaskRepository, TGbotMessageService messageService) {
        this.notificationTaskRepository = notificationTaskRepository;
        this.messageService = messageService;
    }

    @Transactional
    public void createTask(Long chatId, String message) {
        Matcher matcher = PATTERN.matcher(message);
        if (matcher.group(1) == null) {
            messageService.sendMessage(chatId, "Вы отправили текст не шаблону: 01.01.2020 20:00 Встреча");
        } else if (matcher.group(1) != null) {
            try {
                Task task = new Task();
                task.setChatId(chatId);
//                task.setDate(LocalDateTime.parse(matcher.group(1), DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")).truncatedTo(ChronoUnit.MINUTES));
                task.setDate((LocalDateTime) dateTimeFormatter.parse(matcher.group(1)));
                task.setMessage(matcher.group(3));
                messageService.sendMessage(chatId, "Событие добавлено");
                notificationTaskRepository.save(task);
            } catch (DateTimeParseException e) {
                LOG.error("Возникла ошибка: {}", e.getParsedString());
                messageService.sendMessage(chatId, "Ввели не правильный формат даты: дд.мм.гггг чч:мм {сообщение}");
            }
        }
    }

    @Transactional(readOnly = true)
    public List<Task> findTaskForNotifying() {
        return notificationTaskRepository.findByDate(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
    }

    @Transactional
    public void deleteTask(Task task) {
        notificationTaskRepository.delete(task);
    }
}

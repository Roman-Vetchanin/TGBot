package pro.sky.telegrambot.repositiryes;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.telegrambot.model.Task;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationTask extends JpaRepository<Task,Long> {
    List<Task> findByDate(LocalDateTime localDateTime);
}

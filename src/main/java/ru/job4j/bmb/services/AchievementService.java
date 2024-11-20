package ru.job4j.bmb.services;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.event.UserEvent;
import ru.job4j.bmb.model.Achievement;
import ru.job4j.bmb.model.Award;
import ru.job4j.bmb.model.MoodLog;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.AchievementRepository;
import ru.job4j.bmb.repository.AwardRepository;
import ru.job4j.bmb.repository.MoodLogRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Класс, который следит за достижениями пользователя
 * и награждает его за выполнение определенных действий.
 */

@Service
public class AchievementService implements ApplicationListener<UserEvent> {
    private final SentContent sentContent;
    private final AchievementRepository achievementRepository;
    private final MoodLogRepository moodlogRepository;
    private final AwardRepository awardRepository;

    public AchievementService(SentContent sentContent,
                              AchievementRepository achievementRepository,
                              MoodLogRepository moodlogRepository,
                              AwardRepository awardRepository) {
        this.sentContent = sentContent;
        this.achievementRepository = achievementRepository;
        this.moodlogRepository = moodlogRepository;
        this.awardRepository = awardRepository;
    }

    @Transactional
    @Override
    public void onApplicationEvent(UserEvent event) {
        User user = event.getUser();

        List<MoodLog> moodLogs = moodlogRepository.findAll().stream()
                .filter(value -> value.getUser().equals(user))
                .filter(value -> value.getMood().isGood())
                .toList();

        Optional<Award> award = awardRepository.findAll().stream()
                .filter(value -> value.getDays() == moodLogs.size())
                .findFirst();

        award.ifPresent(value -> achievementRepository.save(
                new Achievement(Instant.now().getEpochSecond(), user, value)));

        List<Achievement> achievements = achievementRepository.findAll().stream()
                .filter(value -> value.getUser().equals(user))
                .toList();

        Content content = new Content(user.getChatId());

        if (!achievements.isEmpty()) {
            content.setText("Так держать! Ваши имеющиеся награды : \n");
        } else {
            content.setText("К настоящему моменту у Вас нет наград.\n");
        }

        sentContent.sent(content);

        for (Achievement achievement : achievements) {
            content.setText("-- Количество дней :" + achievement.getAward().getDays() + " --\n"
                    + "Заголовок: " + achievement.getAward().getTitle() + "\n"
                    + "Название: " + achievement.getAward().getDescription() + "\n");
            sentContent.sent(content);
        }
    }

    @PostConstruct
    public void init() {
        System.out.println("achievementServiceBean is going through @PostConstruct init.");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("achievementServiceBean will be destroyed via @PreDestroy.");
    }
}

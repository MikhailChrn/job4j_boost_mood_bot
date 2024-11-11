package ru.job4j.bmb.services;

import org.springframework.stereotype.Service;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.model.*;
import ru.job4j.bmb.repository.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * MoodService
 * Класс, отвечающий за обработку запросов пользователя в зависимости от его настроения.
 */

@Service
public class MoodService {
    private final MoodLogRepository moodLogRepository;
    private final MoodRepository moodRepository;
    private final RecommendationEngine recommendationEngine;
    private final UserRepository userRepository;
    private final AchievementRepository achievementRepository;
    private final AwardRepository awardRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern("dd-MM-yyyy HH:mm")
            .withZone(ZoneId.systemDefault());

    public MoodService(MoodLogRepository moodLogRepository,
                       MoodRepository moodRepository,
                       RecommendationEngine recommendationEngine,
                       UserRepository userRepository,
                       AchievementRepository achievementRepository,
                       AwardRepository awardRepository) {
        this.moodLogRepository = moodLogRepository;
        this.moodRepository = moodRepository;
        this.recommendationEngine = recommendationEngine;
        this.userRepository = userRepository;
        this.achievementRepository = achievementRepository;
        this.awardRepository = awardRepository;
    }

    /**
     * Позволяет пользователю выбрать текущее настроение и фиксирует этот выбор в логе событий
     *
     * @param user
     * @param moodId
     * @return
     */
    public Content chooseMood(User user, Long moodId) {
        Optional<Mood> mood = moodRepository.findById(moodId);
        moodLogRepository.save(
                new MoodLog(user, mood.get(), Instant.now().getEpochSecond()));
        return recommendationEngine.recommendFor(user.getChatId(), moodId);
    }

    /**
     * Возвращает лог настроений пользователя за прошедшую неделю
     *
     * @param chatId
     * @param clientId
     * @return
     */
    public Optional<Content> weekMoodLogCommand(long chatId, Long clientId) {
        Content content = new Content(chatId);
        content.setText(formatMoodLogs(getPeriodOfMoodLogs(chatId, clientId, 7 * 24 * 60 * 60),
                "Отчёт по состоянию настроения за последние семь дней :"));
        return Optional.of(content);
    }

    /**
     * Возвращает лог настроений пользователя за прошедший месяц
     *
     * @param chatId
     * @param clientId
     * @return
     */
    public Optional<Content> monthMoodLogCommand(long chatId, Long clientId) {
        Content content = new Content(chatId);
        content.setText(formatMoodLogs(getPeriodOfMoodLogs(chatId, clientId, 30 * 24 * 60 * 60),
                "Отчёт по состоянию настроения за последние тридцать дней :"));
        return Optional.of(content);
    }

    /**
     * Возвращает заслуженную награду за поддержание хорошего настроения
     *
     * @param chatId
     * @param clientId
     * @return
     */
    public Optional<Content> awards(long chatId, Long clientId) {
        Content content = new Content(chatId);

        User user = userRepository.findAll().stream()
                .filter(value -> Objects.equals(value.getClientId(), clientId)
                        && Objects.equals(value.getChatId(), chatId))
                .findFirst()
                .orElse(null);

        long countOfGoodDays = (moodLogRepository.findAll().stream()
                .filter(moodLog -> moodLog.getUser().equals(user))
                .filter(moodLog -> moodLog.getMood().isGood())
                .count());

        Award award = awardRepository.findAll().stream()
                .filter(awrd -> awrd.getDays() <= countOfGoodDays)
                .max(Comparator.comparing(Award::getDays))
                .orElse(null);

        content.setText(String.format("Ваше достижение :\n%s",
                award == null ? "award not found" : award.getTitle()));

        return Optional.of(content);
    }

    /**
     * Возвращает лог настроений пользователя на произвольную глубину времени от текущего момента
     *
     * @param chatId
     * @param clientId
     * @param seconds
     * @return
     */
    private List<MoodLog> getPeriodOfMoodLogs(long chatId, Long clientId, long seconds) {
        User user = userRepository.findAll().stream()
                .filter(value -> Objects.equals(value.getClientId(), clientId)
                        && Objects.equals(value.getChatId(), chatId))
                .findFirst()
                .orElse(null);

        return moodLogRepository.findAll().stream()
                .filter(moodLog -> moodLog.getUser().equals(user)
                        && (Instant.now().getEpochSecond() - moodLog.getCreatedAt() <= seconds))
                .toList();
    }

    /**
     * Форматирует лог настроений пользователя и возвращает отчёт в виде строки
     *
     * @param logs
     * @param title
     * @return
     */
    private String formatMoodLogs(List<MoodLog> logs, String title) {
        if (logs.isEmpty()) {
            return title + ":\nNo mood logs found.";
        }

        StringBuilder sb = new StringBuilder(title + ":\n");

        logs.forEach(log -> {
            String formattedDate = formatter.format(Instant.ofEpochSecond(log.getCreatedAt()));
            sb.append(formattedDate).append(": ").append(log.getMood().getText()).append("\n");
        });

        return sb.toString();
    }
}

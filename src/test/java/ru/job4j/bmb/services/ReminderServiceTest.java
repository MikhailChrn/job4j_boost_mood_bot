package ru.job4j.bmb.services;

import org.junit.jupiter.api.Test;
import ru.job4j.bmb.component.TgUI;
import ru.job4j.bmb.repofake.MoodLogFakeRepository;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.model.Mood;
import ru.job4j.bmb.model.MoodLog;
import ru.job4j.bmb.model.User;

import ru.job4j.bmb.repofake.MoodFakeRepository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ReminderServiceTest {
    @Test
    public void whenMoodGood() {
        ArrayList<Content> result = new ArrayList<>();
        SentContent sentContent = new SentContent() {
            @Override
            public void sent(Content content) {
                result.add(content);
            }
        };
        MoodFakeRepository moodRepository = new MoodFakeRepository();
        moodRepository.save(new Mood("Good", true));
        MoodLogFakeRepository moodLogRepository = new MoodLogFakeRepository();
        User user = new User();
        user.setChatId(100);
        MoodLog moodLog = new MoodLog();
        moodLog.setUser(user);

        long tenDaysBefore = LocalDate.now()
                .minusDays(10)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli() - 1;

        moodLog.setCreatedAt(tenDaysBefore);
        moodLogRepository.save(moodLog);

        TgUI tgUI = new TgUI(moodRepository);

        new ReminderService(sentContent, moodLogRepository, tgUI)
                .remindUsers();
        assertThat(result.iterator().next().getMarkup().getKeyboard()
                .iterator().next().iterator().next().getText()).isEqualTo("Good");
    }

    @Test
    public void whenMoodGoodVoteNotNeed() {
        ArrayList<Content> result = new ArrayList<>();
        SentContent sentContent = new SentContent() {
            @Override
            public void sent(Content content) {
                result.add(content);
            }
        };
        MoodFakeRepository moodRepository = new MoodFakeRepository();
        moodRepository.save(new Mood("Good", true));
        MoodLogFakeRepository moodLogRepository = new MoodLogFakeRepository();
        User user = new User();
        user.setChatId(100);
        MoodLog moodLog = new MoodLog();
        moodLog.setUser(user);

        long yesterday = LocalDate.now()
                .plusDays(1)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli() - 300;
        moodLog.setCreatedAt(yesterday);
        moodLogRepository.save(moodLog);

        TgUI tgUI = new TgUI(moodRepository);

        new ReminderService(sentContent, moodLogRepository, tgUI)
                .remindUsers();
        assertThat(result.size()).isEqualTo(0);
        assertThat(result).isEmpty();
    }
}
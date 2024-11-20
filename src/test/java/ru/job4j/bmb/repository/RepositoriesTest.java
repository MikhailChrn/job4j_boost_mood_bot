package ru.job4j.bmb.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import ru.job4j.bmb.model.Mood;
import ru.job4j.bmb.model.MoodLog;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repofake.MoodFakeRepository;
import ru.job4j.bmb.repofake.MoodLogFakeRepository;
import ru.job4j.bmb.repofake.UserFakeRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ContextConfiguration(classes = {
        UserFakeRepository.class,
        MoodFakeRepository.class,
        MoodLogFakeRepository.class
})
class RepositoriesTest {
    @Autowired
    @Qualifier("userFakeRepository")
    private UserRepository userRepository;

    @Autowired
    @Qualifier("moodFakeRepository")
    private MoodRepository moodRepository;

    @Autowired
    @Qualifier("moodLogFakeRepository")
    private MoodLogFakeRepository moodLogRepository;

    User user;
    Mood mood;
    MoodLog moodLog;

    {
        user = new User(2L, 4L, 6L);
        mood = new Mood("Хорошо", true);
        moodLog = new MoodLog(user, mood, 999L);
    }

    @Test
    public void addUser() {
        userRepository.save(user);
        assertThat(userRepository.findAll().stream().findFirst()
                .get()
                .getChatId())
                .isEqualTo(user.getChatId());
    }

    @Test
    public void addMood() {
        moodRepository.save(mood);
        assertThat(moodRepository.findAll().stream().findFirst()
                .get()
                .getText())
                .isEqualTo(mood.getText());
    }

    @Test
    public void addMoodLog() {
        moodLogRepository.save(moodLog);
        assertThat(moodLogRepository.findAll().stream().findFirst()
                .get()
                .getUser()
                .getClientId())
                .isEqualTo(user.getClientId());
    }
}
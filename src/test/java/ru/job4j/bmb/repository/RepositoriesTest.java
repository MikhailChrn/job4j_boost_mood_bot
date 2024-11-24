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

    @Test
    public void addUser() {
        User user = new User(2L, 4L, 6L);
        userRepository.save(user);
        assertThat(userRepository.findById(user.getId()).stream().findFirst()
                .get()
                .getChatId())
                .isEqualTo(user.getChatId());
    }

    @Test
    public void addMood() {
        Mood mood = new Mood("Хорошо", true);
        moodRepository.save(mood);
        assertThat(moodRepository.findById(mood.getId())
                .get()
                .getText())
                .isEqualTo(mood.getText());
    }

    @Test
    public void addMoodLog() {
        User user = new User(2L, 4L, 6L);
        Mood mood = new Mood("Хорошо", true);
        MoodLog moodLog = new MoodLog(user, mood, 999L);
        moodLogRepository.save(moodLog);
        assertThat(moodLogRepository.findById(moodLog.getId())
                .get()
                .getUser()
                .getClientId())
                .isEqualTo(user.getClientId());
    }
}
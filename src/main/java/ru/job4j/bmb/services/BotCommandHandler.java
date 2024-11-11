package ru.job4j.bmb.services;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.UserRepository;

import java.util.Optional;

/**
 * Этот класс будет обрабатывать меню бота
 */

@Service
public class BotCommandHandler {
    private final UserRepository userRepository;
    private final MoodService moodService;
    private final TgUI tgUI;

    public BotCommandHandler(UserRepository userRepository, MoodService moodService, TgUI tgUI) {
        this.userRepository = userRepository;
        this.moodService = moodService;
        this.tgUI = tgUI;
    }

    /**
     * Обработка текстовых команд
     * /start: Вызывает handleStartCommand, который инициализирует взаимодействие с пользователем.
     * /week_mood_log: Обращается к MoodService для получения лога настроений пользователя за неделю.
     * /month_mood_log: Получает лог настроений за месяц через MoodService.
     * /award: Обращается к MoodService для получения списка наград пользователя
     * Если команда не совпадает ни с одним из известных вариантов, возвращается Optional.empty().
     * @param message
     * @return
     */
    Optional<Content> commands(Message message) {
        Optional<Content> result = Optional.empty();

        if ("/start".equals(message.getText())) {
            result = handleStartCommand(message.getChatId(), message.getFrom().getId());
        } else if ("/week_mood_log".equals(message.getText())) {
            result = moodService.weekMoodLogCommand(message.getChatId(), message.getFrom().getId());
        } else if ("/month_mood_log".equals(message.getText())) {
            result = moodService.weekMoodLogCommand(message.getChatId(), message.getFrom().getId());
        } else if ("/award".equals(message.getText())) {
            result = moodService.awards(message.getChatId(), message.getFrom().getId());
        }

        return result;
    }

    /**
     * Обработка нажатия на кнопки меню
     * @param callback
     * @return
     */
    Optional<Content> handleCallback(CallbackQuery callback) {
        Long moodId = Long.valueOf(callback.getData());
        User user = userRepository
                .findByClientId(callback.getFrom().getId()).get();
        return Optional.of(
                moodService.chooseMood(user, moodId));
    }

    private Optional<Content> handleStartCommand(long chatId, Long clientId) {
        User user = new User();
        user.setClientId(clientId);
        user.setChatId(chatId);
        userRepository.save(user);
        Content content = new Content(user.getChatId());
        content.setText("Как настроение?");
        content.setMarkup(tgUI.buildButtons());
        return Optional.of(content);
    }
}
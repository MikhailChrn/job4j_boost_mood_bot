package ru.job4j.bmb.telegram;

import org.springframework.stereotype.Service;
import ru.job4j.bmb.content.Content;

/**
 * Основной класс, который использует Telegram API
 * для получения и отправки сообщений.
 */

@Service
public class TelegramBotService {
    private final BotCommandHandler handler;

    public TelegramBotService(BotCommandHandler handler) {
        this.handler = handler;
    }

    public void content(Content content) {
        handler.receive(content);
    }
}
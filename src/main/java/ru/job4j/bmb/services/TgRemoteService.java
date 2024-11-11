package ru.job4j.bmb.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.job4j.bmb.repository.UserRepository;

/**
 * TgRemoteService сервис для интеграции с Telegram Rest API
 */

@Service
public class TgRemoteService extends TelegramLongPollingBot {
    private final String botName;
    private final String botToken;
    private final UserRepository userRepository;
    private final TgUI tgUI;
    private final BotCommandHandler botCommandHandler;

    public TgRemoteService(@Value("${telegram.bot.name}") String botName,
                           @Value("${telegram.bot.token}") String botToken,
                           UserRepository userRepository, TgUI tgUI, BotCommandHandler botCommandHandler) {
        this.botName = botName;
        this.botToken = botToken;
        this.userRepository = userRepository;
        this.tgUI = tgUI;
        this.botCommandHandler = botCommandHandler;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(botCommandHandler
                    .commands(update.getMessage()).get()
                    .getText());
            send(sendMessage);
        }
    }

    public void send(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}

package ru.job4j.bmb.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.job4j.bmb.content.Content;

@Service
public class TelegramBotService extends TelegramLongPollingBot implements SentContent {
    private final BotCommandHandler handler;
    private final String botName;

    public TelegramBotService(@Value("${telegram.bot.name}") String botName,
                              @Value("${telegram.bot.token}") String botToken,
                              BotCommandHandler handler) {
        super(botToken);
        this.handler = handler;
        this.botName = botName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            handler.handleCallback(update.getCallbackQuery())
                    .ifPresent(this::sent);
        } else if (update.hasMessage() && update.getMessage().getText() != null) {
            handler.commands(update.getMessage())
                    .ifPresent(this::sent);
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void sent(Content content) {
        try {
            if (content.getAudio() != null) {
                SendAudio message = new SendAudio();
                message.setChatId(content.getChatId());
                message.setAudio(content.getAudio());
                message.setTitle(content.getText());
                execute(message);
            } else if (content.getPhoto() != null) {
                SendPhoto message = new SendPhoto();
                message.setChatId(content.getChatId());
                message.setPhoto(content.getPhoto());
                message.setReplyMarkup(content.getMarkup());
                execute(message);
            } else if (content.getMarkup() != null) {
                SendMessage message = new SendMessage();
                message.setChatId(content.getChatId());
                message.setReplyMarkup(content.getMarkup());
                message.setText(content.getText());
                execute(message);
            } else {
                SendMessage message = new SendMessage();
                message.setChatId(content.getChatId());
                message.setText(content.getText());
                execute(message);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}

package ru.job4j.bmb.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppConfig {

    private final String telegramBotName;

    public AppConfig(@Value("${telegram.bot.name}") String telegramBotName) {
        this.telegramBotName = telegramBotName;
    }
}
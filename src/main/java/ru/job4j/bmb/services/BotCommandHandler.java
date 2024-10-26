package ru.job4j.bmb.services;

import org.springframework.stereotype.Service;
import ru.job4j.bmb.content.Content;

/**
 * Этот класс будет обрабатывать меню бота
 */

@Service
public class BotCommandHandler {
    void receive(Content content) {
        System.out.println(content);
    }
}

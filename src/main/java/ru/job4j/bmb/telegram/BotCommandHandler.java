package ru.job4j.bmb.telegram;

import ru.job4j.bmb.content.Content;

/**
 * Класс для обработки команд, поступающих от пользователей
 * (например, выбор настроения, запрос рекомендаций).
 */

public class BotCommandHandler {
    void receive(Content content) {
        System.out.println(content);
    }
}

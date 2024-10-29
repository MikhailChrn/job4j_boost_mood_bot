package ru.job4j.bmb.services;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.BeanNameAware;

/**
 * Класс, который следит за достижениями пользователя
 * и награждает его за выполнение определенных действий.
 */

@Service
public class AchievementService implements BeanNameAware {

    @Override
    public void setBeanName(String name) {
        System.out.println("Bean name is " + name);
    }

    @PostConstruct
    public void init() {
        System.out.println("Bean is going through @PostConstruct init.");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("Bean will be destroyed via @PreDestroy.");
    }

}

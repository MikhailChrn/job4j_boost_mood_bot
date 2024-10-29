package ru.job4j.bmb.model;

import java.util.List;

public interface UserRepository {
    List<User> findAll();

    User findByClientId(Long clientId);
}

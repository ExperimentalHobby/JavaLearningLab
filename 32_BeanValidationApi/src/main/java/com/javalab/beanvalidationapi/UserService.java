package com.javalab.beanvalidationapi;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * ユーザーのインメモリ管理を行うサービス。Springコンテナに依存しないため、プレーンなJUnitテストで検証できる。
 */
@Service
public class UserService {

    // ConcurrentHashMapは反復順序を保証しないため、IDの昇順を保証するConcurrentSkipListMapを使う
    // (IDは単調増加のため、キー昇順=登録順になる)。
    private final Map<Long, User> users = new ConcurrentSkipListMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public User register(UserRegistrationRequest request) {
        long id = nextId.getAndIncrement();
        User user = new User(id, request.name(), request.email(), request.age());
        users.put(id, user);
        return user;
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public User findById(long id) {
        User user = users.get(id);
        if (user == null) {
            throw new UserNotFoundException(id);
        }
        return user;
    }

    public boolean existsByEmail(String email) {
        return users.values().stream().anyMatch(u -> u.email().equals(email));
    }
}

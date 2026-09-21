package com.javalab.beanvalidationapi;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserServiceTest {

    @Test
    void findAll_returnsUsersInRegistrationOrder() {
        // ConcurrentHashMapはMapインターフェースの契約上、反復順序を保証しない。IDが小さい連番の
        // Longの場合はハッシュ分散の都合で結果的に登録順に見えてしまうため、この黒箱テストだけでは
        // Redを再現できない(検証したのは実装をConcurrentSkipListMapに置き換えた後も
        // 契約として順序が保証されることのドキュメント)。
        UserService service = new UserService();
        service.register(new UserRegistrationRequest("佐藤一郎", "sato@example.com", 20));
        service.register(new UserRegistrationRequest("鈴木花子", "suzuki@example.com", 25));
        service.register(new UserRegistrationRequest("山田太郎", "yamada@example.com", 30));

        List<User> users = service.findAll();

        assertEquals(List.of("佐藤一郎", "鈴木花子", "山田太郎"), users.stream().map(User::name).toList());
    }

    @Test
    void register_assignsIncrementingIdAndIsFindable() {
        UserService service = new UserService();

        User user = service.register(new UserRegistrationRequest("山田太郎", "yamada@example.com", 30));

        assertEquals(1L, user.id());
        assertEquals("山田太郎", user.name());
        assertEquals(1, service.findAll().size());
    }

    @Test
    void findById_unknownId_throwsUserNotFoundException() {
        UserService service = new UserService();

        UserNotFoundException ex = assertThrows(UserNotFoundException.class, () -> service.findById(999L));

        assertEquals("user not found: id=999", ex.getMessage());
    }
}

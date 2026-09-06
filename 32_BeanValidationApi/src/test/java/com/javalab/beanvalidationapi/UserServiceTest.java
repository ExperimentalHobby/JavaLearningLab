package com.javalab.beanvalidationapi;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserServiceTest {

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

package com.javalab.optionalnullsafety;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmployeeRepositoryTest {

    @Test
    void findById_existingId_returnsOptionalWithEmployee() {
        EmployeeRepository repository = new EmployeeRepository();
        repository.add(new Employee("E001", "山田太郎", "yamada@example.com"));

        Optional<Employee> found = repository.findById("E001");

        assertTrue(found.isPresent());
        assertEquals("山田太郎", found.get().name());
    }

    @Test
    void findById_unknownId_returnsEmptyOptional() {
        EmployeeRepository repository = new EmployeeRepository();

        Optional<Employee> found = repository.findById("E999");

        assertTrue(found.isEmpty());
    }

    @Test
    void findByEmail_existingEmail_returnsOptionalWithEmployee() {
        EmployeeRepository repository = new EmployeeRepository();
        repository.add(new Employee("E001", "山田太郎", "yamada@example.com"));

        Optional<Employee> found = repository.findByEmail("yamada@example.com");

        assertTrue(found.isPresent());
        assertEquals("E001", found.get().id());
    }
}

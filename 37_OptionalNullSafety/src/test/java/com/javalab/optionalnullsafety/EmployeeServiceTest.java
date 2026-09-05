package com.javalab.optionalnullsafety;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmployeeServiceTest {

    @Test
    void describe_existingId_returnsNameContainingText() {
        EmployeeRepository repository = new EmployeeRepository();
        repository.add(new Employee("E001", "山田太郎", "yamada@example.com"));
        EmployeeService service = new EmployeeService(repository);

        assertEquals("山田太郎", service.describe("E001"));
    }

    @Test
    void describe_unknownId_returnsNotFoundMessage() {
        EmployeeService service = new EmployeeService(new EmployeeRepository());

        assertEquals("(該当社員なし)", service.describe("E999"));
    }

    @Test
    void emailOf_existingId_returnsEmail() {
        EmployeeRepository repository = new EmployeeRepository();
        repository.add(new Employee("E001", "山田太郎", "yamada@example.com"));
        EmployeeService service = new EmployeeService(repository);

        assertEquals("yamada@example.com", service.emailOf("E001"));
    }

    @Test
    void emailOf_unknownId_throwsEmployeeNotFoundException() {
        EmployeeService service = new EmployeeService(new EmployeeRepository());

        EmployeeNotFoundException ex = assertThrows(EmployeeNotFoundException.class, () -> service.emailOf("E999"));
        assertEquals("employee not found: id=E999", ex.getMessage());
    }

    @Test
    void findByIdOrEmail_matchesById() {
        EmployeeRepository repository = new EmployeeRepository();
        repository.add(new Employee("E001", "山田太郎", "yamada@example.com"));
        EmployeeService service = new EmployeeService(repository);

        Optional<Employee> found = service.findByIdOrEmail("E001");

        assertTrue(found.isPresent());
        assertEquals("山田太郎", found.get().name());
    }

    @Test
    void findByIdOrEmail_matchesByEmailWhenIdDoesNotMatch() {
        EmployeeRepository repository = new EmployeeRepository();
        repository.add(new Employee("E001", "山田太郎", "yamada@example.com"));
        EmployeeService service = new EmployeeService(repository);

        Optional<Employee> found = service.findByIdOrEmail("yamada@example.com");

        assertTrue(found.isPresent());
        assertEquals("E001", found.get().id());
    }

    @Test
    void findByIdOrEmail_noMatch_returnsEmptyOptional() {
        EmployeeService service = new EmployeeService(new EmployeeRepository());

        Optional<Employee> found = service.findByIdOrEmail("nobody");

        assertTrue(found.isEmpty());
    }
}

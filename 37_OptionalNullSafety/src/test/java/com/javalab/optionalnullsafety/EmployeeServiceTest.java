package com.javalab.optionalnullsafety;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmployeeServiceTest {

    @Test
    void emailDomainOf_existingId_returnsDomainPart() {
        // Optional#flatMapの活用例: findById(Optional<Employee>)の結果に対し、
        // さらにOptionalを返すextractDomainを連結する。
        EmployeeRepository repository = new EmployeeRepository();
        repository.add(new Employee("E001", "山田太郎", "yamada@example.com"));
        EmployeeService service = new EmployeeService(repository);

        Optional<String> domain = service.emailDomainOf("E001");

        assertEquals(Optional.of("example.com"), domain);
    }

    @Test
    void emailDomainOf_unknownId_returnsEmptyOptional() {
        EmployeeService service = new EmployeeService(new EmployeeRepository());

        assertTrue(service.emailDomainOf("E999").isEmpty());
    }

    @Test
    void findByIdWithEmailDomain_matchingDomain_returnsEmployee() {
        // Optional#filterの活用例。
        EmployeeRepository repository = new EmployeeRepository();
        repository.add(new Employee("E001", "山田太郎", "yamada@example.com"));
        EmployeeService service = new EmployeeService(repository);

        Optional<Employee> found = service.findByIdWithEmailDomain("E001", "example.com");

        assertTrue(found.isPresent());
    }

    @Test
    void findByIdWithEmailDomain_nonMatchingDomain_returnsEmptyOptional() {
        EmployeeRepository repository = new EmployeeRepository();
        repository.add(new Employee("E001", "山田太郎", "yamada@example.com"));
        EmployeeService service = new EmployeeService(repository);

        Optional<Employee> found = service.findByIdWithEmailDomain("E001", "other.com");

        assertTrue(found.isEmpty());
    }

    @Test
    void findExisting_mixedIds_returnsOnlyExistingEmployees() {
        // Optional.stream()の活用例: 複数IDのうち実在するものだけを集める。
        EmployeeRepository repository = new EmployeeRepository();
        repository.add(new Employee("E001", "山田太郎", "yamada@example.com"));
        repository.add(new Employee("E002", "鈴木花子", "suzuki@example.com"));
        EmployeeService service = new EmployeeService(repository);

        List<Employee> found = service.findExisting(List.of("E001", "E999", "E002"));

        assertEquals(List.of("山田太郎", "鈴木花子"), found.stream().map(Employee::name).toList());
    }

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
        assertEquals("該当する社員が見つかりません: id=E999", ex.getMessage());
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

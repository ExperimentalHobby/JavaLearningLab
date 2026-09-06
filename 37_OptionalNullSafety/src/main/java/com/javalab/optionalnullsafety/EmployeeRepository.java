package com.javalab.optionalnullsafety;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 社員のインメモリ管理を行うリポジトリ。
 * 「見つからないかもしれない」検索系メソッドは、nullを返さず{@code Optional}を戻り値の型として使う
 * (API境界でのnull安全設計の実践)。
 */
public class EmployeeRepository {

    private final Map<String, Employee> employeesById = new LinkedHashMap<>();

    public void add(Employee employee) {
        employeesById.put(employee.id(), employee);
    }

    public Optional<Employee> findById(String id) {
        return Optional.ofNullable(employeesById.get(id));
    }

    public Optional<Employee> findByEmail(String email) {
        return employeesById.values().stream()
                .filter(e -> e.email().equals(email))
                .findFirst();
    }

    public List<Employee> findAll() {
        return new ArrayList<>(employeesById.values());
    }
}

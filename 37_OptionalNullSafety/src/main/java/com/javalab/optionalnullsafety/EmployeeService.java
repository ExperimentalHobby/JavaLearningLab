package com.javalab.optionalnullsafety;

import java.util.Optional;

/**
 * 社員検索に関するロジックを提供する。{@link Optional}のAPI(map/or/orElse/orElseThrow)を
 * 使い分けることで、null安全なAPI設計を実践する。
 */
public class EmployeeService {

    private final EmployeeRepository repository;

    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }

    /** 存在すれば氏名、存在しなければ固定メッセージを返す(例外を投げない用途)。 */
    public String describe(String id) {
        return repository.findById(id)
                .map(Employee::name)
                .orElse("(該当社員なし)");
    }

    /** 存在すればメールアドレス、存在しなければ例外を投げる(呼び出し側に対処を強制する用途)。 */
    public String emailOf(String id) {
        return repository.findById(id)
                .map(Employee::email)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    /** IDとして検索し、見つからなければメールアドレスとしても検索する({@link Optional#or}の活用例)。 */
    public Optional<Employee> findByIdOrEmail(String idOrEmail) {
        return repository.findById(idOrEmail)
                .or(() -> repository.findByEmail(idOrEmail));
    }
}

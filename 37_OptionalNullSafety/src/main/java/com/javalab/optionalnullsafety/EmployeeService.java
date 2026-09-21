package com.javalab.optionalnullsafety;

import java.util.List;
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

    /**
     * 指定IDの社員のメールアドレスからドメイン部分を取り出す({@link Optional#flatMap}の活用例)。
     * {@code findById}(Optional)の結果に対し、さらにOptionalを返す{@code extractDomain}を連結する。
     * {@code map}だと結果が{@code Optional<Optional<String>>}になってしまうところを、
     * {@code flatMap}で1段階の{@code Optional<String>}に平坦化している。
     */
    public Optional<String> emailDomainOf(String id) {
        return repository.findById(id)
                .flatMap(e -> extractDomain(e.email()));
    }

    private static Optional<String> extractDomain(String email) {
        int at = email.indexOf('@');
        return at < 0 ? Optional.empty() : Optional.of(email.substring(at + 1));
    }

    /**
     * 指定IDの社員が、指定ドメインのメールアドレスを持つ場合のみ返す({@link Optional#filter}の活用例)。
     */
    public Optional<Employee> findByIdWithEmailDomain(String id, String domain) {
        return repository.findById(id)
                .filter(e -> e.email().endsWith("@" + domain));
    }

    /**
     * 複数のID候補のうち、実在する社員だけを集める({@link Optional#stream()}の活用例)。
     * {@code Optional<Employee>}のStreamを{@code flatMap(Optional::stream)}することで、
     * 空のOptionalは自然に除外され、値のあるものだけが残る。
     */
    public List<Employee> findExisting(List<String> ids) {
        return ids.stream()
                .map(repository::findById)
                .flatMap(Optional::stream)
                .toList();
    }
}

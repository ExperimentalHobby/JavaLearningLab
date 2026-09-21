package com.javalab.optionalnullsafety;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link Employee}のコンパクトコンストラクタによる検証を確認するテスト。
 * 「値が存在しないかもしれない」ことはOptionalで表現する方針だが、これはそれとは別の話で、
 * 生成されたEmployeeインスタンスのフィールドは常にnullであってはならないという不変条件を
 * {@link java.util.Objects#requireNonNull}で保証する。
 */
class EmployeeTest {

    @Test
    void constructor_nullId_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Employee(null, "山田太郎", "yamada@example.com"));
    }

    @Test
    void constructor_nullName_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Employee("E001", null, "yamada@example.com"));
    }

    @Test
    void constructor_nullEmail_throwsNullPointerException() {
        // 修正前はEmployeeRepository.findByEmailのe.email().equals(email)でNPEになっていた
        // (nullを許してしまっていたため)。コンストラクタ時点で拒否することで、
        // 「emailが存在するEmployeeインスタンス」であることをコンパイル後も保証する。
        assertThrows(NullPointerException.class, () -> new Employee("E001", "山田太郎", null));
    }
}

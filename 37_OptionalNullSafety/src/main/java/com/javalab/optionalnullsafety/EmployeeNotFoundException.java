package com.javalab.optionalnullsafety;

/** 指定IDの社員が存在しない場合にスローする非チェック例外。 */
public class EmployeeNotFoundException extends RuntimeException {

    public EmployeeNotFoundException(String id) {
        super("employee not found: id=" + id);
    }
}

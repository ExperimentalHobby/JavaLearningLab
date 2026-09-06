package com.javalab.reflectionannotation;

/** {@link Validator}によるリフレクションベースの検証対象となるデモ用フォーム。 */
public class UserForm {

    @NotBlank
    private final String name;

    @Min(0)
    @Max(150)
    private final int age;

    @NotBlank
    private final String email;

    public UserForm(String name, int age, String email) {
        this.name = name;
        this.age = age;
        this.email = email;
    }

    public String name() {
        return name;
    }

    public int age() {
        return age;
    }

    public String email() {
        return email;
    }
}

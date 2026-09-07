package com.company.automation.data;

public class TestDataBuilder {
    private String firstName = "Test";
    private String lastName = "User";
    private String username = "user";
    private String password = "Password123!";

    public TestDataBuilder firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public TestDataBuilder lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public TestDataBuilder username(String username) {
        this.username = username;
        return this;
    }

    public TestDataBuilder password(String password) {
        this.password = password;
        return this;
    }

    public User buildUser() {
        return new User(firstName, lastName, username, password);
    }
}

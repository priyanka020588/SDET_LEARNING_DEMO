package com.company.automation.learning.lesson02;

/**
 * Same role as {@code com.company.automation.data.User}.
 *
 * <p>Login needs several related values (username, password, display name).
 * Wrapping them in ONE type means methods take {@code Account account}
 * instead of four Strings that you can mix up.
 */
public class Account {
    private final String displayName;
    private final String username;
    private final String password;

    public Account(String displayName, String username, String password) {
        this.displayName = displayName;
        this.username = username;
        this.password = password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
    public Account withPassword(String newPassword) {
return new Account(displayName, username, newPassword); 
    }
}

package com.company.automation.learning.lesson02;

/**
 * Pretend login form. Mirrors {@code LoginPage.loginAs(User user)}:
 * one argument of a custom type, then the page UNWRAPS fields with getters.
 */
public class FakeLoginForm {
    public static void submit(Account account) {
        // Inside LoginPage this is: type(username, user.getUsername());
        System.out.println("typing username: " + account.getUsername());
        System.out.println("typing password: " + account.getPassword());
        System.out.println("click Log in");
        System.out.println("welcome " + account.getDisplayName());
    }

    /** The messy version — this is what wrapping avoids. */
    public static void submitLoose(String username, String password, String displayName) {
        System.out.println("easy to swap args by mistake: " + username + " / " + password);
    }
}

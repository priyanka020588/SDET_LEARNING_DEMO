package com.company.automation.learning.lesson02;

/**
 * This is the idea behind:
 *
 * <pre>
 *   User user = UserApiHelper.createActiveUser();
 *   new LoginPage(getDriver()).loginAs(user);
 * </pre>
 *
 * {@code User} / {@code Account} is a BUNDLE. The helper RETURNS that type.
 * Selenium does not know JSON; it only needs getters on the object.
 */
public class Lesson02Main {
    public static void main(String[] args) {
        // Left side: datatype Account (your class).
        // Right side: one object that holds three Strings together.
        Account account = new Account("Ada", "ada.demo", "Passw0rd!");

        FakeLoginForm.submit(account);

        System.out.println("---");
        System.out.println("account is still usable after submit: " + account.getDisplayName());
    }
}

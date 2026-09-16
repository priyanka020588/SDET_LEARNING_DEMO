package com.company.automation.learning.lesson05;

import com.company.automation.learning.lesson02.Account;
import com.company.automation.learning.lesson04.StudentApiHelper;
import com.company.automation.learning.lesson01.Student;

/**
 * Mini version of {@code LoginTest.validUserCanLogIn}.
 *
 * <pre>
 *   User user = UserApiHelper.createActiveUser();
 *   HomePage home = new LoginPage(getDriver()).loginAs(user);
 * </pre>
 *
 * Read it as: datatype on the left, create/wrap on the right, next page as the return type.
 */
public class Lesson05Main {
    public static void main(String[] args) {
        // BaseTest.setUp(): open browser + login URL (NOT inside LoginPage)
        FakeBrowser browser = new FakeBrowser();
        browser.open("/login");

        // UserApiHelper.createActiveUser() — API first, UI second
        Student student = StudentApiHelper.createActiveStudent();
        Account account = new Account(student.getName(), student.getName(), "Passw0rd!");

        // new LoginPage(getDriver()).loginAs(user)
        HomeScreen home = new LoginScreen(browser).signIn(account);

        System.out.println("home loaded? " + home.isLoaded());
        System.out.println(home.welcome(account.getDisplayName()));
    }
}

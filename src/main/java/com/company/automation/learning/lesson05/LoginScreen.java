package com.company.automation.learning.lesson05;

import com.company.automation.learning.lesson02.Account;

/**
 * Like {@code LoginPage}. Constructor does not open a URL.
 * {@code signIn} types, clicks, returns the NEXT screen — like {@code loginAs} → {@code HomePage}.
 */
public class LoginScreen extends BaseScreen {

    public LoginScreen(FakeBrowser browser) {
        super(browser);
    }

    public HomeScreen signIn(Account account) {
        System.out.println("type " + account.getUsername());
        System.out.println("type ********");
        System.out.println("click Log in");
        browser.goTo("/home");
        return new HomeScreen(browser);
    }
}

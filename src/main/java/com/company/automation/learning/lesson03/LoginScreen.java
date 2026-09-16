package com.company.automation.learning.lesson03;

/**
 * Child class — same role as {@code LoginPage extends BasePage}.
 *
 * <pre>
 *   public LoginPage(WebDriver driver) {
 *       super(driver);  // give the browser to BasePage first
 *   }
 * </pre>
 *
 * Java rule: the parent object is built BEFORE the child body runs.
 */
public class LoginScreen extends Screen {

    public LoginScreen(String title) {
        super(title);  // MUST be first statement. Passes title up to Screen.
        System.out.println("LoginScreen extra setup done");
    }

    public void signIn(String username) {
        // click() was written on Screen, but this object can call it
        // because LoginScreen IS-A Screen (inheritance).
        click("login with " + username);
    }
}

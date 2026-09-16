package com.company.automation.learning.lesson03;

/**
 * Run and read the print order: Screen constructor, then LoginScreen.
 * That is the same order as BasePage then LoginPage.
 */
public class Lesson03Main {
    public static void main(String[] args) {
        LoginScreen login = new LoginScreen("Demo Shop — Login");
        login.signIn("ada.demo");
    }
}

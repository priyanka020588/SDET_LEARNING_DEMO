package com.company.automation.learning.lesson03;

/**
 * Parent class — same role as {@code BasePage}.
 *
 * <p>{@code abstract} means you never write {@code new Screen(...)} in tests.
 * You construct a concrete child ({@code LoginScreen}), which still IS a Screen.
 *
 * <p>{@code protected} fields are visible to children, not to tests.
 */
public abstract class Screen {
    /** Like {@code protected final WebDriver driver} on BasePage. */
    protected final String title;

    /**
     * Parent constructor. Children MUST call this with {@code super(...)}.
     */
    protected Screen(String title) {
        this.title = title;
        System.out.println("Screen stored title = " + title);
    }

    protected void click(String control) {
        System.out.println("[" + title + "] click " + control);
    }
}

package com.company.automation.learning.lesson04;

import com.company.automation.learning.lesson01.Student;

/**
 * Same shape as {@code UserApiHelper.createActiveUser()}:
 * factory builds data → helper stores it → returns {@code Student} / {@code User}.
 */
public final class StudentApiHelper {
    private StudentApiHelper() {
    }

    public static Student createActiveStudent() {
        Student payload = StudentFactory.unique();
        return StudentDirectory.register(payload);
    }
}

package com.company.automation.learning.lesson04;

import com.company.automation.learning.lesson01.Student;
import java.util.UUID;

/**
 * Same role as {@code UserFactory.uniqueActiveUser()}.
 * Builds a Student in memory. Does NOT save it anywhere yet.
 */
public final class StudentFactory {
    private StudentFactory() {
    }

    public static Student unique() {
        String token = UUID.randomUUID().toString().substring(0, 8);
        // Unique name so two tests do not share one account — same as ada.<token>
        return new Student("Ada-" + token, 36);
    }
}

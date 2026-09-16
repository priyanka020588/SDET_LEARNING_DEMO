package com.company.automation.learning.lesson04;

import com.company.automation.learning.lesson01.Student;

/**
 * Mirrors LoginTest's first line:
 * {@code User user = UserApiHelper.createActiveUser();}
 *
 * <p>The variable type is Student. The object was created in the factory,
 * then stored by the directory (fake API), then handed back.
 */
public class Lesson04Main {
    public static void main(String[] args) {
        Student student = StudentApiHelper.createActiveStudent();

        System.out.println("variable 'student' has type Student");
        System.out.println("name = " + student.getName());
        System.out.println("exists in directory? " + StudentDirectory.exists(student.getName()));
    }
}

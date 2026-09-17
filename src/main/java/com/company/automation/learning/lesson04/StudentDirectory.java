package com.company.automation.learning.lesson04;

import com.company.automation.learning.lesson01.Student;
import java.util.HashMap;
import java.util.Map;

/**
 * Stand-in for {@code UserApiHelper} + {@code DemoShopServer} {@code /api/users}.
 *
 * <p>In the real framework RestAssured sends HTTP POST and the server stores the user.
 * Here we use a Map so you can see "register then look up" without a network.
 */
public final class StudentDirectory {
    private static final Map<String, Student> STORE = new HashMap<>();

    private StudentDirectory() {
    }

    /**
     * Like {@code UserApiHelper.createActiveUser()}: take a built object, save it, return it.
     */
    // In the real project this is RestAssured POST /api/users
    public static Student register(Student student) {
        STORE.put(student.getName(), student);
        System.out.println("directory saved student: " + student.getName());
        return student;
    }

    public static boolean exists(String name) {
        return STORE.containsKey(name);
    }
}

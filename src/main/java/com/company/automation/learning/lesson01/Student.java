package com.company.automation.learning.lesson01;

/**
 * A CLASS is a blueprint. It does not log anyone in by itself.
 * Same idea as {@code com.company.automation.data.User}: fields + constructor + getters.
 *
 * <p>An OBJECT is one real student in memory, created with {@code new Student(...)}.
 */
public class Student {
    // Fields = data this object holds. private = other classes use getters, not raw fields.
    private String name;
    private int age;

    /**
     * Constructor: runs when you write {@code new Student("Ada", 36)}.
     * {@code this.name} means "the field on THIS object", not some other student.
     */
    public Student(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void introduce() {
        System.out.println("Hello, my name is" ); 
    }
}

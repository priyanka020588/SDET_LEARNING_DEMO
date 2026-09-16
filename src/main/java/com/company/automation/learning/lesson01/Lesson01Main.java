package com.company.automation.learning.lesson01;

/**
 * Run this file's {@code main} in the IDE.
 *
 * <p>Java types you already know: {@code int}, {@code String}.
 * {@code Student} is a type YOU invented. A variable's type is written on the left:
 *
 * <pre>
 *   Student ada = new Student("Ada", 36);
 *   // ^^^^ type     ^^^^ variable    ^^^^ create object
 * </pre>
 *
 * Framework parallel: {@code User user = ...} — type User, variable user, object on the right.
 */
public class Lesson01Main {
    public static void main(String[] args) {
        // "new" allocates an object and calls the constructor.
        Student ada = new Student("Ada", 36);
        Student sam = new Student("Sam", 30);

        System.out.println("type of ada is Student");
        System.out.println("name = " + ada.getName() + ", age = " + ada.getAge());
        System.out.println("name=" +sam.getName()+",age="+sam.getAge()); 
        // ada is a REFERENCE: it points at the object. It is not the object itself.
        Student alsoAda = ada;
        alsoAda.setAge(37);
        System.out.println("ada.age after alias setAge = " + ada.getAge() + " (same object!)");
    }
}

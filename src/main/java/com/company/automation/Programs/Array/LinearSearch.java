package com.company.automation.Programs.Array;

public class LinearSearch {
    public static void main(String[] args) {
        int[] arr = {4, 2, 7, 1, 9, 3};
        int target = 9;
        int result = -1;

        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == target) {
                result = i;
                break;
            }
        }

        if (result != -1) {
            System.out.println("Target found at index: " + result);
        } else {
            System.out.println("Target not found "+result);
        }
    }
}

package com.company.automation.Programs.Array;

public class BinarySearch {
    public static void main(String[] args) {
        int[] array = {1, 3, 5, 7, 9, 11, 13};
        int target = 11;
        int low = 0;
        int high = array.length - 1;
        int result = -1;

        while (low <= high) {
            int mid = (low + high) / 2;
            if (array[mid] == target) 
            {
                result = mid;
                break;
            } 
            else if (array[mid] < target) 
            {
                low = mid + 1;
            } 
            else 
            {
                high = mid - 1;
            }
        }

        if (result != -1) {
            System.out.println("Target found at index: " + result);
        } else {
            System.out.println("Target " + target + " not found in the array");
        }
    }

}
package app;

import java.util.Arrays;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        int[] array = new int[10];
        Random random = new Random();

        for (int i = 0; i < array.length; i++) {
            array[i] = random.nextInt(100) + 1;
        }

        System.out.println("Original array: " + Arrays.toString(array));

        ArrayUtils.mergeSort(array);
        System.out.println("Sorted array: " + Arrays.toString(array));

        int target = array[array.length / 2];
        int index = ArrayUtils.binarySearch(array, target);

        if (index >= 0) {
            System.out.println("Value " + target + " found at index " + index);
        } else {
            System.out.println("Value " + target + " not found");
        }
    }
}

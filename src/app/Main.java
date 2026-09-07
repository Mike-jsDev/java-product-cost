package app;

import java.lang.reflect.Method;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        int[] numbers = {5, 12, 3, 27, 9};

        System.out.println("Array: " + Arrays.toString(numbers));
        System.out.println("Max: " + ArrayUtils.findMax(numbers));
        System.out.println("Min: " + ArrayUtils.findMin(numbers));
        System.out.println("Sum: " + ArrayUtils.calculateSum(numbers));
        System.out.println();

        for (Method method : ArrayUtils.class.getDeclaredMethods()) {
            if (method.isAnnotationPresent(MethodInfo.class)
                    && method.isAnnotationPresent(Author.class)) {

                MethodInfo methodInfo = method.getAnnotation(MethodInfo.class);
                Author author = method.getAnnotation(Author.class);

                System.out.println("Method: " + methodInfo.name());
                System.out.println("Return type: " + methodInfo.returnType());
                System.out.println("Description: " + methodInfo.description());
                System.out.println("Author: " + author.firstName() + " " + author.lastName());
                System.out.println();
            }
        }
    }
}

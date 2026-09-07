package app;

public class ArrayUtils {

    @MethodInfo(
            name = "findMax",
            returnType = "int",
            description = "Finds the maximum value in an array"
    )
    @Author(firstName = "Mike", lastName = "Student")
    public static int findMax(int[] array) {
        int max = array[0];
        for (int value : array) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    @MethodInfo(
            name = "findMin",
            returnType = "int",
            description = "Finds the minimum value in an array"
    )
    @Author(firstName = "Mike", lastName = "Student")
    public static int findMin(int[] array) {
        int min = array[0];
        for (int value : array) {
            if (value < min) {
                min = value;
            }
        }
        return min;
    }

    @MethodInfo(
            name = "calculateSum",
            returnType = "int",
            description = "Calculates the sum of all elements in an array"
    )
    @Author(firstName = "Mike", lastName = "Student")
    public static int calculateSum(int[] array) {
        int sum = 0;
        for (int value : array) {
            sum += value;
        }
        return sum;
    }
}

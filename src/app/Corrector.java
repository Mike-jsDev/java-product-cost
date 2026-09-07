package app;

public class Corrector {

    public String handleData(String[] words) {
        if (words == null || words.length == 0) {
            throw new IllegalArgumentException("Input array must not be null or empty");
        }

        StringBuilder stringBuilder = new StringBuilder();
        int count = 0;

        for (String str : words) {
            count++;
            str = str.replace("b", "o");

            stringBuilder.append(count)
                    .append(") ")
                    .append(str)
                    .append("\n");
        }

        stringBuilder.deleteCharAt(stringBuilder.length() - 1);

        return stringBuilder.toString();
    }
}

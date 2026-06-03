package app;

public class Corrector {

    public String handleData(String[] words) {
        StringBuilder stringBuilder = new StringBuilder();
        int count = 0;

        for (String str : words) {
            count++;

            if (str.contains("b")) {
                str = str.replace("b", "o");
            }

            stringBuilder.append(count)
                    .append(") ")
                    .append(str)
                    .append("\n");
        }

        return stringBuilder.toString();
    }
}
package utils;

import java.util.Comparator;

public class FileNameComparator implements Comparator<String> {


    public static int getFileNumber(String s) {

        int index1 = s.lastIndexOf("_");
        int index2 = s.lastIndexOf(".txt");

        return new Integer(s.substring(index1 + 1, index2));

    }

    @Override
    public int compare(String s, String s2) {


        int num1, num2;

        if (!s.endsWith(".txt") || !s2.endsWith(".txt") || !s.contains("_") || !s2.contains("_"))
            return s.compareTo(s2);


        num1 = getFileNumber(s);

        num2 = getFileNumber(s2);

        return num1 - num2;
    }
}

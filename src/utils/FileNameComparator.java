package utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class FileNameComparator implements Comparator<String> {



    @Override
    public int compare(String s, String s2) {

        int index11 = s.lastIndexOf("_");
        int index12 = s.lastIndexOf(".txt");
        int index21 = s2.lastIndexOf("_");
        int index22 = s2.lastIndexOf(".txt");
        int num1, num2;

        if(index11 == -1 || index12 == -1 ||
                index21 == -1 || index22 == -1){
            return s.compareTo(s2);
        }

        if(!s.substring(0,index11).equals(s2.substring(0,index21))){
            return  s.compareTo(s2);
        }


        num1 = new Integer(s.substring(index11+1,index12));

        num2 = new Integer(s2.substring(index21+1,index22));

        return num1 - num2;
    }
}

package cn.edu.neu.Demo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Demo02 {
    public static void main(String[] args) {
        String regex = "Indexed full text search on (.*) using (.*) [(](.*)[)]  .*";
        String description = "Indexed full text search on t1 using a (a='collections')  (cost=0.35 rows=1)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(description);
        if (matcher.find()) {
            System.out.println(matcher.group(0));
            System.out.println(matcher.group(1));
            System.out.println(matcher.group(2));
            System.out.println(matcher.group(3));
        }
    }
}
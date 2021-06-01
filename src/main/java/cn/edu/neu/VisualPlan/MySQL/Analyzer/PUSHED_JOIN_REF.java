package cn.edu.neu.VisualPlan.MySQL.Analyzer;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PUSHED_JOIN_REF implements AccessPath {
    private static AccessPath _instance = new PUSHED_JOIN_REF();

    private PUSHED_JOIN_REF() {
    }

    public static AccessPath getInstance() {
        return _instance;
    }

    private Pattern _pattern = Pattern.compile("Index lookup on (.*) using (.*) [(](.*)[)]");

    @Override
    public boolean tryInsertFields(Map<String, String> fieldMap, String description) {
        Matcher matcher = _pattern.matcher(description);
        if (!matcher.find()) {
            return false;
        }
        fieldMap.put("type", "PUSHED_JOIN_REF");
        fieldMap.put("1", matcher.group(1));
        fieldMap.put("2", matcher.group(2));
        fieldMap.put("3", matcher.group(3));
        return true;
    }
}

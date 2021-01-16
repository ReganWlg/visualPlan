package cn.edu.neu.VisualPlan.MySQL.Analyzer;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class INDEX_SCAN implements AccessPath {
    private static AccessPath _instance = new INDEX_SCAN();

    private INDEX_SCAN() {
    }

    public static AccessPath getInstance() {
        return _instance;
    }

    private Pattern _pattern = Pattern.compile("Index scan on (.*) using (.*)");
    @Override
    public boolean tryInsertFields(Map<String,String> fieldMap, String description) {
        Matcher matcher = _pattern.matcher(description);
        if (!matcher.find()) {
            return false;
        }
        fieldMap.put("type", "INDEX_SCAN");
        fieldMap.put("1", matcher.group(1));
        fieldMap.put("2", matcher.group(2));
        return true;
    }
}
package cn.edu.neu.VisualPlan.MySQL.Analyzer;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UNQUALIFIED_COUNT implements AccessPath {
    private static AccessPath _instance = new UNQUALIFIED_COUNT();

    private UNQUALIFIED_COUNT() {
    }

    public static AccessPath getInstance() {
        return _instance;
    }

    private Pattern _pattern = Pattern.compile("Count rows in (.*)");
    @Override
    public boolean tryInsertFields(Map<String,String> fieldMap, String description) {
        Matcher matcher = _pattern.matcher(description);
        if (!matcher.find()) {
            return false;
        }
        fieldMap.put("type", "UNQUALIFIED_COUNT");
        fieldMap.put("1", matcher.group(1));
        return true;
    }
}

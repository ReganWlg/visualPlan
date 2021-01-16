package cn.edu.neu.VisualPlan.MySQL.Analyzer;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FOLLOW_TAIL implements AccessPath {
    private static AccessPath _instance = new FOLLOW_TAIL();

    private FOLLOW_TAIL() {
    }

    public static AccessPath getInstance() {
        return _instance;
    }

    private Pattern _pattern = Pattern.compile("Scan new records on (.*)");
    @Override
    public boolean tryInsertFields(Map<String,String> fieldMap, String description) {
        Matcher matcher = _pattern.matcher(description);
        if (!matcher.find()) {
            return false;
        }
        fieldMap.put("type", "FOLLOW_TAIL");
        fieldMap.put("1", matcher.group(1));
        return true;
    }
}
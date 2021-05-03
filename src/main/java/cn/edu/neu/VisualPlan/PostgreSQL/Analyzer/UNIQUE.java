package cn.edu.neu.VisualPlan.PostgreSQL.Analyzer;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UNIQUE implements AccessPath{
    private static AccessPath _instance = new UNIQUE();

    private UNIQUE() {

    }

    public static AccessPath getInstance() {
        return _instance;
    }

    private Pattern _pattern = Pattern.compile("Unique (.*)");
    @Override
    public boolean tryInsertFields(Map<String, String> fieldMap, String description) {
        Matcher matcher = _pattern.matcher(description);
        if (!matcher.find()) {
            return false;
        } else {
            fieldMap.put("type", "UNIQUE");
            return true;
        }
    }
}

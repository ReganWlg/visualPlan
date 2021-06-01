package cn.edu.neu.VisualPlan.MySQL.Analyzer;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ALTERNATIVE implements AccessPath {
    private static AccessPath _instance = new ALTERNATIVE();

    private ALTERNATIVE() {
    }

    public static AccessPath getInstance() {
        return _instance;
    }

    private Pattern _pattern = Pattern.compile("Alternative plans for IN subquery: Index lookup unless");


    @Override
    public boolean tryInsertFields(Map<String, String> fieldMap, String description) {
        Matcher matcher = _pattern.matcher(description);
        if (!matcher.find()) {
            return false;
        }
        fieldMap.put("type", "ALTERNATIVE");
        return true;
    }
}

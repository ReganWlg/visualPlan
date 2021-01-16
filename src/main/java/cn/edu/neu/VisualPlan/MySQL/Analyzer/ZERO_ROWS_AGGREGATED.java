package cn.edu.neu.VisualPlan.MySQL.Analyzer;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ZERO_ROWS_AGGREGATED implements AccessPath {
    private static AccessPath _instance = new ZERO_ROWS_AGGREGATED();

    private ZERO_ROWS_AGGREGATED() {
    }

    public static AccessPath getInstance() {
        return _instance;
    }

    private Pattern _pattern = Pattern.compile("Zero input rows [(](.*)[)], aggregated into one output row");
    @Override
    public boolean tryInsertFields(Map<String,String> fieldMap, String description) {
        Matcher matcher = _pattern.matcher(description);
        if (!matcher.find()) {
            return false;
        }
        fieldMap.put("type", "ZERO_ROWS_AGGREGATED");
        fieldMap.put("1", matcher.group(1));
        return true;
    }
}

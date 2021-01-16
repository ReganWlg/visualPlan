package cn.edu.neu.VisualPlan.MySQL.Analyzer;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TEMPTABLE_AGGREGATE implements AccessPath {
    private static AccessPath _instance = new TEMPTABLE_AGGREGATE();

    private TEMPTABLE_AGGREGATE() {
    }

    public static AccessPath getInstance() {
        return _instance;
    }

    private Pattern _pattern = Pattern.compile("Aggregate using temporary table");
    @Override
    public boolean tryInsertFields(Map<String,String> fieldMap, String description) {
        Matcher matcher = _pattern.matcher(description);
        if (!matcher.find()) {
            return false;
        }
        fieldMap.put("type", "TEMPTABLE_AGGREGATE");
        return true;
    }
}

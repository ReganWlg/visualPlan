package cn.edu.neu.VisualPlan.MySQL.Analyzer;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MATERIALIZE_INFORMATION_SCHEMA_TABLE implements AccessPath {
    private static AccessPath _instance = new MATERIALIZE_INFORMATION_SCHEMA_TABLE();

    private MATERIALIZE_INFORMATION_SCHEMA_TABLE() {
    }

    public static AccessPath getInstance() {
        return _instance;
    }

    private Pattern _pattern = Pattern.compile("Fill information schema table (.*)");
    @Override
    public boolean tryInsertFields(Map<String,String> fieldMap, String description) {
        Matcher matcher = _pattern.matcher(description);
        if (!matcher.find()) {
            return false;
        }
        fieldMap.put("type", "MATERIALIZED_\nINFORMATION_\nSCHEMA_TABLE");
        fieldMap.put("1", matcher.group(1));
        return true;
    }
}

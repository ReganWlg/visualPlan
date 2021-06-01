package cn.edu.neu.VisualPlan.MySQL.Analyzer;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MATERIALIZED_TABLE_FUNCTION implements AccessPath {
    private static AccessPath _instance = new MATERIALIZED_TABLE_FUNCTION();

    private MATERIALIZED_TABLE_FUNCTION() {
    }

    public static AccessPath getInstance() {
        return _instance;
    }

    private Pattern _pattern = Pattern.compile("Materialize table function");
    @Override
    public boolean tryInsertFields(Map<String,String> fieldMap, String description) {
        Matcher matcher = _pattern.matcher(description);
        if (!matcher.find()) {
            return false;
        }
        fieldMap.put("type", "MATERIALIZED_\nTABLE_FUNCTION");
        return true;
    }
}

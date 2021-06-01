package cn.edu.neu.VisualPlan.MySQL.Analyzer;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TABLE_VALUE_CONSTRUCTOR implements AccessPath {
    private static AccessPath _instance = new TABLE_VALUE_CONSTRUCTOR();

    private TABLE_VALUE_CONSTRUCTOR() {
    }

    public static AccessPath getInstance() {
        return _instance;
    }

    private Pattern _pattern = Pattern.compile("Rows fetched before execution");
    @Override
    public boolean tryInsertFields(Map<String,String> fieldMap, String description) {
        Matcher matcher = _pattern.matcher(description);
        if (!matcher.find()) {
            return false;
        }
        fieldMap.put("type", "TABLE_VALUE\n_CONSTRUCTOR\nFAKE_SINGLE_ROW");
        return true;
    }
}

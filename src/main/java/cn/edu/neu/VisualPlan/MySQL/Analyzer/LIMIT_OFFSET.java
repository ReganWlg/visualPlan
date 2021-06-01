package cn.edu.neu.VisualPlan.MySQL.Analyzer;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LIMIT_OFFSET implements AccessPath {
    private static AccessPath _instance = new LIMIT_OFFSET();

    private LIMIT_OFFSET() {
    }

    public static AccessPath getInstance() {
        return _instance;
    }

    private Pattern _pattern = Pattern.compile("(Limit)?(/)?(Offset)?");


    @Override
    public boolean tryInsertFields(Map<String, String> fieldMap, String description) {
        Matcher matcher = _pattern.matcher(description);
        if (!matcher.find()) {
            return false;
        }
        fieldMap.put("type", "LIMIT_OFFSET");
        return true;
    }
}

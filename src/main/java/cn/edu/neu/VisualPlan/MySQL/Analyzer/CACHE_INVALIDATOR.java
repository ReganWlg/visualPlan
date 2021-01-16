package cn.edu.neu.VisualPlan.MySQL.Analyzer;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CACHE_INVALIDATOR implements AccessPath {
    private static AccessPath _instance = new CACHE_INVALIDATOR();

    private CACHE_INVALIDATOR() {
    }

    public static AccessPath getInstance() {
        return _instance;
    }

    private Pattern _pattern = Pattern.compile("Invalidate materialized tables [(]row from (.*)[)]");
    @Override
    public boolean tryInsertFields(Map<String,String> fieldMap, String description) {
        Matcher matcher = _pattern.matcher(description);
        if (!matcher.find()) {
            return false;
        }
        fieldMap.put("type", "CACHE_INVALIDATOR");
        fieldMap.put("1", matcher.group(1));
        return true;
    }
}

package cn.edu.neu.VisualPlan.MySQL.Analyzer;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class REMOVE_DUPLICATES implements AccessPath {
    private static AccessPath _instance = new REMOVE_DUPLICATES();

    private REMOVE_DUPLICATES() {
    }

    public static AccessPath getInstance() {
        return _instance;
    }

    private Pattern _pattern = Pattern.compile("Remove duplicates from input sorted on (.*)");
    @Override
    public boolean tryInsertFields(Map<String,String> fieldMap, String description) {
        Matcher matcher = _pattern.matcher(description);
        if (!matcher.find()) {
            return false;
        }
        fieldMap.put("type", "REMOVE_DUPLICATES");
        fieldMap.put("1", matcher.group(1));
        return true;
    }
}

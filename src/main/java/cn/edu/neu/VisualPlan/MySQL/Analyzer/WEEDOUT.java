package cn.edu.neu.VisualPlan.MySQL.Analyzer;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WEEDOUT implements AccessPath {
    private static AccessPath _instance = new WEEDOUT();

    private WEEDOUT() {
    }

    public static AccessPath getInstance() {
        return _instance;
    }

    private Pattern _pattern = Pattern.compile("Remove duplicate (.*) rows using temporary table [(]weedout[)]");
    @Override
    public boolean tryInsertFields(Map<String,String> fieldMap, String description) {
        Matcher matcher = _pattern.matcher(description);
        if (!matcher.find()) {
            return false;
        }
        fieldMap.put("type", "WEEDOUT");
        fieldMap.put("1", matcher.group(1));
        return true;
    }
}

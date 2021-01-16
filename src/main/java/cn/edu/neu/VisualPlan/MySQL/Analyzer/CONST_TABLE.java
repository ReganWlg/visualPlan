package cn.edu.neu.VisualPlan.MySQL.Analyzer;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CONST_TABLE implements AccessPath {
    private static AccessPath _instance = new CONST_TABLE();

    private CONST_TABLE() {
    }

    public static AccessPath getInstance() {
        return _instance;
    }

    private Pattern _pattern = Pattern.compile("Constant row from (.*)");
    @Override
    public boolean tryInsertFields(Map<String,String> fieldMap, String description) {
        Matcher matcher = _pattern.matcher(description);
        if (!matcher.find()) {
            return false;
        }
        fieldMap.put("type", "CONST_TABLE");
        fieldMap.put("1", matcher.group(1));
        return true;
    }
}

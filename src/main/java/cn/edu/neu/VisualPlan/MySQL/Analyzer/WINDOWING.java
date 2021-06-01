package cn.edu.neu.VisualPlan.MySQL.Analyzer;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WINDOWING implements AccessPath {
    private static AccessPath _instance = new WINDOWING();

    private WINDOWING() {
    }

    public static AccessPath getInstance() {
        return _instance;
    }

    private Pattern _pattern = Pattern.compile("Window (multi-pass )?aggregate( with buffering)?:");


    @Override
    public boolean tryInsertFields(Map<String, String> fieldMap, String description) {
        Matcher matcher = _pattern.matcher(description);
        if (!matcher.find()) {
            return false;
        }
        fieldMap.put("type", "WINDOWING");
        return true;
    }
}

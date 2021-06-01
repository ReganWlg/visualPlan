package cn.edu.neu.VisualPlan.MySQL.Analyzer;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AGGREGATE implements AccessPath {
    private static AccessPath _instance = new AGGREGATE();

    private AGGREGATE() {
    }

    public static AccessPath getInstance() {
        return _instance;
    }

    private Pattern _pattern1 = Pattern.compile("Group ([(]no )?aggregate(s[)])?( with rollup:)?(:)?");
    private Pattern _pattern2 = Pattern.compile("Aggregate:");


    @Override
    public boolean tryInsertFields(Map<String, String> fieldMap, String description) {
        Matcher matcher1 = _pattern1.matcher(description);
        Matcher matcher2 = _pattern2.matcher(description);
        if (!matcher1.find() && !matcher2.find()) {
            return false;
        }
        fieldMap.put("type", "AGGREGATE");
        return true;
    }
}

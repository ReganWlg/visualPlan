package cn.edu.neu.VisualPlan.MySQL.Analyzer;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MATERIALIZE implements AccessPath {
    private static AccessPath _instance = new MATERIALIZE();

    private MATERIALIZE() {
    }

    public static AccessPath getInstance() {
        return _instance;
    }

    private Pattern _pattern1 = Pattern.compile("Materialize( ((recursive)|(union) )? CTE)?");
    private Pattern _pattern2 = Pattern.compile("Union materialize");
    private Pattern _pattern3 = Pattern.compile("Temporary table");


    @Override
    public boolean tryInsertFields(Map<String, String> fieldMap, String description) {
        Matcher matcher1 = _pattern1.matcher(description);
        Matcher matcher2 = _pattern2.matcher(description);
        Matcher matcher3 = _pattern3.matcher(description);
        if (!matcher1.find() && !matcher2.find() && !matcher3.find()) {
            return false;
        }
        fieldMap.put("type", "MATERIALIZE");
        return true;
    }
}

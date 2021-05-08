package cn.edu.neu.VisualPlan.Calcite.Analyzer;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JdbcCalc implements AccessPath{
    private static AccessPath _instance = new JdbcCalc();

    private JdbcCalc() {

    }

    public static AccessPath getInstance() {
        return _instance;
    }

    private Pattern _pattern = Pattern.compile("JdbcCalc(.*)");
    @Override
    public boolean tryInsertFields(Map<String, String> fieldMap, String description) {
        Matcher matcher = _pattern.matcher(description);
        if (!matcher.find()) {
            return false;
        } else {
            fieldMap.put("type", "JdbcCalc");
            fieldMap.put("description", matcher.group(1));
            return true;
        }
    }
}

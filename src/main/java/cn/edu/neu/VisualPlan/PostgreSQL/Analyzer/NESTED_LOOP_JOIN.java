package cn.edu.neu.VisualPlan.PostgreSQL.Analyzer;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NESTED_LOOP_JOIN implements AccessPath{
    private static AccessPath _instance = new NESTED_LOOP_JOIN();

    private NESTED_LOOP_JOIN() {

    }

    public static AccessPath getInstance() {
        return _instance;
    }

    private Pattern _pattern = Pattern.compile("Nested Loop (.*)");
    @Override
    public boolean tryInsertFields(Map<String, String> fieldMap, String description) {
        Matcher matcher = _pattern.matcher(description);
        if (!matcher.find()) {
            return false;
        } else {
            fieldMap.put("type", "NESTED_LOOP_JOIN");
            return true;
        }
    }
}

package cn.edu.neu.VisualPlan.Calcite.Analyzer;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnumerableLimit implements AccessPath {
    private static AccessPath _instance = new EnumerableLimit();

    private EnumerableLimit() {

    }

    public static AccessPath getInstance() {
        return _instance;
    }

    private Pattern _pattern = Pattern.compile("EnumerableLimit(.*)");
    @Override
    public boolean tryInsertFields(Map<String, String> fieldMap, String description) {
        Matcher matcher = _pattern.matcher(description);
        if (!matcher.find()) {
            return false;
        } else {
            fieldMap.put("type", "EnumerableLimit");
            fieldMap.put("description", matcher.group(1));
            return true;
        }
    }
}

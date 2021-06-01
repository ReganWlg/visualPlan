package cn.edu.neu.VisualPlan.Calcite.Analyzer;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnumerableAggregate implements AccessPath {
    private static AccessPath _instance = new EnumerableAggregate();

    private EnumerableAggregate() {

    }

    public static AccessPath getInstance() {
        return _instance;
    }

    private Pattern _pattern = Pattern.compile("EnumerableAggregate(.*)");
    @Override
    public boolean tryInsertFields(Map<String, String> fieldMap, String description) {
        Matcher matcher = _pattern.matcher(description);
        if (!matcher.find()) {
            return false;
        } else {
            fieldMap.put("type", "EnumerableAggregate");
            fieldMap.put("description", matcher.group(1));
            return true;
        }
    }
}

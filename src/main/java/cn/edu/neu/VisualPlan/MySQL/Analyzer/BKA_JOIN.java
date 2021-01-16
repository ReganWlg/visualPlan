package cn.edu.neu.VisualPlan.MySQL.Analyzer;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BKA_JOIN implements AccessPath {
    private static AccessPath _instance = new BKA_JOIN();

    private BKA_JOIN() {
    }

    public static AccessPath getInstance() {
        return _instance;
    }

    private Pattern _pattern = Pattern.compile("Batched key access (.*)");
    @Override
    public boolean tryInsertFields(Map<String,String> fieldMap, String description) {
        Matcher matcher = _pattern.matcher(description);
        if (!matcher.find()) {
            return false;
        }
        fieldMap.put("type", "BKA_JOIN");
        fieldMap.put("1", matcher.group(1));
        return true;
    }
}

package cn.edu.neu.VisualPlan.MySQL.Analyzer;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NESTED_LOOP_SEMIJOIN_WITH_DUPLICATE_REMOVAL implements AccessPath {
    private static AccessPath _instance = new NESTED_LOOP_SEMIJOIN_WITH_DUPLICATE_REMOVAL();

    private NESTED_LOOP_SEMIJOIN_WITH_DUPLICATE_REMOVAL() {
    }

    public static AccessPath getInstance() {
        return _instance;
    }

    private Pattern _pattern = Pattern.compile("Nested loop semijoin with duplicate removal on (.*)");
    @Override
    public boolean tryInsertFields(Map<String,String> fieldMap, String description) {
        Matcher matcher = _pattern.matcher(description);
        if (!matcher.find()) {
            return false;
        }
        fieldMap.put("type", "NESTED_LOOP_\nSEMIJOIN_WITH_\nDUPLICATE_REMOVAL");
        fieldMap.put("1", matcher.group(1));
        return true;
    }
}

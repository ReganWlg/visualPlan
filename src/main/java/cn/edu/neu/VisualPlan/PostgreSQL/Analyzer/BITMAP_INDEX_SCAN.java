package cn.edu.neu.VisualPlan.PostgreSQL.Analyzer;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BITMAP_INDEX_SCAN implements AccessPath{
    private static AccessPath _instance = new BITMAP_INDEX_SCAN();

    private BITMAP_INDEX_SCAN() {

    }

    public static AccessPath getInstance() {
        return _instance;
    }

    private Pattern _pattern = Pattern.compile("Bitmap Index Scan (.*)");
    @Override
    public boolean tryInsertFields(Map<String, String> fieldMap, String description) {
        Matcher matcher = _pattern.matcher(description);
        if (!matcher.find()) {
            return false;
        } else {
            fieldMap.put("type", "BITMAP_INDEX_SCAN");
            return true;
        }
    }
}
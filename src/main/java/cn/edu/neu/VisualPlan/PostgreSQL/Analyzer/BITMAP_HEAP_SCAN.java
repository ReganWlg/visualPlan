package cn.edu.neu.VisualPlan.PostgreSQL.Analyzer;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BITMAP_HEAP_SCAN implements AccessPath{
    private static AccessPath _instance = new BITMAP_HEAP_SCAN();

    private BITMAP_HEAP_SCAN() {

    }

    public static AccessPath getInstance() {
        return _instance;
    }

    private Pattern _pattern = Pattern.compile("Bitmap Heap Scan (.*)");
    @Override
    public boolean tryInsertFields(Map<String, String> fieldMap, String description) {
        Matcher matcher = _pattern.matcher(description);
        if (!matcher.find()) {
            return false;
        } else {
            fieldMap.put("type", "BITMAP_HEAP_SCAN");
            return true;
        }
    }
}

package cn.edu.neu.VisualPlan.MySQL.Analyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Analyzer {
    private static Analyzer _instance = new Analyzer();

    private Analyzer() {
        initAccessPathList();
    }

    public static Analyzer getInstance() {
        return _instance;
    }

    private Pattern _TimingPattern1 = Pattern.compile("(.*) [(]actual time=(.*)[.][.](.*) rows=(.*) loops=(.*)[)]");
    private Pattern _TimingPattern2 = Pattern.compile("(.*) [(](never executed)[)]");
    private Pattern _CostAndRowsPattern = Pattern.compile("(.*) [(]cost=(.*) rows=(.*)[)]");
    public Map<String, String> getFieldMapByDescription(String description) {
        Map<String, String> fieldMap = new HashMap<>();

        Matcher timingMatcher1 = _TimingPattern1.matcher(description);
        if (timingMatcher1.find()) {
            fieldMap.put("Timing_first_row_ms", timingMatcher1.group(2));
            fieldMap.put("Timing_last_row_ms", timingMatcher1.group(3));
            fieldMap.put("Timing_rows", timingMatcher1.group(4));
            fieldMap.put("Timing_loops", timingMatcher1.group(5));
            description = timingMatcher1.group(1);
        } else {
            Matcher timingMatcher2 = _TimingPattern2.matcher(description);
            if (timingMatcher2.find()) {
                fieldMap.put("TimingString", timingMatcher2.group(2));
                description = timingMatcher2.group(1);
            }
        }

        Matcher costAndRowsMatcher = _CostAndRowsPattern.matcher(description);
        if (costAndRowsMatcher.find()) {
            fieldMap.put("description", costAndRowsMatcher.group(1));
            fieldMap.put("cost", costAndRowsMatcher.group(2));
            fieldMap.put("output_rows", costAndRowsMatcher.group(3));
            description = costAndRowsMatcher.group(1);
        }

        for (AccessPath accessPath : _accessPathList) {
            if (accessPath.tryInsertFields(fieldMap, description)) {
                break;
            }
        }

        return fieldMap;
    }

    private List<AccessPath> _accessPathList = null;
    private void initAccessPathList() {
        _accessPathList = new ArrayList<>();
        _accessPathList.add(TABLE_SCAN.getInstance());
        _accessPathList.add(INDEX_SCAN.getInstance());
        _accessPathList.add(REF.getInstance());
        _accessPathList.add(REF_OR_NULL.getInstance());
        _accessPathList.add(EQ_REF.getInstance());
        _accessPathList.add(PUSHED_JOIN_REF.getInstance());
        _accessPathList.add(FULL_TEXT_SEARCH.getInstance());
        _accessPathList.add(CONST_TABLE.getInstance());
        _accessPathList.add(MRR.getInstance());
        _accessPathList.add(FOLLOW_TAIL.getInstance());
        _accessPathList.add(INDEX_RANGE_SCAN.getInstance());
        _accessPathList.add(DYNAMIC_INDEX_RANGE_SCAN.getInstance());
        _accessPathList.add(TABLE_VALUE_CONSTRUCTOR.getInstance());
        _accessPathList.add(FAKE_SINGLE_ROW.getInstance());
        _accessPathList.add(ZERO_ROWS.getInstance());
        _accessPathList.add(ZERO_ROWS_AGGREGATED.getInstance());
        _accessPathList.add(MATERIALIZED_TABLE_FUNCTION.getInstance());
        _accessPathList.add(UNQUALIFIED_COUNT.getInstance());
        _accessPathList.add(NESTED_LOOP_JOIN.getInstance());
        _accessPathList.add(NESTED_LOOP_SEMIJOIN_WITH_DUPLICATE_REMOVAL.getInstance());
        _accessPathList.add(BKA_JOIN.getInstance());
        _accessPathList.add(HASH_JOIN.getInstance());
        _accessPathList.add(FILTER.getInstance());
        _accessPathList.add(SORT.getInstance());
        _accessPathList.add(AGGREGATE.getInstance());
        _accessPathList.add(TEMPTABLE_AGGREGATE.getInstance());
        _accessPathList.add(LIMIT_OFFSET.getInstance());
        _accessPathList.add(STREAM.getInstance());
        _accessPathList.add(MATERIALIZE.getInstance());
        _accessPathList.add(MATERIALIZE_INFORMATION_SCHEMA_TABLE.getInstance());
        _accessPathList.add(APPEND.getInstance());
        _accessPathList.add(WINDOWING.getInstance());
        _accessPathList.add(WEEDOUT.getInstance());
        _accessPathList.add(REMOVE_DUPLICATES.getInstance());
        _accessPathList.add(ALTERNATIVE.getInstance());
        _accessPathList.add(CACHE_INVALIDATOR.getInstance());
    }
}
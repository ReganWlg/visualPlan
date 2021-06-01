package cn.edu.neu.VisualPlan.Calcite.Analyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Analyzer {

    private static Analyzer _instance = new Analyzer();

    private Analyzer() {
        initAccessPathList();
    }

    public static Analyzer getInstance() {
        return _instance;
    }

    private Pattern _CostPattern = Pattern.compile(
            "(.*): rowcount = (.*), cumulative cost = [{](.*) rows, (.*) cpu, (.*) io[}], id = (.*)");
    public Map<String, String> getFieldMapByDescription(String description) {
        Map<String, String> fieldMap = new HashMap<>();

        Matcher costMatcher = _CostPattern.matcher(description);
        if (costMatcher.find()) {
            fieldMap.put("rowCount", costMatcher.group(2));
            fieldMap.put("rows", costMatcher.group(3));
            fieldMap.put("cpu", costMatcher.group(4));
            fieldMap.put("io", costMatcher.group(5));
            fieldMap.put("id", costMatcher.group(6));
            description = costMatcher.group(1);
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
        _accessPathList.add(EnumerableAggregate.getInstance());
        _accessPathList.add(EnumerableCorrelate.getInstance());
        _accessPathList.add(EnumerableFilter.getInstance());
        _accessPathList.add(EnumerableHashJoin.getInstance());
        _accessPathList.add(EnumerableLimit.getInstance());
        _accessPathList.add(EnumerableNestedLoopJoin.getInstance());
        _accessPathList.add(EnumerableProject.getInstance());
        _accessPathList.add(EnumerableSort.getInstance());
        _accessPathList.add(JdbcAggregate.getInstance());
        _accessPathList.add(JdbcCalc.getInstance());
        _accessPathList.add(JdbcFilter.getInstance());
        _accessPathList.add(JdbcIntersect.getInstance());
        _accessPathList.add(JdbcJoin.getInstance());
        _accessPathList.add(JdbcMinus.getInstance());
        _accessPathList.add(JdbcProject.getInstance());
        _accessPathList.add(JdbcSort.getInstance());
        _accessPathList.add(JdbcTableModify.getInstance());
        _accessPathList.add(JdbcTableScan.getInstance());
        _accessPathList.add(JdbcToEnumerableConverter.getInstance());
        _accessPathList.add(JdbcUnion.getInstance());
        _accessPathList.add(JdbcValues.getInstance());
    }
}

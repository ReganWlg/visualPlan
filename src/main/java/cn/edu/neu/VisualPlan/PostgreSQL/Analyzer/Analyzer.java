package cn.edu.neu.VisualPlan.PostgreSQL.Analyzer;

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

    private final Pattern _ActualDataPattern1 = Pattern.compile("(.*) [(]actual time=(.*)[.][.](.*) rows=(.*) loops=(.*)[)]");
    private final Pattern _ActualDataPattern2 = Pattern.compile("(.*) [(](never executed)[)]");
    private final Pattern _EstimateDataPattern = Pattern.compile("(.*) [(]cost=(.*)[.][.](.*) rows=(.*) width=(.*)[)]");
    private final Pattern _PlanningTimePattern = Pattern.compile("Planning Time: (.*)");
    private final Pattern _ExecutionTimePattern = Pattern.compile("Execution Time: (.*)");
    private final Pattern _SubPlanPattern = Pattern.compile("SubPlan [1-9]+[0-9]*$");
    public Map<String, String> getFieldMapByDescription(ArrayList<String> description) {
        Map<String, String> fieldMap = new HashMap<>();
        String firstDescription = description.get(0);

        Matcher subPlanMatcher = _SubPlanPattern.matcher(firstDescription);
        if (subPlanMatcher.find()) {
            fieldMap.put("type", "SUB_PLAN");
            fieldMap.put("description", firstDescription);
        }

        // 向fieldmap中添加"actual_time_each_row_ms"
        //                "actual_time_all_rows_ms"
        //                "actual_rows"
        //                "actual_loops"
        Matcher actualDataMatcher1 = _ActualDataPattern1.matcher(firstDescription);
        Matcher actualDataMatcher2 = _ActualDataPattern2.matcher(firstDescription);
        if (actualDataMatcher1.find()) {
            fieldMap.put("actual_time_each_row_ms", actualDataMatcher1.group(2));
            fieldMap.put("actual_time_all_rows_ms", actualDataMatcher1.group(3));
            fieldMap.put("actual_rows", actualDataMatcher1.group(4));
            fieldMap.put("actual_loops", actualDataMatcher1.group(5));
            firstDescription = actualDataMatcher1.group(1);
        } else if (actualDataMatcher2.find()) {
            fieldMap.put("TimingString", actualDataMatcher2.group(2));
            firstDescription = actualDataMatcher2.group(1);
        }

        // 向fieldmap中添加"estimate_cost_each_row_ms"
        //                "estimate_cost_all_rows_ms"
        //                "estimate_rows"
        //                "estimate_width"
        Matcher estimateDataMatcher = _EstimateDataPattern.matcher(firstDescription);
        if (estimateDataMatcher.find()) {
            fieldMap.put("estimate_cost_each_row_ms", estimateDataMatcher.group(2));
            fieldMap.put("estimate_cost_all_rows_ms", estimateDataMatcher.group(3));
            fieldMap.put("estimate_rows", estimateDataMatcher.group(4));
            fieldMap.put("estimate_width", estimateDataMatcher.group(5));
            firstDescription = estimateDataMatcher.group(1);
        }

        // 向fieldmap中添加"type"
        for (AccessPath accessPath : _accessPathList) {
            if (accessPath.tryInsertFields(fieldMap, firstDescription)) {
                break;
            }
        }

        // 向fieldmap中添加"description"
        StringBuilder totalDescription = new StringBuilder();
        int i = 0;
        for (String eachDescription : description) {
            // 若为第一条，添加处理过的firstDescription
            if (i == 0) {
                totalDescription.append(firstDescription);
            }
            // 添加后续描述时，判断是不是Execution Time或Planning Time
            // 若是，添加到fieldmap中
            // 若不是，添加到totalDescription中
            else {
                Matcher executionTimeMatcher = _ExecutionTimePattern.matcher(eachDescription);
                Matcher planningTimeMatcher = _PlanningTimePattern.matcher(eachDescription);
                if (executionTimeMatcher.find()) {
                    fieldMap.put("execution_time", executionTimeMatcher.group(1));
                    continue;
                } else if (planningTimeMatcher.find()) {
                    fieldMap.put("planning_time", planningTimeMatcher.group(1));
                    continue;
                }
                totalDescription.append("\n--> ").append(eachDescription);
            }
            i++;
        }
        fieldMap.put("description", totalDescription.toString());

        return fieldMap;
    }

    private List<AccessPath> _accessPathList = null;
    private void initAccessPathList() {
        _accessPathList = new ArrayList<>();
        _accessPathList.add(AGGREGATE.getInstance());
        _accessPathList.add(APPEND.getInstance());
        _accessPathList.add(BITMAP_HEAP_SCAN.getInstance());
        _accessPathList.add(BITMAP_INDEX_SCAN.getInstance());
        _accessPathList.add(FUNCTION_SCAN.getInstance());
        _accessPathList.add(GATHER.getInstance());
        _accessPathList.add(GROUP.getInstance());
        _accessPathList.add(HASH_JOIN.getInstance());
        _accessPathList.add(HASH.getInstance());
        _accessPathList.add(INDEX_SCAN.getInstance());
        _accessPathList.add(LIMIT.getInstance());
        _accessPathList.add(MATERIALIZE.getInstance());
        _accessPathList.add(MERGE_JOIN.getInstance());
        _accessPathList.add(NESTED_LOOP_JOIN.getInstance());
        _accessPathList.add(RESULT.getInstance());
        _accessPathList.add(SEQ_SCAN.getInstance());
        _accessPathList.add(SETOP.getInstance());
        _accessPathList.add(SORT.getInstance());
        _accessPathList.add(SUBQUERY_SCAN.getInstance());
        _accessPathList.add(TID_SCAN.getInstance());
        _accessPathList.add(UNIQUE.getInstance());
    }
}

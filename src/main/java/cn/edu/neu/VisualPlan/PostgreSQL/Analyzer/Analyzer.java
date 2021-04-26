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

    private Pattern _ActualDataPattern1 = Pattern.compile("(.*) [(]actual time=(.*)[.][.](.*) rows=(.*) loops=(.*)[)]");
    private Pattern _ActualDataPattern2 = Pattern.compile("(.*) [(](never executed)[)]");
    private Pattern _EstimateDataPattern = Pattern.compile("(.*) [(]cost=(.*)[.][.](.*) rows=(.*) width=(.*)[)]");
    public Map<String, String> getFieldMapByDescription(String description) {
        Map<String, String> fieldMap = new HashMap<>();

        Matcher actualDataMatcher1 = _ActualDataPattern1.matcher(description);
        Matcher actualDataMatcher2 = _ActualDataPattern2.matcher(description);
        if (actualDataMatcher1.find()) {
            fieldMap.put("actual_time_each_row_ms", actualDataMatcher1.group(2));
            fieldMap.put("actual_time_all_rows_ms", actualDataMatcher1.group(3));
            fieldMap.put("actual_rows", actualDataMatcher1.group(4));
            fieldMap.put("actual_loops", actualDataMatcher1.group(5));
            description = actualDataMatcher1.group(1);
        } else if (actualDataMatcher2.find()) {
            fieldMap.put("TimingString", actualDataMatcher2.group(2));
            description = actualDataMatcher2.group(1);
        }

        Matcher estimateDataMatcher = _EstimateDataPattern.matcher(description);
        if (estimateDataMatcher.find()) {
            fieldMap.put("description", estimateDataMatcher.group(1));
            fieldMap.put("estimate_cost_each_row_ms", estimateDataMatcher.group(2));
            fieldMap.put("estimate_cost_all_rows_ms", estimateDataMatcher.group(3));
            fieldMap.put("estimate_rows", estimateDataMatcher.group(4));
            fieldMap.put("estimate_width", estimateDataMatcher.group(5));
            description = estimateDataMatcher.group(1);
        }

//        for (AccessPath accessPath : _accessPathList) {
//            if (accessPath.tryInsertFields(fieldMap, description)) {
//                break;
//            }
//        }

        return fieldMap;
    }

    private Pattern _PlanningTimePattern = Pattern.compile("Planning Time: (.*)");
    private Pattern _ExecutionTimePattern = Pattern.compile("Execution Time: (.*)");
    public void getFieldMapByPlanningTimeAndExecutionTime(Map<String, String> fieldMap, ArrayList<String> description) {
        int length = description.size();
        Matcher planningTimeMatcher = _PlanningTimePattern.matcher(description.get(length - 2));
        Matcher executionTimeMatcher = _ExecutionTimePattern.matcher(description.get(length - 1));
        if (planningTimeMatcher.find()) {
            fieldMap.put("planning_time", planningTimeMatcher.group(1));
        }
        if (executionTimeMatcher.find()) {
            fieldMap.put("execution_time", executionTimeMatcher.group(1));
        }
    }

    private List<AccessPath> _accessPathList = null;
    private void initAccessPathList() {

    }
}

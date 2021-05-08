package cn.edu.neu.VisualPlan.Calcite.Analyzer;

import java.util.Map;

public interface AccessPath {
    boolean tryInsertFields(Map<String, String> fieldMap, String description);
}

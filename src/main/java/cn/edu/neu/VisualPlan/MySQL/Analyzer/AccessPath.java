package cn.edu.neu.VisualPlan.MySQL.Analyzer;

import java.util.Map;

public interface AccessPath {
    boolean tryInsertFields(Map<String, String> fieldMap, String description);
}
package cn.edu.neu.VisualPlan.PostgreSQL.Analyzer;

import java.util.Map;

public interface AccessPath {
    boolean tryInsertFields(Map<String, String> fieldMap, String description);
}

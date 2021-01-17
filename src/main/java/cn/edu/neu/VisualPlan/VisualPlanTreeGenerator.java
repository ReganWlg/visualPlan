package cn.edu.neu.VisualPlan;

import java.sql.Statement;
import java.sql.SQLException;

public interface VisualPlanTreeGenerator {
    VisualPlanNode getVisualPlanTree(Statement stmt, String sql) throws SQLException;
}
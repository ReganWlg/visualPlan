package cn.edu.neu.VisualPlan;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public interface VisualPlanTreeGenerator {
    VisualPlanNode getVisualPlanTree(Connection conn, String sql, int index) throws SQLException;
}
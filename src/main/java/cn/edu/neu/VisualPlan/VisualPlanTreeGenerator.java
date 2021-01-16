package cn.edu.neu.VisualPlan;

import java.sql.Statement;
import java.sql.SQLException;

public abstract class VisualPlanTreeGenerator {
    protected VisualPlanTreeGenerator() {

    }

    protected abstract String getPlanRawString(Statement stmt, String sql) throws SQLException;

    protected abstract VisualPlanNode convVisualPlanTree(String planRawString);

    public final VisualPlanNode getVisualPlanTree(Statement stmt, String sql) throws SQLException {
        String planRawString = getPlanRawString(stmt, sql);
        return convVisualPlanTree(planRawString);
    }
}
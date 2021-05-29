package cn.edu.neu.VisualPlan;

import cn.edu.neu.VisualPlan.Calcite.CalciteVisualPlanTreeGenerator;
import cn.edu.neu.VisualPlan.MySQL.MySQLVisualPlanTreeGenerator;
import cn.edu.neu.VisualPlan.PostgreSQL.PostgreSQLVisualPlanTreeGenerator;

public final class VisualPlanTreeGeneratorFactory {
    private VisualPlanTreeGeneratorFactory() {

    }

    public static VisualPlanTreeGenerator create(String dbms) {
        switch (dbms) {
            case "mysql":
                return MySQLVisualPlanTreeGenerator.getInstance();
            case "postgresql":
                return PostgreSQLVisualPlanTreeGenerator.getInstance();
            case "mysql_calcite":
            case "postgresql_calcite":
                return CalciteVisualPlanTreeGenerator.getInstance();
            default:
                return null;
        }
    }
}
package cn.edu.neu.VisualPlan;

import cn.edu.neu.VisualPlan.Calcite.CalciteVisualPlanTreeGenerator;
import cn.edu.neu.VisualPlan.MySQL.MySQLVisualPlanTreeGenerator;
import cn.edu.neu.VisualPlan.PostgreSQL.PostgreSQLVisualPlanTreeGenerator;

public final class VisualPlanTreeGeneratorFactory {
    private VisualPlanTreeGeneratorFactory() {

    }

    public static VisualPlanTreeGenerator create(String dbms) {
        if (dbms.equals("mysql")) {
            return MySQLVisualPlanTreeGenerator.getInstance();
        } else if (dbms.equals("postgresql")) {
            return PostgreSQLVisualPlanTreeGenerator.getInstance();
        } else if (dbms.equals("mysql_calcite") || dbms.equals("postgresql_calcite")) {
            return CalciteVisualPlanTreeGenerator.getInstance();
        }
        return null;
    }
}
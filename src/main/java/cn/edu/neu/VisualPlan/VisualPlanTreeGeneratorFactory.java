package cn.edu.neu.VisualPlan;

import cn.edu.neu.VisualPlan.MySQL.MySQLVisualPlanTreeGenerator;

public final class VisualPlanTreeGeneratorFactory {
    private VisualPlanTreeGeneratorFactory() {

    }

    public static VisualPlanTreeGenerator create(String dbms) {
        if (dbms.equals("mysql")) {
            return MySQLVisualPlanTreeGenerator.getInstance();
        }
        return null;
    }
}
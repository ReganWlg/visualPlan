package cn.edu.neu.Demo;

import cn.edu.neu.VisualPlan.Graphics.Util.PrintHandler;
import cn.edu.neu.VisualPlan.MySQL.MySQLVisualPlanTreeGenerator;
import cn.edu.neu.VisualPlan.VisualPlanNode;
import cn.edu.neu.VisualPlan.VisualPlanTreeGenerator;
import cn.edu.neu.VisualPlan.VisualPlanTreeGeneratorFactory;

public class Demo02 {
    public static void main(String[] args) {
        String planRawString =
                "-> Nested loop left join  (cost=0.70 rows=1)\n" +
                        "    -> Table scan on t1  (cost=0.35 rows=1)\n" +
                        "    -> Filter: (t3.b is null)  (cost=0.70 rows=1)\n" +
                        "        -> Nested loop left join  (cost=0.70 rows=1)\n" +
                        "            -> Table scan on t2  (cost=0.35 rows=1)\n" +
                        "            -> Filter: (t3.a = t2.a)  (cost=0.35 rows=1)\n" +
                        "                -> Table scan on t3  (cost=0.35 rows=1)";

        VisualPlanTreeGenerator visualPlanTreeGenerator = VisualPlanTreeGeneratorFactory.create("mysql");
        MySQLVisualPlanTreeGenerator mySQLVisualPlanTreeGenerator = (MySQLVisualPlanTreeGenerator)visualPlanTreeGenerator;
        VisualPlanNode root = mySQLVisualPlanTreeGenerator.convVisualPlanTree(planRawString);
        System.out.println("levelOrder: ");
        PrintHandler printHandler = new PrintHandler(0);
        printHandler.draw(root, 0);
        // UI与控制层分离后，此部分不再使用，使用Demo03模式
        // Application.launch(MainStage.class, args);
    }
}
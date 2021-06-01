package cn.edu.neu.Demo;

import cn.edu.neu.VisualPlan.Graphics.Util.PrintHandler;
import cn.edu.neu.VisualPlan.PostgreSQL.PostgreSQLVisualPlanTreeGenerator;
import cn.edu.neu.VisualPlan.VisualPlanNode;
import cn.edu.neu.VisualPlan.VisualPlanTreeGenerator;
import cn.edu.neu.VisualPlan.VisualPlanTreeGeneratorFactory;

import java.util.ArrayList;

public class Demo05 {
    public static void main(String[] args) {
        ArrayList<String> planRawString = new ArrayList<>();
        planRawString.add("Sort  (cost=717.34..717.59 rows=101 width=488) (actual time=7.761..7.774 rows=100 loops=1)");
        planRawString.add("  Sort Key: t1.fivethous");
        planRawString.add("  Sort Method: quicksort  Memory: 77kB");
        planRawString.add("  ->  Hash Join  (cost=230.47..713.98 rows=101 width=488) (actual time=0.711..7.427 rows=100 loops=1)");
        planRawString.add("        Hash Cond: (t2.unique2 = t1.unique2)");
        planRawString.add("        ->  Seq Scan on tenk2 t2  (cost=0.00..445.00 rows=10000 width=244) (actual time=0.007..2.583 rows=10000 loops=1)");
        planRawString.add("        ->  Hash  (cost=229.20..229.20 rows=101 width=244) (actual time=0.659..0.659 rows=100 loops=1)");
        planRawString.add("              Buckets: 1024  Batches: 1  Memory Usage: 28kB");
        planRawString.add("              ->  Bitmap Heap Scan on tenk1 t1  (cost=5.07..229.20 rows=101 width=244) (actual time=0.080..0.526 rows=100 loops=1)");
        planRawString.add("                    Recheck Cond: (unique1 < 100)");
        planRawString.add("                    ->  Bitmap Index Scan on tenk1_unique1  (cost=0.00..5.04 rows=101 width=0) (actual time=0.049..0.049 rows=100 loops=1)");
        planRawString.add("                          Index Cond: (unique1 < 100)");
        planRawString.add("Planning Time: 0.194 ms");
        planRawString.add("Execution Time: 8.008 ms");

        VisualPlanTreeGenerator visualPlanTreeGenerator = VisualPlanTreeGeneratorFactory.create("postgresql");
        PostgreSQLVisualPlanTreeGenerator postgreSQLVisualPlanTreeGenerator = (PostgreSQLVisualPlanTreeGenerator) visualPlanTreeGenerator;
        VisualPlanNode root = postgreSQLVisualPlanTreeGenerator.convVisualPlanTree(planRawString);
        System.out.println("levelOrder: ");
        PrintHandler printHandler = new PrintHandler(0);
        printHandler.draw(root, 0);
        // UI与控制层分离后，此部分不再使用，使用Demo03模式
        // Application.launch(MainStage.class, args);
    }
}

package cn.edu.neu.Demo;

import cn.edu.neu.VisualPlan.Graphics.Util.PrintHandler;
import cn.edu.neu.VisualPlan.VisualPlanNode;
import cn.edu.neu.VisualPlan.VisualPlanTreeGenerator;
import cn.edu.neu.VisualPlan.VisualPlanTreeGeneratorFactory;

import java.sql.*;

public class Demo04 {
    public static void main(String[] args) {

        String dbms = "postgresql";
        String ip = "localhost";
        String port = "5432";
        String database = "tpc";
        String url = String.format("jdbc:%s://%s:%s/%s", dbms, ip, port, database);
        String user = "postgres";
        String password = "regan0429";
        String sql = "select * \n" +
                "from tpch.part \n" +
                "where p_mfgr='Manufacturer#5' and p_brand='Brand#53';";

        Connection conn = null;
        Statement stmt = null;
        try {
            // 获取数据库连接对象
            conn = DriverManager.getConnection(url, user, password);
            // 获取执行计划可视化数据生成器对象
            VisualPlanTreeGenerator visualPlanTreeGenerator = VisualPlanTreeGeneratorFactory.create(dbms);
            // 获取执行计划可视化数据
            VisualPlanNode root = visualPlanTreeGenerator.getVisualPlanTree(conn, sql);
            System.out.println("levelOrder: ");
            PrintHandler printHandler = new PrintHandler();
            printHandler.draw(root, 0);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // 释放资源
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

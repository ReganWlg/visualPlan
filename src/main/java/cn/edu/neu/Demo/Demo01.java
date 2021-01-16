package cn.edu.neu.Demo;

import cn.edu.neu.VisualPlan.*;
import cn.edu.neu.VisualPlan.Graphics.MainStage;
import cn.edu.neu.VisualPlan.Graphics.PrintHandler;
import javafx.application.Application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Demo01 {
    public static void main(String[] args) {

        String dbms = "mysql";
//        String ip = "219.216.64.166";
        String ip = "localhost";
//        String port = "13306";
        String port = "3306";
//        String database = "information_schema";
        String database = "information_schema?useSSL=true&serverTimezone=UTC";
        String url = String.format("jdbc:%s://%s:%s/%s", dbms, ip, port, database);
        String user = "root";
        String password = "123456";
        String sql = "select * from VIEWS";

        Connection conn = null;
        Statement stmt = null;
        try {
            // 获取数据库连接对象
            conn = DriverManager.getConnection(url, user, password);
            // 获取执行 SQL 对象
            stmt = conn.createStatement();
            // 获取执行计划可视化数据生成器对象
            VisualPlanTreeGenerator visualPlanTreeGenerator = VisualPlanTreeGeneratorFactory.create(dbms);
            // 获取执行计划可视化数据
            VisualPlanNode root = visualPlanTreeGenerator.getVisualPlanTree(stmt, sql);
            // 前端使用数据
            // todo by li geng, example:
//            class PrintHandler implements VisualPlanHandler {
//                public void onCall(VisualPlanNode node) {
//                    System.out.println(node.toString());
//                }
//            }

//            root.preOrderHandle(new PrintHandler());
            System.out.println("levelOrder: ");
            root.levelHandle(new PrintHandler());
//            root.postOrderHandle(new PrintHandler());

            Application.launch(MainStage.class, args);
            /*
            System.out.println("level:");
            root.levelHandle(new PrintHandler());
            System.out.println("postOrder:");
            root.postOrderHandle(new PrintHandler());
             */
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // 释放资源
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
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
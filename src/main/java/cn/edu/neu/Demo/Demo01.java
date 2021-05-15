package cn.edu.neu.Demo;

import cn.edu.neu.VisualPlan.*;
import cn.edu.neu.VisualPlan.Graphics.Util.PrintHandler;

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
            // 获取执行计划可视化数据生成器对象
            VisualPlanTreeGenerator visualPlanTreeGenerator = VisualPlanTreeGeneratorFactory.create(dbms);
            // 获取执行计划可视化数据
            VisualPlanNode root = visualPlanTreeGenerator.getVisualPlanTree(conn, sql);
            System.out.println("levelOrder: ");
            PrintHandler printHandler = new PrintHandler();
            printHandler.draw(root, 0);
            // UI与控制层分离后，此部分不再使用，使用Demo03模式
            // Application.launch(MainStage.class, args);
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
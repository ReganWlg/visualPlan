package cn.edu.neu.VisualPlan.Graphics.Control;

import cn.edu.neu.VisualPlan.Graphics.Util.PrintHandler;
import cn.edu.neu.VisualPlan.Graphics.Util.StageManager;
import cn.edu.neu.VisualPlan.VisualPlanNode;
import cn.edu.neu.VisualPlan.VisualPlanTreeGenerator;
import cn.edu.neu.VisualPlan.VisualPlanTreeGeneratorFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class MainStageControl implements Initializable {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Label l_dbms;
    @FXML
    private Label l_ip;
    @FXML
    private Label l_port;
    @FXML
    private Label l_database;
    @FXML
    private Label l_user;
    @FXML
    private Label l_sql;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ConnectDBControl connectDBControl = (ConnectDBControl) StageManager.CONTROLLER.get("ConnectDBControl");
        l_dbms.setText(connectDBControl.getDbms());
        l_ip.setText(connectDBControl.getIp());
        l_port.setText(connectDBControl.getPort());
        l_database.setText(connectDBControl.getDatabase());
        String database = l_database.getText();
        if (l_ip.getText().equals("localhost")) {
            database += "?useSSL=true&serverTimezone=UTC";
        }
        l_user.setText(connectDBControl.getUser());
        String sql = "select * from VIEWS";
        l_sql.setText(sql);

        Connection conn = null;
        Statement stmt = null;
        try {
            conn = connectDBControl.getConn();
            // 获取执行 SQL 对象
            stmt = conn.createStatement();
            // 获取执行计划可视化数据生成器对象
            VisualPlanTreeGenerator visualPlanTreeGenerator = VisualPlanTreeGeneratorFactory.create(l_dbms.getText());
            // 获取执行计划可视化数据
            VisualPlanNode root = visualPlanTreeGenerator.getVisualPlanTree(stmt, sql);
            System.out.println("levelOrder: ");
            PrintHandler printHandler = new PrintHandler();
            printHandler.draw(root);
            scrollPane.setContent(printHandler.getRoot());

            StageManager.CONTROLLER.put("MainStageControl", this);
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

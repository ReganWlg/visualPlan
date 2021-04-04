package cn.edu.neu.VisualPlan.Graphics.Control;

import cn.edu.neu.VisualPlan.Graphics.Util.PrintHandler;
import cn.edu.neu.VisualPlan.Graphics.Util.StageManager;
import cn.edu.neu.VisualPlan.VisualPlanNode;
import cn.edu.neu.VisualPlan.VisualPlanTreeGenerator;
import cn.edu.neu.VisualPlan.VisualPlanTreeGeneratorFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
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
    private TextArea txt_sql;

    private Connection conn;
    private Statement stmt;

    // MainStage初始化
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ConnectDBControl connectDBControl = (ConnectDBControl) StageManager.CONTROLLER.get("ConnectDBControl");
        l_dbms.setText(connectDBControl.getDbms());
        l_ip.setText(connectDBControl.getIp());
        l_port.setText(connectDBControl.getPort());
        l_database.setText(connectDBControl.getDatabase());
        l_user.setText(connectDBControl.getUser());

        // 获取执行 SQL 对象
        try {
            conn = connectDBControl.getConn();
            stmt = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        StageManager.CONTROLLER.put("MainStageControl", this);
    }

    // 断开当前数据库连接
    public void disconnect(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("确认提示框");
        alert.setHeaderText("是否确认断开当前数据库？");
        alert.setContentText("确认断开将返回连接数据库界面");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            // 关闭当前主界面
            StageManager.STAGE.get("MainStage").close();
            // 删除map中主界面的引用
            StageManager.STAGE.remove("MainStage");
            StageManager.CONTROLLER.remove("MainStageControl");
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
            // 打开连接数据库界面
            Stage stage = StageManager.STAGE.get("ConnectDB");
            stage.show();
        }
    }

    // 查询当前SQL的执行计划
    public void queryExecutionPlan(ActionEvent event) {
        try {
            // 获取执行计划可视化数据生成器对象
            VisualPlanTreeGenerator visualPlanTreeGenerator = VisualPlanTreeGeneratorFactory.create(l_dbms.getText());
            // 获取执行计划可视化数据
            VisualPlanNode root = visualPlanTreeGenerator.getVisualPlanTree(stmt, txt_sql.getText());
            System.out.println("levelOrder: ");
            PrintHandler printHandler = new PrintHandler();
            printHandler.draw(root);
            scrollPane.setContent(printHandler.getRoot());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

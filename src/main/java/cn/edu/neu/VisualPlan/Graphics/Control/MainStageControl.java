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
    private Label l_sql;

    // MainStage初始化
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ConnectDBControl connectDBControl = (ConnectDBControl) StageManager.CONTROLLER.get("ConnectDBControl");
        l_dbms.setText(connectDBControl.getDbms());
        l_ip.setText(connectDBControl.getIp());
        l_port.setText(connectDBControl.getPort());
        l_database.setText(connectDBControl.getDatabase());
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

    // 断开当前数据库连接
    public void disconnect(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("确认提示框");
        alert.setHeaderText("是否确认断开当前数据库？");
        alert.setContentText("确认断开将返回连接数据库界面");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            StageManager.STAGE.get("MainStage").close();
            StageManager.STAGE.remove("MainStage");
            StageManager.CONTROLLER.remove("MainStageControl");
            Stage stage = StageManager.STAGE.get("ConnectDB");
            stage.show();
        }
    }
}

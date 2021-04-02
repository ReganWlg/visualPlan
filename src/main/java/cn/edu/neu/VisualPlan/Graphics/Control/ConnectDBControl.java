package cn.edu.neu.VisualPlan.Graphics.Control;

import cn.edu.neu.VisualPlan.Graphics.Util.StageManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ConnectDBControl implements Initializable {
    @FXML
    private TextField txtDBMS;
    @FXML
    private TextField txtIP;
    @FXML
    private TextField txtPort;
    @FXML
    private TextField txtDatabase;
    @FXML
    private TextField txtUser;
    @FXML
    private PasswordField pwdPassword;

    private String dbms;
    private String ip;
    private String port;
    private String database;
    private String user;
    private Connection conn;

    public String getDbms() {
        return dbms;
    }
    public String getIp() {
        return ip;
    }
    public String getPort() {
        return port;
    }
    public String getDatabase() {
        return database;
    }
    public String getUser() {
        return user;
    }
    public Connection getConn() {
        return conn;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void clear(ActionEvent event) {
        txtIP.setText("");
        txtPort.setText("");
        txtDatabase.setText("");
        txtUser.setText("");
        pwdPassword.setText("");
    }


    public void connect(ActionEvent event) throws Exception {
        dbms = txtDBMS.getText();
        ip = txtIP.getText();
        port = txtPort.getText();
        database = txtDatabase.getText();
        String database1 = database;
        if (ip.equals("localhost")) {
            database1 += "?useSSL=true&serverTimezone=UTC";
        }
        user = txtUser.getText();
        String password = pwdPassword.getText();

        String jdbcUrl = String.format("jdbc:%s://%s:%s/%s", dbms, ip, port, database1);
        try {
            conn = DriverManager.getConnection(jdbcUrl, user, password);

            // 将当前窗口控制器保存到map中
            StageManager.CONTROLLER.put("ConnectDBControl", this);
            // 关闭当前ConnectDB窗口
            StageManager.STAGE.get("ConnectDB").close();

            // 新建stage，加载MainStage窗口
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/MainStage.fxml"));
            stage.setTitle("查询执行计划可视化");
            stage.setMaximized(true);
            stage.setScene(new Scene(root));
            stage.show();

            // 将MainStage窗口保存到map中
            StageManager.STAGE.put("MainStage", stage);
        } catch (SQLException e) {
            // 弹出错误提示框
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("错误提示");
            alert.setHeaderText("数据库连接失败!");
            alert.setContentText("请检查参数，重新连接");

            // 将异常轨迹存入缓冲区
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);

            // 在弹窗中添加扩展区域，用于显示异常轨迹
            Label label = new Label("Exception stacktrace:");
            TextArea textArea = new TextArea(sw.toString());
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);

            GridPane expContent = new GridPane();
            expContent.setMaxWidth(Double.MAX_VALUE);
            expContent.add(label, 0, 0);
            expContent.add(textArea, 0, 1);

            alert.getDialogPane().setExpandableContent(expContent);
            alert.showAndWait();
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

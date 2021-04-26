package cn.edu.neu.VisualPlan.Graphics.Control;

import cn.edu.neu.VisualPlan.Graphics.Util.StageManager;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
    private ComboBox<String> cb_dbms;
    @FXML
    private TextField txt_ip;
    @FXML
    private TextField txt_port;
    @FXML
    private TextField txt_database;
    @FXML
    private TextField txt_user;
    @FXML
    private PasswordField pwd_password;

    private Connection conn;

    public String getDbms() {
        return cb_dbms.getValue();
    }
    public String getIp() {
        return txt_ip.getText();
    }
    public String getPort() {
        return txt_port.getText();
    }
    public String getDatabase() {
        return txt_database.getText();
    }
    public String getUser() {
        return txt_user.getText();
    }
    public Connection getConn() {
        return conn;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    // “重置”按钮
    public void clear(ActionEvent event) {
        txt_ip.setText("");
        txt_port.setText("");
        txt_database.setText("");
        txt_user.setText("");
        pwd_password.setText("");
    }

    // “连接”按钮
    public void connect(ActionEvent event) throws Exception {
        
        String database1 = txt_database.getText();
        if (txt_ip.getText().equals("localhost") && cb_dbms.getValue().equals("mysql")) {
            database1 += "?useSSL=true&serverTimezone=UTC";
        }

        String jdbcUrl = String.format(
                "jdbc:%s://%s:%s/%s",
                cb_dbms.getValue(),
                txt_ip.getText(),
                txt_port.getText(),
                database1);
        try {
            conn = DriverManager.getConnection(
                    jdbcUrl,
                    txt_user.getText(),
                    pwd_password.getText());

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
            // 主界面组件宽度自适应
            stage.widthProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Scene scene = stage.getScene();
                            double width = stage.getWidth();
                            GridPane gridPane = (GridPane) scene.lookup("#gridPane");
                            ScrollPane scrollPane = (ScrollPane) scene.lookup("#scrollPane");
                            gridPane.setPrefWidth(width * 0.2);
                            scrollPane.setPrefWidth(width * 0.8);
                        }
                    });
                }
            });
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
        }
    }
}

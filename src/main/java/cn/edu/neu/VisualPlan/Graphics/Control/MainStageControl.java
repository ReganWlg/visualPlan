package cn.edu.neu.VisualPlan.Graphics.Control;

import cn.edu.neu.VisualPlan.Graphics.Util.PrintHandler;
import cn.edu.neu.VisualPlan.Graphics.Util.StageManager;
import cn.edu.neu.VisualPlan.PostgreSQL.PostgreSQLVisualPlanNode;
import cn.edu.neu.VisualPlan.VisualPlanNode;
import cn.edu.neu.VisualPlan.VisualPlanTreeGenerator;
import cn.edu.neu.VisualPlan.VisualPlanTreeGeneratorFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.Properties;
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
    private Label l_schema;
    @FXML
    private Label l_user;
    @FXML
    private TextArea txt_sql;
    @FXML
    private CheckBox cb_calcite;
    @FXML
    private Label l_planningTime;
    @FXML
    private Label l_executionTime;

    private Connection conn;

    // MainStage初始化
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ConnectDBControl connectDBControl = (ConnectDBControl) StageManager.CONTROLLER.get("ConnectDBControl");
        l_dbms.setText(connectDBControl.getDBMS());
        l_ip.setText(connectDBControl.getIp());
        l_port.setText(connectDBControl.getPort());
        l_database.setText(connectDBControl.getDatabase());
        if (connectDBControl.getSchema().equals("")) {
            l_schema.setText("无");
        } else {
            l_schema.setText(connectDBControl.getSchema());
        }
        l_user.setText(connectDBControl.getUser());

        if (connectDBControl.getDBMS().equals("postgresql")) {
            txt_sql.setText("Select P.P_NAME, P.P_mfgr,\n" +
                    "P.P_RETAILPRICE, PS.PS_SUPPLYCOST\n" +
                    "From part P, partsupp PS\n" +
                    "Where P.P_RETAILPRICE>PS.PS_SUPPLYCOST\n" +
                    "Limit 200 ");
//            txt_sql.setText("SELECT C_CUSTKEY, C_NAME\n" +
//                    "FROM CUSTOMER C\n" +
//                    "WHERE NOT EXISTS (\n" +
//                        "SELECT O.O_CUSTKEY\n" +
//                        "FROM Orders O, Lineitem L, PartSupp PS, Part P\n" +
//                        "WHERE C.C_CUSTKEY=O.O_CUSTKEY AND\n" +
//                            "O.O_ORDERKEY=L.L_ORDERKEY AND\n" +
//                            "L.L_PARTKEY=PS.PS_PARTKEY AND\n" +
//                            "L.L_SUPPKEY= PS.PS_SUPPKEY AND\n" +
//                            "PS.PS_PARTKEY=P.P_PARTKEY AND\n" +
//                            "P.P_BRAND='Brand#13')\n" +
//                    "Limit 200;");
        } else {
            txt_sql.setText("select * from views");
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
        ConnectDBControl connectDBControl = (ConnectDBControl) StageManager.CONTROLLER.get("ConnectDBControl");
        try {
            String dbms = l_dbms.getText();
            if (cb_calcite.isSelected()) {
                dbms += "_calcite";

                String ip = connectDBControl.getIp();
                String port = connectDBControl.getPort();
                String database = connectDBControl.getDatabase();
                String jdbcDriver = null;
                if (l_dbms.getText().equals("postgresql")) {
                    jdbcDriver = "org.postgresql.Driver";
                } else if (l_dbms.getText().equals("mysql")) {
                    jdbcDriver = "com.mysql.cj.jdbc.Driver";
                }
                String jdbcSchema = null;
                if (connectDBControl.getSchema().equals("")) {
                    jdbcSchema = connectDBControl.getDatabase();
                } else {
                    jdbcSchema = connectDBControl.getSchema();
                }
                String jdbcUrl = String.format("jdbc:%s://%s:%s/%s",
                        l_dbms.getText(), ip, port, database);
                String jdbcUser = connectDBControl.getUser();
                String jdbcPassword = connectDBControl.getPassword();

                Properties config = new Properties();
                config.put("model",
                        "inline:" +
                        "{\n" +
                        "  version: '1.0',\n" +
                        "  defaultSchema: '" + jdbcSchema + "',\n" +
                        "  schemas: [\n" +
                        "    {\n" +
                        "      name: '" + jdbcSchema + "',\n" +
                        "      type: 'jdbc',\n" +
                        "      jdbcDriver: '" + jdbcDriver + "',\n" +
                        "      jdbcUrl: '" + jdbcUrl + "',\n" +
                        "      jdbcSchema: '" + jdbcSchema + "',\n" +
                        "      jdbcUser: '" + jdbcUser + "',\n" +
                        "      jdbcPassword: '" + jdbcPassword + "'\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}" );
                config.put("lex", "MYSQL");
                // 获取执行 SQL 对象
                conn = DriverManager.getConnection("jdbc:calcite:", config);

            } else {
                // 获取执行 SQL 对象
                conn = connectDBControl.getConn();
            }

            // 获取执行计划可视化数据生成器对象
            VisualPlanTreeGenerator visualPlanTreeGenerator = VisualPlanTreeGeneratorFactory.create(dbms);
            // 获取执行计划可视化数据
            VisualPlanNode root = visualPlanTreeGenerator.getVisualPlanTree(conn, txt_sql.getText());
            System.out.println("levelOrder: ");
            PrintHandler printHandler = new PrintHandler();

            if (root instanceof PostgreSQLVisualPlanNode) {
                l_planningTime.setText(root.getFieldByKey("planning_time"));
                l_planningTime.setTextFill(Paint.valueOf("#FF0000"));
                l_executionTime.setText(root.getFieldByKey("execution_time"));
                l_executionTime.setTextFill(Paint.valueOf("FF0000"));
            }

            printHandler.draw(root);
            scrollPane.setContent(printHandler.getRoot());
        } catch (SQLException e) {
            // 弹出错误提示框
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("错误提示");
            alert.setHeaderText("查询失败!");
            alert.setContentText("请检查SQL语句，重新查询");

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

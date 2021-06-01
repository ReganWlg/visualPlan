package cn.edu.neu.VisualPlan.Graphics.Control;

import cn.edu.neu.VisualPlan.Graphics.Util.DialogBuilder;
import cn.edu.neu.VisualPlan.Graphics.Util.PrintHandler;
import cn.edu.neu.VisualPlan.Graphics.Util.StageManager;
import cn.edu.neu.VisualPlan.PostgreSQL.PostgreSQLVisualPlanNode;
import cn.edu.neu.VisualPlan.VisualPlanNode;
import cn.edu.neu.VisualPlan.VisualPlanTreeGenerator;
import cn.edu.neu.VisualPlan.VisualPlanTreeGeneratorFactory;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXToggleButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
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
    private JFXButton btn_disconnectDBMS;
    @FXML
    private JFXButton btn_connectNewDBMS;
    @FXML
    private JFXTextArea txt_sql;
    @FXML
    private JFXRadioButton btn_mode0;
    @FXML
    private JFXRadioButton btn_mode1;
    @FXML
    private JFXToggleButton btn_calcite;
    @FXML
    public JFXButton btn_query;
    @FXML
    private Label l_planningTime_title;
    @FXML
    private Label l_planningTime;
    @FXML
    private Label l_executionTime_title;
    @FXML
    private Label l_executionTime;
    @FXML
    private Text txt_detail;

    private Connection conn;
    private VisualPlanNode root;
    private PrintHandler printHandler;
    private boolean haveQuery;
    private int displayMode;
    private int index;

    public void setTxtDetail(String txtDetail) {
        txt_detail.setText(txtDetail);
    }

    // MainStage初始化
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        index = StageManager.CURRENT_INDEX;
        String controlKey = "ConnectDBControl_" + index;
        ConnectDBControl connectDBControl = (ConnectDBControl) StageManager.CONTROLLER.get(controlKey);
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

//        txt_sql.setText("Select P.P_NAME, P.P_mfgr,\n" +
//                "P.P_RETAILPRICE, PS.PS_SUPPLYCOST\n" +
//                "From part P, partsupp PS\n" +
//                "Where P.P_RETAILPRICE>PS.PS_SUPPLYCOST\n" +
//                "Limit 200 ");
        txt_sql.setText("SELECT C_CUSTKEY, C_NAME\n" +
                "FROM CUSTOMER C\n" +
                "WHERE NOT EXISTS (\n" +
                    "SELECT O.O_CUSTKEY\n" +
                    "FROM Orders O, Lineitem L, PartSupp PS, Part P\n" +
                    "WHERE C.C_CUSTKEY=O.O_CUSTKEY AND\n" +
                        "O.O_ORDERKEY=L.L_ORDERKEY AND\n" +
                        "L.L_PARTKEY=PS.PS_PARTKEY AND\n" +
                        "L.L_SUPPKEY= PS.PS_SUPPKEY AND\n" +
                        "PS.PS_PARTKEY=P.P_PARTKEY AND\n" +
                        "P.P_BRAND='Brand#13')\n" +
                "Limit 200");

        haveQuery = false;
        displayMode = 0;
        //StageManager.CONTROLLER.put("MainStageControl", this);
    }

    // 断开当前数据库连接
    public void disconnect(ActionEvent event) {
        new DialogBuilder(btn_disconnectDBMS)
                .setTitle("是否确认断开当前数据库？")
                .setMessage("确认断开将返回连接数据库界面")
                .setNegativeBtn("取消")
                .setPositiveBtn("确定", () -> {
                    String stageKey = "MainStage_" + index;
                    String controlKey = "MainStageControl_" + index;
                    // 关闭当前主界面
                    StageManager.STAGE.get(stageKey).close();
                    // 删除map中主界面的引用
                    StageManager.STAGE.remove(stageKey);
                    StageManager.CONTROLLER.remove(controlKey);
                    // 释放资源
                    if (conn != null) {
                        try {
                            conn.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    stageKey = "ConnectDB_" + index;
                    // 打开连接数据库界面
                    Stage stage = StageManager.STAGE.get(stageKey);
                    stage.show();
                })
                .create();
    }

    // 连接新数据库
    public void connectNew(ActionEvent event) {
        new DialogBuilder(btn_connectNewDBMS)
                .setTitle("是否连接新数据库？")
                .setMessage("确认将打开一个新的连接数据库界面")
                .setNegativeBtn("取消")
                .setPositiveBtn("确定", () -> {
                    // 新建stage，加载MainStage窗口
                    Stage stage = new Stage();
                    StageManager.TOTAL_INDEX++;
                    Parent root = FXMLLoader.load(getClass().getResource("/ConnectDB.fxml"));
                    String title = "连接数据库_" + StageManager.TOTAL_INDEX;
                    stage.setTitle(title);
                    stage.setResizable(false);
                    stage.setScene(new Scene(root));
                    stage.show();
                    String key = "ConnectDB_" + StageManager.TOTAL_INDEX;
                    StageManager.STAGE.put(key, stage);
                })
                .create();
    }

    // 选中缩略模式
    public void changeToMode0(ActionEvent event) {
        if (haveQuery && displayMode == 1) {
            displayMode = 0;
            printHandler = new PrintHandler(index);
            printHandler.draw(root, displayMode);
            scrollPane.setContent(printHandler.getRoot());
        }
    }

    // 选中详细模式
    public void changeToMode1(ActionEvent event) {
        if (haveQuery && displayMode == 0) {
            displayMode = 1;
            printHandler = new PrintHandler(index);
            printHandler.draw(root, displayMode);
            scrollPane.setContent(printHandler.getRoot());
        }
    }

    // 查询当前SQL的执行计划
    public void queryExecutionPlan(ActionEvent event) {
        String controlKey = "ConnectDBControl_" + index;
        ConnectDBControl connectDBControl = (ConnectDBControl) StageManager.CONTROLLER.get(controlKey);
        // 当前的模式
        if (btn_mode0.isSelected()) {
            displayMode = 0;
        } else if (btn_mode1.isSelected()) {
            displayMode = 1;
        }

        String dbms = l_dbms.getText();
        // 使用Calcite进行优化
        if (btn_calcite.isSelected()) {
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

            // 配置Calcite连接信息
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
            try {
                conn = DriverManager.getConnection("jdbc:calcite:", config);
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        // 不使用calcite进行优化
        else {
            // 获取执行 SQL 对象
            conn = connectDBControl.getConn();
        }

        try {
            controlKey = "MainStageControl_" + index;
            StageManager.CONTROLLER.put(controlKey, this);

            // 获取执行计划可视化数据生成器对象
            VisualPlanTreeGenerator visualPlanTreeGenerator = VisualPlanTreeGeneratorFactory.create(dbms);
            // 获取执行计划可视化数据
            root = visualPlanTreeGenerator.getVisualPlanTree(conn, txt_sql.getText(), index);
            System.out.println("levelOrder: ");
            printHandler = new PrintHandler(index);

            if (root instanceof PostgreSQLVisualPlanNode) {
                l_planningTime_title.setText("Planning Time: ");
                l_planningTime.setText(root.getFieldByKey("planning_time"));
                l_planningTime.setTextFill(Paint.valueOf("#FF0000"));
                l_executionTime_title.setText("Execution Time: ");
                l_executionTime.setText(root.getFieldByKey("execution_time"));
                l_executionTime.setTextFill(Paint.valueOf("FF0000"));
            } else {
                l_planningTime_title.setText("");
                l_planningTime.setText("");
                l_executionTime_title.setText("");
                l_executionTime.setText("");
            }

            printHandler.draw(root, displayMode);
            scrollPane.setContent(printHandler.getRoot());
            txt_detail.setText("节点详细信息：");
            haveQuery = true;
        } catch (SQLException e) {
            // 弹出错误提示框
            new DialogBuilder(btn_query)
                    .setTitle("查询失败!")
                    .setMessage("请检查SQL语句，重新查询")
                    .setPositiveBtn("确定")
                    .create();
        }
    }
}

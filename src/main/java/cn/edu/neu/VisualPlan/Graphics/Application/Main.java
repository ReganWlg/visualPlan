package cn.edu.neu.VisualPlan.Graphics.Application;

import cn.edu.neu.VisualPlan.Graphics.Util.StageManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ConnectDB.fxml"));
            primaryStage.setTitle("连接数据库");
            primaryStage.setScene(new Scene(root));
            primaryStage.setResizable(false);
            primaryStage.show();
            //将“连接数据库“窗口保存到map中
            StageManager.STAGE.put("ConnectDB", primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

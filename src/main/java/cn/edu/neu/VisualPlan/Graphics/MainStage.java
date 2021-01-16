package cn.edu.neu.VisualPlan.Graphics;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MainStage extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        AnchorPane root = PrintHandler.getRoot();
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(root);
        // 滚动条显示策略
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        scrollPane.setFitToWidth(true);

        //滚动监听
        scrollPane.hvalueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            }
        });

        scrollPane.vvalueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            }
        });

        Scene scene = new Scene(scrollPane, 800, 600);
        primaryStage.setTitle("VisualPlan");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

package cn.edu.neu.VisualPlan.Graphics.Util;

import cn.edu.neu.VisualPlan.VisualPlanNode;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class RectangleField extends StackPane {

    // 记录当前结点
    private final VisualPlanNode _node;

    private final double RECTANGLE_WIDTH = 300;
    private final double RECTANGLE_HEIGHT = 160;
    private final double RECTANGLE_ARC_WIDTH = 20;
    private final double RECTANGLE_ARC_HEIGHT = 20;
    private final double TEXT_WIDTH = 280;

    public RectangleField(VisualPlanNode node) {

        _node = node;

        Rectangle rectangle = new Rectangle();
        rectangle.setWidth(RECTANGLE_WIDTH);
        rectangle.setHeight(RECTANGLE_HEIGHT);
        rectangle.setArcWidth(RECTANGLE_ARC_WIDTH);
        rectangle.setArcHeight(RECTANGLE_ARC_HEIGHT);
        rectangle.setFill(Paint.valueOf("#F0F8FF"));
        rectangle.setStroke(Color.BLACK);

        GridPane gridPane = new GridPane();
        gridPane.setPrefWidth(RECTANGLE_WIDTH);
        gridPane.setHgap(10);
        gridPane.setVgap(5);
        gridPane.setAlignment(Pos.CENTER);

        Label type = new Label(node.getFieldByKey("type"));
        type.setTextFill(Paint.valueOf("#FF0000"));
        type.setStyle("-fx-font-weight: bold;");
        gridPane.add(type, 0, 0);
        GridPane.setColumnSpan(type, 3);

        Text description = new Text(
                String.format("Description: %s", node.getFieldByKey("description")));
        description.setWrappingWidth(TEXT_WIDTH);
        gridPane.add(description, 0, 1);
        GridPane.setColumnSpan(description, 3);
        GridPane.setMargin(description, new Insets(5, 0, 0, 0));

        Label estimate = new Label("Estimate");
        estimate.setStyle("-fx-font-weight: bold;" +
                "-fx-background-fill: black, white;" +
                "-fx-background-insets: 0, 1;");
        gridPane.add(estimate, 0, 2);
        GridPane.setMargin(estimate, new Insets(5, 0, 0, 0));

        Label estimateCost = new Label(String.format("cost=%s", node.getFieldByKey("cost")));
        gridPane.add(estimateCost, 0, 3);
        GridPane.setMargin(estimateCost, new Insets(0, 10, 0, 0));

        Label estimateRows = new Label(String.format("rows=%s", node.getFieldByKey("output_rows")));
        gridPane.add(estimateRows, 0, 4);
        GridPane.setMargin(estimateRows, new Insets(0, 10, 0, 0));

        Label actualTime = new Label("Actual");
        actualTime.setStyle("-fx-font-weight: bold;");
        gridPane.add(actualTime, 1, 2);
        GridPane.setColumnSpan(actualTime, 2);
        GridPane.setHalignment(actualTime, HPos.CENTER);
        GridPane.setMargin(actualTime, new Insets(5, 0, 0, 0));

        Label actualTimeEachRow = new Label(
                String.format("each row=%sms", node.getFieldByKey("Timing_first_row_ms")));
        gridPane.add(actualTimeEachRow, 1, 3);

        Label actualTimeAllRows = new Label(
                String.format("all rows=%sms", node.getFieldByKey("Timing_last_row_ms")));
        gridPane.add(actualTimeAllRows, 1, 4);

        Label actualNumberRowsRead = new Label(
                String.format("rows=%s", node.getFieldByKey("Timing_rows")));
        gridPane.add(actualNumberRowsRead, 2, 3);

        Label actualNumberCycles = new Label(
                String.format("loops=%s", node.getFieldByKey("Timing_loops")));
        gridPane.add(actualNumberCycles, 2, 4);

        this.getChildren().addAll(rectangle, gridPane);
    }

    public VisualPlanNode getNode() {
        return _node;
    }
}

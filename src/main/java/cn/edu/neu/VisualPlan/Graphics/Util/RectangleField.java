package cn.edu.neu.VisualPlan.Graphics.Util;

import cn.edu.neu.VisualPlan.VisualPlanNode;
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
    private VisualPlanNode _node = null;

    // 272:170=16:10，感觉这个长宽比不错，
    // 且之前尺寸300*150=45000，现在尺寸272*170=46240，面积相近，不大不小刚刚好
    private final double RECTANGLE_WIDTH = 272;
    private final double RECTANGLE_HEIGHT = 170;
    private final double RECTANGLE_ARC_WIDTH = 20;
    private final double RECTANGLE_ARC_HEIGHT = 20;
    private final double TEXT_WIDTH = 222;

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
        gridPane.setHgap(30);
        gridPane.setVgap(5);
        gridPane.setAlignment(Pos.CENTER);

        Label type = new Label(node.getFieldByKey("type"));
        type.setTextFill(Paint.valueOf("#FF0000"));
        gridPane.add(type, 0, 0);
        GridPane.setColumnSpan(type, 2);

        Text description = new Text(
                String.format("Description: %s", node.getFieldByKey("description")));
        description.setWrappingWidth(TEXT_WIDTH);
        gridPane.add(description, 0, 1);
        GridPane.setColumnSpan(description, 2);

        Label actualTime = new Label("Actual Time:");
        gridPane.add(actualTime, 0, 2);

        Label actualNumber = new Label("Actual Number:");
        gridPane.add(actualNumber, 1, 2);

        Label actualTimeFirstRow = new Label(
                String.format("get first row: %sms", node.getFieldByKey("Timing_first_row_ms")));
        gridPane.add(actualTimeFirstRow, 0, 3);

        Label actualNumberRowsRead = new Label(
                String.format("rows read: %s", node.getFieldByKey("Timing_rows")));
        gridPane.add(actualNumberRowsRead, 1, 3);

        Label actualTimeAllRows = new Label(
                String.format("get all rows: %sms", node.getFieldByKey("Timing_last_row_ms")));
        gridPane.add(actualTimeAllRows, 0, 4);

        Label actualNumberCycles = new Label(
                String.format("cycles: %s", node.getFieldByKey("Timing_loops")));
        gridPane.add(actualNumberCycles, 1, 4);

        this.getChildren().addAll(rectangle, gridPane);
    }

    public VisualPlanNode getNode() {
        return _node;
    }
}

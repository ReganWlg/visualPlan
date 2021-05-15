package cn.edu.neu.VisualPlan.Graphics.Util;

import cn.edu.neu.VisualPlan.Calcite.CalciteVisualPlanNode;
import cn.edu.neu.VisualPlan.MySQL.MySQLVisualPlanNode;
import cn.edu.neu.VisualPlan.PostgreSQL.PostgreSQLVisualPlanNode;
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

    public RectangleField(VisualPlanNode node, int mode) {

        _node = node;

        double RECTANGLE_WIDTH;
        double RECTANGLE_HEIGHT;
        double RECTANGLE_ARC_WIDTH;
        double RECTANGLE_ARC_HEIGHT;
        double TEXT_WIDTH;

        Rectangle rectangle = new Rectangle();
        GridPane gridPane = new GridPane();

        // 默认缩略模式，只展示结点类型
        if (mode == 0) {
            RECTANGLE_WIDTH = 150;
            RECTANGLE_HEIGHT = 50;
            RECTANGLE_ARC_WIDTH = 20;
            RECTANGLE_ARC_HEIGHT = 20;

            rectangle.setWidth(RECTANGLE_WIDTH);
            rectangle.setHeight(RECTANGLE_HEIGHT);
            rectangle.setArcWidth(RECTANGLE_ARC_WIDTH);
            rectangle.setArcHeight(RECTANGLE_ARC_HEIGHT);
            rectangle.setFill(Paint.valueOf("#F0F8FF"));
            rectangle.setStroke(Color.BLACK);

            gridPane.setPrefWidth(RECTANGLE_WIDTH);
            gridPane.setAlignment(Pos.CENTER);

            Label type = new Label(node.getFieldByKey("type"));
            type.setTextFill(Paint.valueOf("#FF0000"));
            type.setStyle("-fx-font-weight: bold;");
            gridPane.add(type, 0, 0);
        }
        // 详细模式，展示全部信息
        else if (mode == 1) {

            if (node instanceof MySQLVisualPlanNode) {
                RECTANGLE_WIDTH = 300;
                RECTANGLE_HEIGHT = 160;
                RECTANGLE_ARC_WIDTH = 20;
                RECTANGLE_ARC_HEIGHT = 20;
                TEXT_WIDTH = 280;

                rectangle.setWidth(RECTANGLE_WIDTH);
                rectangle.setHeight(RECTANGLE_HEIGHT);
                rectangle.setArcWidth(RECTANGLE_ARC_WIDTH);
                rectangle.setArcHeight(RECTANGLE_ARC_HEIGHT);
                rectangle.setFill(Paint.valueOf("#F0F8FF"));
                rectangle.setStroke(Color.BLACK);

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
                estimate.setStyle("-fx-font-weight: bold;");
                gridPane.add(estimate, 0, 2);
                GridPane.setMargin(estimate, new Insets(5, 0, 0, 0));

                Label estimateCost = new Label(String.format("cost=%s", node.getFieldByKey("cost")));
                gridPane.add(estimateCost, 0, 3);
                GridPane.setMargin(estimateCost, new Insets(0, 10, 0, 0));

                Label estimateRows = new Label(String.format("rows=%s", node.getFieldByKey("output_rows")));
                gridPane.add(estimateRows, 0, 4);
                GridPane.setMargin(estimateRows, new Insets(0, 10, 0, 0));

                Label actual = new Label("Actual");
                actual.setStyle("-fx-font-weight: bold;");
                gridPane.add(actual, 1, 2);
                GridPane.setColumnSpan(actual, 2);
                GridPane.setHalignment(actual, HPos.CENTER);
                GridPane.setMargin(actual, new Insets(5, 0, 0, 0));

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
            } else if (node instanceof PostgreSQLVisualPlanNode) {
                RECTANGLE_WIDTH = 350;
                RECTANGLE_HEIGHT = 160;
                RECTANGLE_ARC_WIDTH = 20;
                RECTANGLE_ARC_HEIGHT = 20;
                TEXT_WIDTH = 330;

                rectangle.setWidth(RECTANGLE_WIDTH);
                rectangle.setHeight(RECTANGLE_HEIGHT);
                rectangle.setArcWidth(RECTANGLE_ARC_WIDTH);
                rectangle.setArcHeight(RECTANGLE_ARC_HEIGHT);
                rectangle.setFill(Paint.valueOf("#F0F8FF"));
                rectangle.setStroke(Color.BLACK);

                gridPane.setPrefWidth(RECTANGLE_WIDTH);
                gridPane.setHgap(10);
                gridPane.setVgap(5);
                gridPane.setAlignment(Pos.CENTER_LEFT);

                Label type = new Label(node.getFieldByKey("type"));
                type.setTextFill(Paint.valueOf("#FF0000"));
                type.setStyle("-fx-font-weight: bold;");
                gridPane.add(type, 0, 0);
                GridPane.setMargin(type, new Insets(0, 0, 0, 10));
                GridPane.setColumnSpan(type, 4);

                Text description = new Text(
                        String.format("Description: %s", node.getFieldByKey("description")));
                description.setWrappingWidth(TEXT_WIDTH);
                gridPane.add(description, 0, 1);
                GridPane.setColumnSpan(description, 4);
                GridPane.setMargin(description, new Insets(5, 0, 0, 10));

                Label estimate = new Label("Estimate");
                estimate.setStyle("-fx-font-weight: bold;");
                gridPane.add(estimate, 0, 2);
                GridPane.setColumnSpan(estimate, 2);
                GridPane.setHalignment(estimate, HPos.CENTER);
                GridPane.setMargin(estimate, new Insets(5, 0, 0, 0));

                Label estimateCost = new Label(
                        String.format("cost=%s .. %s",
                                node.getFieldByKey("estimate_cost_each_row_ms"),
                                node.getFieldByKey("estimate_cost_all_rows_ms")));
                gridPane.add(estimateCost, 0, 3);
                GridPane.setMargin(estimateCost, new Insets(0, 10, 0, 10));
                GridPane.setColumnSpan(estimateCost, 2);

                Label estimateRows = new Label(
                        String.format("rows=%s", node.getFieldByKey("estimate_rows")));
                gridPane.add(estimateRows, 0, 4);
                GridPane.setMargin(estimateRows, new Insets(0, 0, 0, 10));

                Label estimateWidth = new Label(
                        String.format("width=%s", node.getFieldByKey("estimate_width")));
                gridPane.add(estimateWidth, 1, 4);
                GridPane.setMargin(estimateWidth, new Insets(0, 10, 0, 0));

                Label actual = new Label("Actual");
                actual.setStyle("-fx-font-weight: bold;");
                gridPane.add(actual, 2, 2);
                GridPane.setColumnSpan(actual, 2);
                GridPane.setHalignment(actual, HPos.CENTER);
                GridPane.setMargin(actual, new Insets(5, 0, 0, 0));

                Label actualTime = new Label(
                        String.format("time=%s .. %s ms",
                                node.getFieldByKey("actual_time_each_row_ms"),
                                node.getFieldByKey("actual_time_all_rows_ms")));
                gridPane.add(actualTime, 2, 3);
                GridPane.setColumnSpan(actualTime, 2);

                Label actualNumberRowsRead = new Label(
                        String.format("rows=%s", node.getFieldByKey("actual_rows")));
                gridPane.add(actualNumberRowsRead, 2, 4);

                Label actualNumberCycles = new Label(
                        String.format("loops=%s", node.getFieldByKey("actual_loops")));
                gridPane.add(actualNumberCycles, 3, 4);
            } else if (node instanceof CalciteVisualPlanNode) {
                RECTANGLE_WIDTH = 300;
                RECTANGLE_HEIGHT = 160;
                RECTANGLE_ARC_WIDTH = 20;
                RECTANGLE_ARC_HEIGHT = 20;
                TEXT_WIDTH = 280;

                rectangle.setWidth(RECTANGLE_WIDTH);
                rectangle.setHeight(RECTANGLE_HEIGHT);
                rectangle.setArcWidth(RECTANGLE_ARC_WIDTH);
                rectangle.setArcHeight(RECTANGLE_ARC_HEIGHT);
                rectangle.setFill(Paint.valueOf("#F0F8FF"));
                rectangle.setStroke(Color.BLACK);

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

                Label rowCount = new Label(String.format("RowCount: %s", node.getFieldByKey("rowCount")));
                gridPane.add(rowCount, 0, 2);
                GridPane.setColumnSpan(rowCount, 3);
                GridPane.setMargin(rowCount, new Insets(5, 0, 0, 0));

                Label cumulativeCost = new Label("Cumulative Cost");
                cumulativeCost.setStyle("-fx-font-weight: bold;");
                gridPane.add(cumulativeCost, 0, 3);
                GridPane.setRowSpan(cumulativeCost, 3);

                Label rows = new Label("rows:");
                gridPane.add(rows, 1, 3);
                GridPane.setHalignment(rows, HPos.RIGHT);

                Label rowsValue = new Label(node.getFieldByKey("rows"));
                gridPane.add(rowsValue, 2, 3);

                Label cpu = new Label("cpu:");
                gridPane.add(cpu, 1, 4);
                GridPane.setHalignment(cpu, HPos.RIGHT);

                Label cpuValue = new Label(node.getFieldByKey("cpu"));
                gridPane.add(cpuValue, 2, 4);

                Label io = new Label("io:");
                gridPane.add(io, 1, 5);
                GridPane.setHalignment(io, HPos.RIGHT);

                Label ioValue = new Label(node.getFieldByKey("io"));
                gridPane.add(ioValue, 2, 5);

            }
        }

        this.getChildren().addAll(rectangle, gridPane);
    }

    public VisualPlanNode getNode() {
        return _node;
    }
}

package cn.edu.neu.VisualPlan.Graphics;

import cn.edu.neu.VisualPlan.VisualPlanNode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class RectangleField extends StackPane {

    // 记录当前结点
    private VisualPlanNode _node = null;

    private final double RECTANGLE_WIDTH = 400;
    private final double RECTANGLE_HEIGHT = 180;
    private final double RECTANGLE_ARC_WIDTH = 20;
    private final double RECTANGLE_ARC_HEIGHT = 20;
    private final double TEXT_WIDTH = 350;
    private final double TEXT_LAYOUTX = 25;

    public RectangleField(VisualPlanNode node) {

        _node = node;

        Rectangle rectangle = new Rectangle();
        rectangle.setWidth(RECTANGLE_WIDTH);
        rectangle.setHeight(RECTANGLE_HEIGHT);
        rectangle.setArcWidth(RECTANGLE_ARC_WIDTH);
        rectangle.setArcHeight(RECTANGLE_ARC_HEIGHT);
        rectangle.setFill(Paint.valueOf("#F0F8FF"));
        rectangle.setStroke(Color.BLACK);

        Text text = new Text(node.toString());
        text.setWrappingWidth(TEXT_WIDTH);
        text.setLayoutX(TEXT_LAYOUTX);

        this.getChildren().addAll(rectangle, text);
    }

    public VisualPlanNode getNode() {
        return _node;
    }
}

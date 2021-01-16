package cn.edu.neu.VisualPlan.Graphics;

import cn.edu.neu.VisualPlan.VisualPlanHandler;
import cn.edu.neu.VisualPlan.VisualPlanNode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;


public class PrintHandler implements VisualPlanHandler {

    private static AnchorPane _root = new AnchorPane();

    private int currentLevel = 0;

    // 添加到_root中的rectangleField数量
    private int childrenNum = 0;

    private final double RECTANGLE_PADDING = 20;
    private final double LEVEL_PADDING = 40;
    private final double RECTANGLE_WIDTH = 400;
    private final double RECTANGLE_HEIGHT = 180;

    public static AnchorPane getRoot() {
        return _root;
    }

    public void onCall(VisualPlanNode node) {
        System.out.println(node.toString());

        // 添加第一个结点
        if (node.getLevel() == 0) {
            RectangleField rectangleField = new RectangleField(node);
            rectangleField.setLayoutX(20);
            rectangleField.setLayoutY(20);
            _root.getChildren().add(rectangleField);
            childrenNum++;
            return;
        }

        // 在当前层添加结点
        if (node.getLevel() == currentLevel) {
            RectangleField rectangleField = new RectangleField(node);
            VisualPlanNode lastNode = ((RectangleField) _root.getChildren().get(childrenNum - 1)).getNode();

            // 若即将加入的结点与上一个结点的parent是同一个
            if (lastNode.getParentNode() == node.getParentNode()) {
                double layoutX = _root.getChildren().get(childrenNum - 1).getLayoutX() + RECTANGLE_WIDTH + RECTANGLE_PADDING;
                double layoutY = _root.getChildren().get(childrenNum - 1).getLayoutY();

                rectangleField.setLayoutX(layoutX);
                rectangleField.setLayoutY(layoutY);
                _root.getChildren().add(rectangleField);
                childrenNum++;
                // 调整其parent及相关结点位置，向右移动(400 + 20/2 - 200)单位
                adjustParentLocation1(node.getParentNode(), currentLevel - 1);
            }
            // 若即将加入的结点与上一个结点的parent不同
            else {
                int parentLevelIndex = node.getParentNode().getLevelIndex();
                double parentLayoutX = _root.getChildren().get(parentLevelIndex).getLayoutX();
                double parentLayoutY = _root.getChildren().get(parentLevelIndex).getLayoutY();
                double lastLayoutX = _root.getChildren().get(childrenNum - 1).getLayoutX();

                if (lastLayoutX + RECTANGLE_WIDTH > parentLayoutX) {
                    rectangleField.setLayoutX(lastLayoutX + RECTANGLE_WIDTH + RECTANGLE_PADDING);
                    rectangleField.setLayoutY(parentLayoutY + RECTANGLE_HEIGHT + LEVEL_PADDING);
                    _root.getChildren().add(rectangleField);
                    childrenNum++;
                    // 需要调节parent及相关结点位置
                    adjustParentLocation2(node.getParentNode(), currentLevel - 1);
                } else {
                    rectangleField.setLayoutX(parentLayoutX);
                    rectangleField.setLayoutY(parentLayoutY + RECTANGLE_HEIGHT + LEVEL_PADDING);
                    _root.getChildren().add(rectangleField);
                    childrenNum++;
                }
            }
        }
        // 遍历到下一层了，在新层添加第一个结点
        else {
            currentLevel++;
            RectangleField rectangleField = new RectangleField(node);
            int parentLevelIndex = node.getParentNode().getLevelIndex();
            double parentLayoutX = _root.getChildren().get(parentLevelIndex).getLayoutX();
            double parentLayoutY = _root.getChildren().get(parentLevelIndex).getLayoutY();

            rectangleField.setLayoutX(parentLayoutX);
            rectangleField.setLayoutY(parentLayoutY + RECTANGLE_HEIGHT + LEVEL_PADDING);
            _root.getChildren().add(rectangleField);
            childrenNum++;
        }
    }

    // 第一种调整方式
    private void adjustParentLocation1(VisualPlanNode node, int level) {
        int currentNodeLevel = node.getLevel();
        int currentNodeLevelIndex = node.getLevelIndex();

        // 将parent所在level的右侧Node的位置进行调整
        while (level == currentNodeLevel) {
            double preLayoutX = _root.getChildren().get(currentNodeLevelIndex).getLayoutX();
            _root.getChildren().get(currentNodeLevelIndex).setLayoutX(preLayoutX + (RECTANGLE_WIDTH + RECTANGLE_PADDING) / 2);
            currentNodeLevelIndex++;
            VisualPlanNode nextNode = ((RectangleField) _root.getChildren().get(currentNodeLevelIndex)).getNode();
            currentNodeLevel = nextNode.getLevel();
        }

        if (level != 0) {
            adjustParentLocation1(node.getParentNode(), level - 1);
        }
    }

    // 第二种调整方式
    private void adjustParentLocation2(VisualPlanNode node, int level) {
        int currentNodeLevel = node.getLevel();
        int currentNodeLevelIndex = node.getLevelIndex();

        // 两个孩子，根据两个孩子确定新位置及偏移量
        if (node.getSubNodeList().size() == 2) {
            VisualPlanNode leftChild = node.getSubNodeList().get(0);
            VisualPlanNode rightChild = node.getSubNodeList().get(1);
            double leftChildLayoutX = _root.getChildren().get(leftChild.getLevelIndex()).getLayoutX();
            double rightChildLayoutX = _root.getChildren().get(rightChild.getLevelIndex()).getLayoutX();
            double preLayoutX = _root.getChildren().get(currentNodeLevelIndex).getLayoutX();
            double offset = (leftChildLayoutX + rightChildLayoutX) / 2 - preLayoutX;

            _root.getChildren().get(currentNodeLevelIndex).setLayoutX(offset + preLayoutX);
            currentNodeLevelIndex++;
            VisualPlanNode nextNode = ((RectangleField) _root.getChildren().get(currentNodeLevelIndex)).getNode();
            currentNodeLevel = nextNode.getLevel();

            // 将parent所在level的右侧Node的位置进行调整，向右偏移offset距离
            while (level == currentNodeLevel) {
                preLayoutX = _root.getChildren().get(currentNodeLevelIndex).getLayoutX();
                _root.getChildren().get(currentNodeLevelIndex).setLayoutX(preLayoutX + offset);
                currentNodeLevelIndex++;
                nextNode = ((RectangleField) _root.getChildren().get(currentNodeLevelIndex)).getNode();
                currentNodeLevel = nextNode.getLevel();
            }
        }
        // 一个孩子，直接根据孩子位置定新位置及偏移量
        else {
            VisualPlanNode child = node.getSubNodeList().get(0);
            double childLayoutX = _root.getChildren().get(child.getLevelIndex()).getLayoutX();
            double preLayoutX = _root.getChildren().get(currentNodeLevelIndex).getLayoutX();
            double offset = childLayoutX - preLayoutX;

            // 将parent所在level的右侧Node加入queue
            while (level == currentNodeLevel) {
                preLayoutX = _root.getChildren().get(currentNodeLevelIndex).getLayoutX();
                _root.getChildren().get(currentNodeLevelIndex).setLayoutX(preLayoutX + offset);
                currentNodeLevelIndex++;
                VisualPlanNode nextNode = ((RectangleField) _root.getChildren().get(currentNodeLevelIndex)).getNode();
                currentNodeLevel = nextNode.getLevel();
            }
        }

        if (level != 0) {
            adjustParentLocation2(node.getParentNode(), level - 1);
        }
    }

    public void drawLine() {
        for (int i = 0; i < childrenNum; i++) {
            RectangleField parent = (RectangleField) _root.getChildren().get(i);
            double parentX = parent.getLayoutX() + RECTANGLE_WIDTH / 2;
            double parentY = parent.getLayoutY() + RECTANGLE_HEIGHT;
            for (VisualPlanNode subNode : parent.getNode().getSubNodeList()) {
                int levelIndex = subNode.getLevelIndex();
                RectangleField child = (RectangleField) _root.getChildren().get(levelIndex);
                double childX = child.getLayoutX() + RECTANGLE_WIDTH / 2;
                double childY = child.getLayoutY();
                Line line = new Line(parentX, parentY, childX, childY);
                _root.getChildren().add(line);
            }
        }
    }
}

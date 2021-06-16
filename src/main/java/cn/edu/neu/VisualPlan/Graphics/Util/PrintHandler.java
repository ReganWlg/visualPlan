package cn.edu.neu.VisualPlan.Graphics.Util;

import cn.edu.neu.VisualPlan.Calcite.CalciteVisualPlanNode;
import cn.edu.neu.VisualPlan.Graphics.Control.MainStageControl;
import cn.edu.neu.VisualPlan.MySQL.MySQLVisualPlanNode;
import cn.edu.neu.VisualPlan.PostgreSQL.PostgreSQLVisualPlanNode;
import cn.edu.neu.VisualPlan.VisualPlanNode;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;

import java.util.LinkedList;
import java.util.Queue;


public class PrintHandler {

    private AnchorPane _root = new AnchorPane();

    private final int index;

    private int currentLevel = 0;

    // 添加到_root中的rectangleField数量
    private int childrenNum = 0;

    private final double RECTANGLE_PADDING = 20;
    private final double LEVEL_PADDING = 40;
    private double RECTANGLE_WIDTH;
    private double RECTANGLE_HEIGHT;

    public PrintHandler(int index) {
        this.index = index;
    }

    public AnchorPane getRoot() {
        return _root;
    }

    public final void draw(VisualPlanNode root, int mode) {
        if (mode == 0) {
            RECTANGLE_WIDTH = 150;
            RECTANGLE_HEIGHT = 50;
        }
        else if (mode == 1) {
            if (root instanceof MySQLVisualPlanNode) {
                RECTANGLE_WIDTH = 300;
                RECTANGLE_HEIGHT = 160;
            } else if (root instanceof PostgreSQLVisualPlanNode){
                RECTANGLE_WIDTH = 350;
                RECTANGLE_HEIGHT = 160;
            } else if (root instanceof CalciteVisualPlanNode) {
                RECTANGLE_WIDTH = 300;
                RECTANGLE_HEIGHT = 160;
            }
        }

        Queue<VisualPlanNode> queue = new LinkedList<>();
        int levelIndex = 0;
        root.setLevelIndex(levelIndex++);
        queue.add(root);
        while (!queue.isEmpty()) {
            VisualPlanNode node = queue.remove();
            drawNode(node, mode);
            for (VisualPlanNode subNode : node.getSubNodeList()) {
                subNode.setLevelIndex(levelIndex++);
                queue.add(subNode);
            }
        }
        drawLine();
    }

    private void drawNode(VisualPlanNode node, int mode) {
//        System.out.println(node.toString());
        RectangleField rectangleField = node.createRectangleField(mode);
        childrenNum++;

        rectangleField.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                String controlKey = "MainStageControl_" + index;
                MainStageControl mainStageControl = (MainStageControl) StageManager.CONTROLLER.get(controlKey);
                mainStageControl.setTxtDetail(node.toString());
            }
        });

        // 添加第一个结点
        if (node.getLevel() == 0) {
            rectangleField.setLayoutX(40);
            rectangleField.setLayoutY(40);
            _root.getChildren().add(rectangleField);
            return;
        }

        // 在当前层添加结点
        if (node.getLevel() == currentLevel) {
            // 找到上一个节点，即当前节点左边的节点
            RectangleField lastNode = (RectangleField) _root.getChildren().get(node.getLevelIndex() - 1);

            // 若即将加入的结点与上一个结点的parent是同一个
            if (lastNode.getNode().getParentNode() == node.getParentNode()) {
                // 先判断lastNode左侧空间是否被占用，若没被占用，只调整lastNode及当前node位置即可
                RectangleField lastNode2 = (RectangleField) _root.getChildren().get(node.getLevelIndex() - 2);

                // lastNode左侧有多余的空间，则调整lastNode并添加当前node，不需要调整parent位置
                if ((lastNode2.getNode().getLevel() == currentLevel
                && lastNode2.getLayoutX() + (RECTANGLE_WIDTH + RECTANGLE_PADDING) * 3 / 2 <= lastNode.getLayoutX())
                || (lastNode2.getNode().getLevel() < currentLevel
                && lastNode.getLayoutX() - (RECTANGLE_WIDTH + RECTANGLE_PADDING) / 2 >= 40)) {
                    // 调整lastNode位置，向左平移
                    lastNode.setLayoutX(lastNode.getLayoutX() - (RECTANGLE_WIDTH + RECTANGLE_PADDING) / 2);

                    // 为新node设置位置
                    double layoutX = lastNode.getLayoutX() + RECTANGLE_WIDTH + RECTANGLE_PADDING;
                    double layoutY = lastNode.getLayoutY();

                    rectangleField.setLayoutX(layoutX);
                    rectangleField.setLayoutY(layoutY);
                    _root.getChildren().add(rectangleField);
                }
                // lastNode左侧空间被占用，则在lastNode右侧位置直接添加结点，并调整parent位置
                else {
                    double layoutX = lastNode.getLayoutX() + RECTANGLE_WIDTH + RECTANGLE_PADDING;
                    double layoutY = lastNode.getLayoutY();

                    rectangleField.setLayoutX(layoutX);
                    rectangleField.setLayoutY(layoutY);
                    _root.getChildren().add(rectangleField);
                    // 调整其parent及相关结点位置，向右移动(400 + 20/2 - 200)单位，目前parent有多个子节点
                    adjustParentLocation(
                            node.getParentNode(),
                            currentLevel - 1,
                            node.getSubNodeIndex(),
                            true);

                }
            }
            // 若即将加入的结点与上一个结点的parent不同
            else {
                int parentLevelIndex = node.getParentNode().getLevelIndex();
                double parentLayoutX = _root.getChildren().get(parentLevelIndex).getLayoutX();
                double parentLayoutY = _root.getChildren().get(parentLevelIndex).getLayoutY();
                double lastLayoutX = _root.getChildren().get(node.getLevelIndex() - 1).getLayoutX();
                double lastLayoutY = _root.getChildren().get(node.getLevelIndex() - 1).getLayoutY();

                if (lastLayoutX + RECTANGLE_WIDTH > parentLayoutX) {
                    rectangleField.setLayoutX(lastLayoutX + RECTANGLE_WIDTH + RECTANGLE_PADDING);
                    rectangleField.setLayoutY(lastLayoutY);
                    _root.getChildren().add(rectangleField);
                    // 需要调节parent及相关结点位置，目前parent的子节点只有当前node一个
                    adjustParentLocation(
                            node.getParentNode(),
                            currentLevel - 1,
                            node.getSubNodeIndex(),
                            true);
                } else {
                    rectangleField.setLayoutX(parentLayoutX);
                    rectangleField.setLayoutY(lastLayoutY);
                    _root.getChildren().add(rectangleField);
                }
            }
        }
        // 遍历到下一层了，在新层添加第一个结点
        else {
            currentLevel++;
            int parentLevelIndex = node.getParentNode().getLevelIndex();
            double parentLayoutX = _root.getChildren().get(parentLevelIndex).getLayoutX();
            double parentLayoutY = _root.getChildren().get(parentLevelIndex).getLayoutY();

            // 新节点的水平位置与其父节点对齐
            rectangleField.setLayoutX(parentLayoutX);
            rectangleField.setLayoutY(parentLayoutY + RECTANGLE_HEIGHT + LEVEL_PADDING);
            _root.getChildren().add(rectangleField);
        }
    }

    // 调整parent结点的位置
    // 外部调用时subNodeIndex=node.getSubNodeIndex()，
    // 自身调用时subNodeIndex=node.getParentNode().getSubNodeList().size() - 1
    // 外部调用时flag=true，自身调用时flag=false
    private void adjustParentLocation(VisualPlanNode node, int level, int subNodeIndex, boolean flag) {
        int currentNodeLevel = node.getLevel();
        int currentNodeLevelIndex = node.getLevelIndex();

        // 根据孩子确定新位置及偏移量
        VisualPlanNode leftChild = node.getSubNodeList().get(0);
        VisualPlanNode rightChild = node.getSubNodeList().get(subNodeIndex);
        double leftChildLayoutX = _root.getChildren().get(leftChild.getLevelIndex()).getLayoutX();
        double rightChildLayoutX = _root.getChildren().get(rightChild.getLevelIndex()).getLayoutX();
        double preLayoutX = _root.getChildren().get(currentNodeLevelIndex).getLayoutX();
        double offset = (leftChildLayoutX + rightChildLayoutX) / 2 - preLayoutX;

        _root.getChildren().get(currentNodeLevelIndex).setLayoutX(offset + preLayoutX);
        currentNodeLevelIndex++;
        RectangleField nextNode = (RectangleField) _root.getChildren().get(currentNodeLevelIndex);
        currentNodeLevel = nextNode.getNode().getLevel();

        // 将parent所在level的右侧Node的位置进行调整，向右偏移offset距离
        while (level == currentNodeLevel) {
            if (flag) {
                preLayoutX = nextNode.getLayoutX();
            } else {
                if (nextNode.getNode().getSubNodeList().isEmpty()) {
                    preLayoutX = nextNode.getLayoutX();
                } else {
                    leftChild = nextNode.getNode().getSubNodeList().get(0);
                    rightChild = nextNode.getNode().getSubNodeList()
                            .get(nextNode.getNode().getSubNodeList().size() - 1);
                    leftChildLayoutX = _root.getChildren().get(leftChild.getLevelIndex()).getLayoutX();
                    rightChildLayoutX = _root.getChildren().get(rightChild.getLevelIndex()).getLayoutX();
                    preLayoutX = nextNode.getLayoutX();
                    offset = (leftChildLayoutX + rightChildLayoutX) / 2 - preLayoutX;
                }
            }
            nextNode.setLayoutX(preLayoutX + offset);
            currentNodeLevelIndex++;
            nextNode = (RectangleField) _root.getChildren().get(currentNodeLevelIndex);
            currentNodeLevel = nextNode.getNode().getLevel();
        }

        if (level != 0) {
            adjustParentLocation(
                    node.getParentNode(),
                    level - 1,
                    node.getParentNode().getSubNodeList().size() - 1,
                    false);
        }
    }

    // 所有结点位置均调整完，开始画结点之间的线
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

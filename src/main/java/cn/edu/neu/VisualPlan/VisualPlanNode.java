package cn.edu.neu.VisualPlan;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public abstract class VisualPlanNode {
    private int _level = 0;
    private List<VisualPlanNode> _subNodeList = null;

    // 层序遍历时使用的变量，前端用
    // 父结点
    private VisualPlanNode _parentNode = null;
    // 在前端层序遍历时的索引
    private int _levelIndex = 0;

    public VisualPlanNode(int level, List<VisualPlanNode> subNodeList) {
        _level = level;
        _subNodeList = subNodeList;
    }

    public int getLevel() {
       return _level;
    }

    public List<VisualPlanNode> getSubNodeList() {
        return _subNodeList;
    }

    public VisualPlanNode getParentNode() {
        return _parentNode;
    }

    private void setParentNode(VisualPlanNode parentNode) {
        _parentNode = parentNode;
    }

    public int getLevelIndex() {
        return _levelIndex;
    }

    private void setLevelIndex(int levelIndex) {
        _levelIndex = levelIndex;
    }
    
    public final void levelHandle(VisualPlanHandler handler) {
        Queue<VisualPlanNode> queue = new LinkedList<>();
        int levelIndex = 0;
        this.setLevelIndex(levelIndex++);
        queue.add(this);
        while (!queue.isEmpty()) {
            VisualPlanNode node = queue.remove();
            handler.onCall(node);
            for (VisualPlanNode subNode : node.getSubNodeList()) {
                subNode.setParentNode(node);
                subNode.setLevelIndex(levelIndex++);
                queue.add(subNode);
            }
        }
        handler.drawLine();
    }
}
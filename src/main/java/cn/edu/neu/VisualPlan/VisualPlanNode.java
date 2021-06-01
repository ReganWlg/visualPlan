package cn.edu.neu.VisualPlan;

import cn.edu.neu.VisualPlan.Graphics.Util.RectangleField;

import java.util.List;

public abstract class VisualPlanNode {
    private int _level = 0;
    private List<VisualPlanNode> _subNodeList = null;
    private VisualPlanNode _parentNode = null;

    // 层序遍历时使用的变量，前端用
    // 在前端层序遍历时的索引
    private int _levelIndex = 0;
    // 作为子节点，在子节点列表中的索引
    private int _subNodeIndex = 0;

    public VisualPlanNode(int level, List<VisualPlanNode> subNodeList) {
        _level = level;
        _subNodeList = subNodeList;
        int i = 0;
        for (VisualPlanNode subNode : getSubNodeList()) {
            subNode._parentNode = this;
            subNode._subNodeIndex = i;
            i++;
        }
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

    public int getLevelIndex() {
        return _levelIndex;
    }

    public void setLevelIndex(int levelIndex) {
        _levelIndex = levelIndex;
    }

    public int getSubNodeIndex() {
        return _subNodeIndex;
    }

    public RectangleField createRectangleField(int mode) {
        return new RectangleField(this, mode);
    }

    public abstract String getFieldByKey(String key);
}
package cn.edu.neu.VisualPlan.MySQL;

import cn.edu.neu.VisualPlan.Graphics.Util.RectangleField;
import cn.edu.neu.VisualPlan.VisualPlanNode;

import java.util.List;
import java.util.Map;

public final class MySQLVisualPlanNode extends VisualPlanNode {
    private String _description;
    private Map<String, String> _fieldMap = null;

    MySQLVisualPlanNode(int level, List<VisualPlanNode> subNodeList, String description, Map<String, String> fieldMap) {
        super(level, subNodeList);
        _description = description;
        _fieldMap = fieldMap;
    }

    public String getDescription() {
        return _description;
    }

    @Override
    public String getFieldByKey(String key) {
        return _fieldMap.get(key);
    }

    @Override
    public String toString() {
        return String.format("level: %d, description: %s, fieldMap: %s", getLevel(), getDescription(), _fieldMap.toString());
    }

    @Override
    public RectangleField createRectangleField() {
        RectangleField rectangleField = super.createRectangleField();
        return rectangleField;
    }
}
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
//        return String.format("Type: %s\n" +
//                "Description: %s\n\n" +
//                "Actual time: \n" +
//                "get the first row: %sms    " +
//                "get all rows: %sms\n\n" +
//                "Actual number: \n" +
//                "rows read: %s    " +
//                "cycles: %s\n",
//                getFieldByKey("type"),
//                getFieldByKey("description"),
//                getFieldByKey("Timing_first_row_ms"),
//                getFieldByKey("Timing_last_row_ms"),
//                getFieldByKey("Timing_rows"),
//                getFieldByKey("Timing_loops"));
    }

    @Override
    public RectangleField createRectangleField() {
        RectangleField rectangleField = super.createRectangleField();
        return rectangleField;
    }
}
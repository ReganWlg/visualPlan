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
        return String.format(
                "节点详细信息：" +
                        "\n\n\nLevel: %d" +
                        "\n\n\nType: %s" +
                        "\n\n\nDescription: \n%s" +
                        "\n\n\nEstimate" +
                        "\n\n    cost: %s" +
                        "\n    rows: %s" +
                        "\n\n\nActual" +
                        "\n\n    first row: %s(ms)" +
                        "\n    all rows: %s(ms)" +
                        "\n    rows: %s" +
                        "\n    loops: %s\n",
                getLevel(),
                getFieldByKey("type"),
                getFieldByKey("description"),
                getFieldByKey("cost"),
                getFieldByKey("output_rows"),
                getFieldByKey("Timing_first_row_ms"),
                getFieldByKey("Timing_last_row_ms"),
                getFieldByKey("Timing_rows"),
                getFieldByKey("Timing_loops"));
    }

    @Override
    public RectangleField createRectangleField(int mode) {
        return super.createRectangleField(mode);
    }
}
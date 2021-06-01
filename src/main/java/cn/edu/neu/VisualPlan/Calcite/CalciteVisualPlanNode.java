package cn.edu.neu.VisualPlan.Calcite;

import cn.edu.neu.VisualPlan.Graphics.Util.RectangleField;
import cn.edu.neu.VisualPlan.VisualPlanNode;

import java.util.List;
import java.util.Map;

public class CalciteVisualPlanNode extends VisualPlanNode {

    private String _description;
    private Map<String, String> _fieldMap = null;

    public CalciteVisualPlanNode(int level, List<VisualPlanNode> subNodeList, String description, Map<String, String> fieldMap) {
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
                        "\n\n\nRowCount: %s" +
                        "\n\n\nCumulative Cost" +
                        "\n\n    rows: %s" +
                        "\n    cpu: %s" +
                        "\n    io: %s" +
                        "\n\n\nId: %s\n",
                getLevel(),
                getFieldByKey("type"),
                getFieldByKey("description"),
                getFieldByKey("rowCount"),
                getFieldByKey("rows"),
                getFieldByKey("cpu"),
                getFieldByKey("io"),
                getFieldByKey("id"));
    }

    @Override
    public RectangleField createRectangleField(int mode) {
        return super.createRectangleField(mode);
    }
}

package cn.edu.neu.VisualPlan.PostgreSQL;

import cn.edu.neu.VisualPlan.Graphics.Util.RectangleField;
import cn.edu.neu.VisualPlan.VisualPlanNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PostgreSQLVisualPlanNode extends VisualPlanNode {

    private ArrayList<String> _description;
    private Map<String, String> _fieldMap = null;

    public PostgreSQLVisualPlanNode(int level, List<VisualPlanNode> subNodeList, ArrayList<String> description, Map<String, String> fieldMap) {
        super(level, subNodeList);
        _description = description;
        _fieldMap = fieldMap;
    }

    public ArrayList<String> getDescription() {
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
                "\n\n    each row: %s" +
                "\n    all rows: %s" +
                "\n    rows: %s" +
                "\n    width: %s" +
                "\n\n\nActual" +
                "\n\n    each row: %s(ms)" +
                "\n    all rows: %s(ms)" +
                "\n    rows: %s" +
                "\n    loops: %s\n",
                getLevel(),
                getFieldByKey("type"),
                getFieldByKey("description"),
                getFieldByKey("estimate_cost_each_row_ms"),
                getFieldByKey("estimate_cost_all_rows_ms"),
                getFieldByKey("estimate_rows"),
                getFieldByKey("estimate_width"),
                getFieldByKey("actual_time_each_row_ms"),
                getFieldByKey("actual_time_all_rows_ms"),
                getFieldByKey("actual_rows"),
                getFieldByKey("actual_loops"));
    }

    @Override
    public RectangleField createRectangleField(int mode) {
        return super.createRectangleField(mode);
    }
}

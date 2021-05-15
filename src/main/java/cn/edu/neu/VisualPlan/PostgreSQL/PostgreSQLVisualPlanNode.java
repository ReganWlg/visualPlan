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
//        return String.format("level: %d, description: %s, fieldMap: %s", getLevel(), getDescription(), _fieldMap.toString());
        StringBuilder str = new StringBuilder();
        str.append(String.format("level: %d description: ", getLevel()));
        for (String description : _description) {
            str.append(String.format("\n\t%s", description));
        }
        return str.toString();
    }

    @Override
    public RectangleField createRectangleField(int mode) {
        return super.createRectangleField(mode);
    }
}

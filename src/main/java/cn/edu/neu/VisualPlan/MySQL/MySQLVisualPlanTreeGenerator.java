package cn.edu.neu.VisualPlan.MySQL;

import cn.edu.neu.VisualPlan.MySQL.Analyzer.Analyzer;
import cn.edu.neu.VisualPlan.VisualPlanNode;
import cn.edu.neu.VisualPlan.VisualPlanTreeGenerator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MySQLVisualPlanTreeGenerator implements VisualPlanTreeGenerator {
    private static VisualPlanTreeGenerator _instance = new MySQLVisualPlanTreeGenerator();

    private MySQLVisualPlanTreeGenerator() {

    }

    public static VisualPlanTreeGenerator getInstance() {
        return _instance;
    }

    @Override
    public VisualPlanNode getVisualPlanTree(Statement stmt, String sql) throws SQLException {
        String planRawString = getPlanRawString(stmt, sql);
        System.out.println(planRawString.toString());
        return convVisualPlanTree(planRawString);
    }

    private String getPlanRawString(Statement stmt, String sql) throws SQLException {
        String planRawString = null;
        ResultSet rs = stmt.executeQuery(String.format("explain analyze %s", sql));
        if (!rs.next()) {
            System.err.println(String.format("Can't get, please check your sql: %s", sql));
        } else {
            planRawString = rs.getString("EXPLAIN");
        }
        rs.close();
        return planRawString;
    }

    // public for debug, this function should be private.
    public VisualPlanNode convVisualPlanTree(String planRawString) {
        String[] nodeRawStrings = planRawString.split("\n");
        Queue<LevelAndDescription> levelAndDescriptionQueue = new LinkedList<>();
        for (String nodeRawString : nodeRawStrings) {
            LevelAndDescription levelAndDescription = splitLevelAndDescription(nodeRawString);
            levelAndDescriptionQueue.add(levelAndDescription);
        }
        return buildVisualPlanTree(levelAndDescriptionQueue);
    }

    private class LevelAndDescription {
        private int _level;
        private String _description;

        public LevelAndDescription(int level, String description) {
            _level = level;
            _description = description;
        }

        public int getLevel() {
            return _level;
        }

        public String getDescription() {
            return _description;
        }
    };

    private Pattern _levelAndDescriptionPattern = Pattern.compile("(.*)-> (.*)");
    private final int RECESS_LENGTH = 4;
    private LevelAndDescription splitLevelAndDescription(String nodeRawString) {
        Matcher matcher = _levelAndDescriptionPattern.matcher(nodeRawString);
        if (!matcher.find()) {
            System.err.println(String.format("can't split, please check raw string: %s", nodeRawString));
            return null;
        }
        int level = matcher.group(1).length() / RECESS_LENGTH;
        String description = matcher.group(2);
        return new LevelAndDescription(level, description);
    }

    // 先序遍历建树
    private VisualPlanNode buildVisualPlanTree(Queue<LevelAndDescription> levelAndDescriptionQueue) {
        if (levelAndDescriptionQueue.isEmpty()) {
            return null;
        }
        LevelAndDescription rootLevelAndDescription = levelAndDescriptionQueue.remove();
        int level = rootLevelAndDescription.getLevel();
        String description = rootLevelAndDescription.getDescription();
        Map<String, String> fieldMap = Analyzer.getInstance().getFieldMapByDescription(description);
        List<VisualPlanNode> subNodeList = new ArrayList<>();
        while (!levelAndDescriptionQueue.isEmpty() && levelAndDescriptionQueue.element().getLevel() == level + 1) {
            VisualPlanNode subNode = buildVisualPlanTree(levelAndDescriptionQueue);
            subNodeList.add(subNode);
        }
        return new MySQLVisualPlanNode(level, subNodeList, description, fieldMap);
    }
}
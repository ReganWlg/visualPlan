package cn.edu.neu.VisualPlan.PostgreSQL;

import cn.edu.neu.VisualPlan.PostgreSQL.Analyzer.Analyzer;
import cn.edu.neu.VisualPlan.VisualPlanNode;
import cn.edu.neu.VisualPlan.VisualPlanTreeGenerator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PostgreSQLVisualPlanTreeGenerator implements VisualPlanTreeGenerator {
    private static VisualPlanTreeGenerator _instance = new PostgreSQLVisualPlanTreeGenerator();

    private PostgreSQLVisualPlanTreeGenerator() {

    }

    public static VisualPlanTreeGenerator getInstance() {
        return _instance;
    }

    @Override
    public VisualPlanNode getVisualPlanTree(Statement stmt, String sql) throws SQLException {
        ArrayList<String> planRawString = getPlanRawString(stmt, sql);
        return convVisualPlanTree(planRawString);
    }

    private ArrayList<String> getPlanRawString(Statement stmt, String sql) throws SQLException {
        ArrayList<String> planRawString = new ArrayList<String>();
        ResultSet rs = stmt.executeQuery(String.format("explain analyze %s", sql));
        while (rs.next()) {
            System.out.println(rs.getString(1));
            planRawString.add(rs.getString(1));
        }
        rs.close();
        return planRawString;
    }

    // public for debug, this function should be private.
    public VisualPlanNode convVisualPlanTree(ArrayList<String> planRawString) {
        LinkedList<LevelAndDescription> levelAndDescriptionList = new LinkedList<>();
        for (String nodeRawString : planRawString) {
            splitLevelAndDescription(nodeRawString, levelAndDescriptionList);
        }
        for (LevelAndDescription levelAndDescription : levelAndDescriptionList) {
            StringBuilder str = new StringBuilder();
            str.append(String.format("level: %d description: ", levelAndDescription.getLevel()));
            for (String description : levelAndDescription.getDescription()) {
                str.append(String.format("\n\t%s", description));
            }
            System.out.println(str);
        }

        return buildVisualPlanTree(levelAndDescriptionList);
    }

    private class LevelAndDescription {
        private int _level;
        private ArrayList<String> _description;

        public LevelAndDescription(int level, ArrayList<String> description) {
            _level = level;
            _description = description;
        }

        public int getLevel() {
            return _level;
        }

        public ArrayList<String> getDescription() {
            return _description;
        }
    };

    private Pattern _levelAndDescriptionPattern1 = Pattern.compile("(.*)->  (.*)");
    private Pattern _levelAndDescriptionPattern2 = Pattern.compile("(.*)[A-Z](.*)");
    private Pattern _levelAndDescriptionPattern3 = Pattern.compile("Planning Time: (.*)");
    private Pattern _levelAndDescriptionPattern4 = Pattern.compile("Execution Time: (.*)");
    private final int RECESS_LENGTH = 6; // 缩进长度
    private void splitLevelAndDescription(String nodeRawString, LinkedList<LevelAndDescription> levelAndDescriptionList) {
        int level = 0;
        ArrayList<String> description = new ArrayList<>();
        Matcher matcher1 = _levelAndDescriptionPattern1.matcher(nodeRawString);
        Matcher matcher2 = _levelAndDescriptionPattern2.matcher(nodeRawString);
        Matcher matcher3 = _levelAndDescriptionPattern3.matcher(nodeRawString);
        Matcher matcher4 = _levelAndDescriptionPattern4.matcher(nodeRawString);
        if (matcher1.find()) {
            level = matcher1.group(1).length() / RECESS_LENGTH + 1;
            description.add(matcher1.group(2));
            levelAndDescriptionList.add(new LevelAndDescription(level, description));
        } else if (matcher3.find() || matcher4.find()) {
            LevelAndDescription rootNode = levelAndDescriptionList.getFirst();
            description = rootNode.getDescription();
            description.add(nodeRawString);
        } else if (matcher2.find()) {
            if (levelAndDescriptionList.isEmpty()) {
                level = 0;
                description.add(nodeRawString.trim());
                levelAndDescriptionList.add(new LevelAndDescription(level, description));
            } else {
                LevelAndDescription lastNode = levelAndDescriptionList.getLast();
                description = lastNode.getDescription();
                description.add(nodeRawString.trim());
            }
        } else {
            System.err.println(String.format("can't split, please check raw string: %s", nodeRawString));
        }
    }

    // 先序遍历建树
    private VisualPlanNode buildVisualPlanTree(LinkedList<LevelAndDescription> levelAndDescriptionQueue) {
        if (levelAndDescriptionQueue.isEmpty()) {
            return null;
        }
        LevelAndDescription rootLevelAndDescription = levelAndDescriptionQueue.removeFirst();
        int level = rootLevelAndDescription.getLevel();
        ArrayList<String> description = rootLevelAndDescription.getDescription();
        Map<String, String> fieldMap = Analyzer.getInstance().getFieldMapByDescription(description);
        List<VisualPlanNode> subNodeList = new ArrayList<>();
        while (!levelAndDescriptionQueue.isEmpty() && levelAndDescriptionQueue.element().getLevel() == level + 1) {
            VisualPlanNode subNode = buildVisualPlanTree(levelAndDescriptionQueue);
            subNodeList.add(subNode);
        }
        return new PostgreSQLVisualPlanNode(level, subNodeList, description, fieldMap);
    }
}

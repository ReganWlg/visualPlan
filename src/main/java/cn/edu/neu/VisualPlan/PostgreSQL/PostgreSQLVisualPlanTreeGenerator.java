package cn.edu.neu.VisualPlan.PostgreSQL;

import cn.edu.neu.VisualPlan.Graphics.Control.MainStageControl;
import cn.edu.neu.VisualPlan.Graphics.Util.DialogBuilder;
import cn.edu.neu.VisualPlan.Graphics.Util.StageManager;
import cn.edu.neu.VisualPlan.PostgreSQL.Analyzer.Analyzer;
import cn.edu.neu.VisualPlan.VisualPlanNode;
import cn.edu.neu.VisualPlan.VisualPlanTreeGenerator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PostgreSQLVisualPlanTreeGenerator implements VisualPlanTreeGenerator {
    private static VisualPlanTreeGenerator _instance = new PostgreSQLVisualPlanTreeGenerator();
    private int _index;

    private PostgreSQLVisualPlanTreeGenerator() {

    }

    public static VisualPlanTreeGenerator getInstance() {
        return _instance;
    }

    @Override
    public VisualPlanNode getVisualPlanTree(Connection conn, String sql, int index) throws SQLException {
        _index = index;
        Statement stmt = null;
        ArrayList<String> planRawString = new ArrayList<>();
        try {
            // 获取执行 SQL 对象
            stmt = conn.createStatement();
            planRawString = getPlanRawString(stmt, sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // 释放资源
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return convVisualPlanTree(planRawString);
    }

    private ArrayList<String> getPlanRawString(Statement stmt, String sql) throws SQLException {
        ArrayList<String> planRawString = new ArrayList<String>();
        try {
            ResultSet rs = stmt.executeQuery(String.format("explain analyze %s", sql));
            while (rs.next()) {
                System.out.println(rs.getString(1));
                planRawString.add(rs.getString(1));
            }
            rs.close();
        } catch (SQLException e) {
            String controlKey = "MainStageControl_" + _index;
            MainStageControl mainStageControl = (MainStageControl) StageManager.CONTROLLER.get(controlKey);
            new DialogBuilder(mainStageControl.btn_query)
                    .setTitle("查询失败!")
                    .setMessage("请检查SQL语句，重新查询")
                    .setPositiveBtn("确定")
                    .create();
        }

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

    private final Pattern _levelAndDescriptionPattern1 = Pattern.compile("(.*)->  (.*)");
    private final Pattern _levelAndDescriptionPattern2 = Pattern.compile("(.*)[A-Z](.*)");
    private final Pattern _levelAndDescriptionPattern3 = Pattern.compile("Planning Time: (.*)");
    private final Pattern _levelAndDescriptionPattern4 = Pattern.compile("Execution Time: (.*)");
    private final Pattern _levelAndDescriptionPattern5 = Pattern.compile("(.*)SubPlan [1-9]+[0-9]*$");
    private boolean isSubPlan = false;
    private int subPlanLevel = 0;
    private int subPlanSpacingLength = 0;
    private final int RECESS_LENGTH = 6; // 缩进长度
    private void splitLevelAndDescription(String nodeRawString, LinkedList<LevelAndDescription> levelAndDescriptionList) {
        int level = 0;
        ArrayList<String> description = new ArrayList<>();
        Matcher matcher1 = _levelAndDescriptionPattern1.matcher(nodeRawString);
        Matcher matcher2 = _levelAndDescriptionPattern2.matcher(nodeRawString);
        Matcher matcher3 = _levelAndDescriptionPattern3.matcher(nodeRawString);
        Matcher matcher4 = _levelAndDescriptionPattern4.matcher(nodeRawString);
        Matcher matcher5 = _levelAndDescriptionPattern5.matcher(nodeRawString);
        if (matcher1.find()) {
            if (isSubPlan) {
                level = (matcher1.group(1).length() - subPlanSpacingLength) / RECESS_LENGTH + subPlanLevel + 1;
            } else {
                level = matcher1.group(1).length() / RECESS_LENGTH + 1;
                if (level < subPlanLevel) {
                    isSubPlan = false;
                }
            }
            description.add(matcher1.group(2));
            levelAndDescriptionList.add(new LevelAndDescription(level, description));
        } else if (matcher3.find() || matcher4.find()) {
            isSubPlan = false;
            LevelAndDescription rootNode = levelAndDescriptionList.getFirst();
            description = rootNode.getDescription();
            description.add(nodeRawString);
        } else if (matcher5.find()) {
            isSubPlan = true;
            level = matcher5.group(1).length() / RECESS_LENGTH + 1;
            subPlanLevel = level;
            subPlanSpacingLength = matcher5.group(1).length();
            description.add(nodeRawString.trim());
            levelAndDescriptionList.add(new LevelAndDescription(level, description));
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
            System.err.printf("can't split, please check raw string: %s%n", nodeRawString);
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

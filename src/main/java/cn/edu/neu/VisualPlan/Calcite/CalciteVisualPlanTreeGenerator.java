package cn.edu.neu.VisualPlan.Calcite;

import cn.edu.neu.Demo.Demo06;
import cn.edu.neu.VisualPlan.Calcite.Analyzer.Analyzer;
import cn.edu.neu.VisualPlan.Graphics.Control.MainStageControl;
import cn.edu.neu.VisualPlan.Graphics.Util.DialogBuilder;
import cn.edu.neu.VisualPlan.Graphics.Util.StageManager;
import cn.edu.neu.VisualPlan.VisualPlanNode;
import cn.edu.neu.VisualPlan.VisualPlanTreeGenerator;
import com.google.common.collect.ImmutableSet;
import org.apache.calcite.adapter.enumerable.EnumerableConvention;
import org.apache.calcite.adapter.enumerable.EnumerableRules;
import org.apache.calcite.avatica.util.Casing;
import org.apache.calcite.config.CalciteConnectionConfigImpl;
import org.apache.calcite.config.CalciteConnectionProperty;
import org.apache.calcite.config.CalciteSystemProperty;
import org.apache.calcite.jdbc.CalciteConnection;
import org.apache.calcite.jdbc.CalciteSchema;
import org.apache.calcite.jdbc.JavaTypeFactoryImpl;
import org.apache.calcite.plan.*;
import org.apache.calcite.plan.volcano.VolcanoPlanner;
import org.apache.calcite.prepare.CalciteCatalogReader;
import org.apache.calcite.rel.RelDistributionTraitDef;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.rel.rules.CoreRules;
import org.apache.calcite.rel.rules.FilterJoinRule;
import org.apache.calcite.rel.rules.JoinPushThroughJoinRule;
import org.apache.calcite.rel.rules.PruneEmptyRules;
import org.apache.calcite.rel.type.RelDataTypeSystem;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.sql.SqlExplainLevel;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.validate.SqlValidator;
import org.apache.calcite.sql.validate.SqlValidatorUtil;
import org.apache.calcite.sql2rel.SqlToRelConverter;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.RelRunner;

import java.sql.*;
import java.util.*;

public class CalciteVisualPlanTreeGenerator implements VisualPlanTreeGenerator {
    private static VisualPlanTreeGenerator _instance = new CalciteVisualPlanTreeGenerator();
    private int _index;

    private CalciteVisualPlanTreeGenerator() {

    }

    public static VisualPlanTreeGenerator getInstance() {
        return _instance;
    }

    @Override
    public VisualPlanNode getVisualPlanTree(Connection conn, String sql, int index) throws SQLException {
        _index = index;
        String planRawString = getPlanRawString(conn, sql);
        System.out.println(planRawString);
        return convVisualPlanTree(planRawString);
    }

    public static final ImmutableSet<RelOptRule> RULE_SET =
            ImmutableSet.of(
                    EnumerableRules.ENUMERABLE_TABLE_SCAN_RULE,
                    EnumerableRules.ENUMERABLE_JOIN_RULE,
//                    EnumerableRules.ENUMERABLE_MERGE_JOIN_RULE,
                    EnumerableRules.ENUMERABLE_CORRELATE_RULE,
                    EnumerableRules.ENUMERABLE_PROJECT_RULE,
                    EnumerableRules.ENUMERABLE_FILTER_RULE,
                    EnumerableRules.ENUMERABLE_AGGREGATE_RULE,
                    EnumerableRules.ENUMERABLE_SORT_RULE,
                    EnumerableRules.ENUMERABLE_LIMIT_RULE,
                    EnumerableRules.ENUMERABLE_UNION_RULE,
                    EnumerableRules.ENUMERABLE_FILTER_TO_CALC_RULE,
                    EnumerableRules.ENUMERABLE_INTERSECT_RULE,
                    EnumerableRules.ENUMERABLE_MINUS_RULE,
                    EnumerableRules.ENUMERABLE_TABLE_MODIFICATION_RULE,
                    EnumerableRules.ENUMERABLE_VALUES_RULE,
                    EnumerableRules.ENUMERABLE_WINDOW_RULE,
                    EnumerableRules.ENUMERABLE_MATCH_RULE,
                    CoreRules.PROJECT_TO_SEMI_JOIN,
                    CoreRules.JOIN_TO_SEMI_JOIN,
                    CoreRules.MATCH,
                    CalciteSystemProperty.COMMUTE.value()
                            ? CoreRules.JOIN_ASSOCIATE
                            : CoreRules.PROJECT_MERGE,
                    CoreRules.AGGREGATE_STAR_TABLE,
                    CoreRules.AGGREGATE_PROJECT_STAR_TABLE,
                    CoreRules.FILTER_SCAN,
                    CoreRules.FILTER_PROJECT_TRANSPOSE,
                    CoreRules.FILTER_INTO_JOIN,
                    CoreRules.AGGREGATE_EXPAND_DISTINCT_AGGREGATES,
                    CoreRules.AGGREGATE_REDUCE_FUNCTIONS,
                    CoreRules.FILTER_AGGREGATE_TRANSPOSE,
                    CoreRules.JOIN_COMMUTE,
                    JoinPushThroughJoinRule.RIGHT,
                    JoinPushThroughJoinRule.LEFT,
                    CoreRules.SORT_PROJECT_TRANSPOSE);

    private String getPlanRawString(Connection connection, String sql) throws SQLException {
        String planRawString = null;
        CalciteConnection calciteConnection = connection.unwrap(CalciteConnection.class);
        SchemaPlus mySchema = calciteConnection.getRootSchema().getSubSchema(connection.getSchema());

        SqlParser.ConfigBuilder builder = SqlParser.configBuilder();
        //以下需要设置成大写并且忽略大小写
        builder.setQuotedCasing(Casing.TO_UPPER);
        builder.setUnquotedCasing(Casing.TO_UPPER);
        builder.setCaseSensitive(false);

        FrameworkConfig frameworkConfig = Frameworks.newConfigBuilder()
                .defaultSchema(mySchema)
                .parserConfig(builder.build())
                .build();
        VolcanoPlanner planner = new VolcanoPlanner();
        planner.addRelTraitDef(ConventionTraitDef.INSTANCE);
        planner.addRelTraitDef(RelDistributionTraitDef.INSTANCE);
        // add rules
        planner.addRule(FilterJoinRule.FilterIntoJoinRule.Config.DEFAULT.toRule());
//    planner.addRule(ReduceExpressionsRule.ProjectReduceExpressionsRule.Config.DEFAULT.toRule());
        planner.addRule(PruneEmptyRules.PROJECT_INSTANCE);

//        // add ConverterRule
//        planner.addRule(EnumerableRules.ENUMERABLE_JOIN_RULE);
//        planner.addRule(EnumerableRules.ENUMERABLE_SORT_RULE);
//        planner.addRule(EnumerableRules.ENUMERABLE_VALUES_RULE);
//        planner.addRule(EnumerableRules.ENUMERABLE_PROJECT_RULE);
//        planner.addRule(EnumerableRules.ENUMERABLE_FILTER_RULE);
//        planner.addRule(EnumerableRules.ENUMERABLE_FILTER_TO_CALC_RULE);

        for (RelOptRule rule : RULE_SET) {
            planner.addRule(rule);
        }
//        for (RelOptRule rule : EnumerableRules.rules()) {
//            planner.addRule(rule);
//        }

        try {
            JavaTypeFactoryImpl factory = new JavaTypeFactoryImpl(RelDataTypeSystem.DEFAULT);
            // sql parser
            SqlParser parser = SqlParser.create(sql, SqlParser.Config.DEFAULT);
            SqlNode parsed = parser.parseStmt();

//            System.out.printf("%nThe SqlNode after parsed is:%n%n%s%n", parsed.toString());

            //这里需要注意大小写问题，否则表会无法找到
            Properties properties = new Properties();
            properties.setProperty(CalciteConnectionProperty.CASE_SENSITIVE.camelName(),
                    String.valueOf(frameworkConfig.getParserConfig().caseSensitive()));

            CalciteCatalogReader calciteCatalogReader = new CalciteCatalogReader(
                    CalciteSchema.from(mySchema),
                    CalciteSchema.from(mySchema).path(null),
                    factory,
                    new CalciteConnectionConfigImpl(properties));

            // sql validate
            SqlValidator validator = SqlValidatorUtil.newValidator(
                    SqlStdOperatorTable.instance(),
                    calciteCatalogReader,
                    factory,
                    frameworkConfig.getSqlValidatorConfig());
            SqlNode validated = validator.validate(parsed);
//            System.out.printf("%nThe SqlNode after validated is:%n%n%s%n", validated.toString());

            final RexBuilder rexBuilder = new RexBuilder(factory);
            final RelOptCluster cluster = RelOptCluster.create(planner, rexBuilder);

            // init SqlToRelConverter config
            final SqlToRelConverter.Config sqlconfig = SqlToRelConverter.config()
                    .withTrimUnusedFields(false);

            // SqlNode toRelNode
            final SqlToRelConverter sqlToRelConverter = new SqlToRelConverter(new Demo06.ViewExpanderImpl(),
                    validator, calciteCatalogReader, cluster, frameworkConfig.getConvertletTable(), sqlconfig);
            RelRoot root = sqlToRelConverter.convertQuery(validated, false, true);

            RelNode relNode = root.rel;
//            System.out.printf("%nThe relational expression string before optimized is:%n%n%s", RelOptUtil.toString(relNode));

            RelTraitSet desiredTraits =
                    relNode.getCluster().traitSet().replace(EnumerableConvention.INSTANCE);
            relNode = planner.changeTraits(relNode, desiredTraits);

            planner.setRoot(relNode);
            relNode = planner.findBestExp();
//            System.out.printf("%nThe Best relational expression string:%n%n");
            planRawString = RelOptUtil.toString(relNode, SqlExplainLevel.ALL_ATTRIBUTES);
//            System.out.println(planRawString);

//            long startTime = System.currentTimeMillis();
//            RelRunner runner = connection.unwrap(RelRunner.class);
//            PreparedStatement preparedStatement = runner.prepare(relNode);
//            ResultSet resultSet = preparedStatement.executeQuery();
//            long endTime = System.currentTimeMillis();
//
//            long queryTime = endTime - startTime;
//            System.out.println("query time: " + queryTime);

        } catch (/*SQLException | */SqlParseException e) {
            e.printStackTrace();
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

    private final int RECESS_LENGTH = 2;
    private LevelAndDescription splitLevelAndDescription(String nodeRawString) {
        String description = nodeRawString.trim();
        int level = (nodeRawString.length() - description.length()) / RECESS_LENGTH;
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
        return new CalciteVisualPlanNode(level, subNodeList, description, fieldMap);
    }
}

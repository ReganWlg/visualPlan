package cn.edu.neu.Demo;

import org.apache.calcite.adapter.enumerable.EnumerableConvention;
import org.apache.calcite.adapter.enumerable.EnumerableRules;
import org.apache.calcite.config.CalciteConnectionConfigImpl;
import org.apache.calcite.jdbc.CalciteConnection;
import org.apache.calcite.jdbc.CalciteSchema;
import org.apache.calcite.jdbc.JavaTypeFactoryImpl;
import org.apache.calcite.plan.*;
import org.apache.calcite.plan.volcano.VolcanoPlanner;
import org.apache.calcite.prepare.CalciteCatalogReader;
import org.apache.calcite.rel.RelDistributionTraitDef;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.rel.rules.FilterJoinRule;
import org.apache.calcite.rel.rules.PruneEmptyRules;
import org.apache.calcite.rel.type.RelDataType;
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
import java.util.List;
import java.util.Properties;

public class Demo06 {

    public static class ViewExpanderImpl implements RelOptTable.ViewExpander {
        public ViewExpanderImpl() {
        }

        @Override
        public RelRoot expandView(RelDataType rowType, String queryString, List<String> schemaPath,
                                  List<String> viewPath) {
            return null;
        }
    }
    public static void main(String[] args) throws SQLException {

        Properties config = new Properties();
        config.put("model", "E:/Project/visualPlan/src/main/resources/model.json");
        config.put("lex", "MYSQL");
        String sql = "Select \"p\".\"p_name\", \"p\".\"p_mfgr\"," +
        "\"p\".\"p_retailprice\", \"ps\".\"ps_supplycost\"" +
        "From \"tpch\".\"part\" \"p\", \"tpch\".\"partsupp\" \"ps\"" +
        "Where \"p\".\"p_retailprice\">\"ps\".\"ps_supplycost\"" +
        "Limit 200 ";
        Connection connection = DriverManager.getConnection("jdbc:calcite:", config);
        CalciteConnection calciteConnection = connection.unwrap(CalciteConnection.class);
        SchemaPlus mySchema = calciteConnection.getRootSchema().getSubSchema(connection.getSchema());
        FrameworkConfig frameworkConfig = Frameworks.newConfigBuilder()
                .defaultSchema(mySchema)
                .build();
        VolcanoPlanner planner = new VolcanoPlanner();
        planner.addRelTraitDef(ConventionTraitDef.INSTANCE);
        planner.addRelTraitDef(RelDistributionTraitDef.INSTANCE);
        // add rules
        planner.addRule(FilterJoinRule.FilterIntoJoinRule.Config.DEFAULT.toRule());
//    planner.addRule(ReduceExpressionsRule.ProjectReduceExpressionsRule.Config.DEFAULT.toRule());
        planner.addRule(PruneEmptyRules.PROJECT_INSTANCE);


        // add ConverterRule
        planner.addRule(EnumerableRules.ENUMERABLE_JOIN_RULE);
        planner.addRule(EnumerableRules.ENUMERABLE_SORT_RULE);
        planner.addRule(EnumerableRules.ENUMERABLE_VALUES_RULE);
        planner.addRule(EnumerableRules.ENUMERABLE_PROJECT_RULE);
        planner.addRule(EnumerableRules.ENUMERABLE_FILTER_RULE);
        planner.addRule(EnumerableRules.ENUMERABLE_FILTER_TO_CALC_RULE);

        try {
            JavaTypeFactoryImpl factory = new JavaTypeFactoryImpl(RelDataTypeSystem.DEFAULT);
            // sql parser
            SqlParser parser = SqlParser.create(sql, SqlParser.Config.DEFAULT);
            SqlNode parsed = parser.parseStmt();

            System.out.printf("%nThe SqlNode after parsed is:%n%n%s%n", parsed.toString());

            CalciteCatalogReader calciteCatalogReader = new CalciteCatalogReader(
                    CalciteSchema.from(mySchema),
                    CalciteSchema.from(mySchema).path(null),
                    factory,
                    new CalciteConnectionConfigImpl(new Properties()));

            // sql validate
            SqlValidator validator = SqlValidatorUtil.newValidator(SqlStdOperatorTable.instance(), calciteCatalogReader,
                    factory, frameworkConfig.getSqlValidatorConfig());
            SqlNode validated = validator.validate(parsed);
            System.out.printf("%nThe SqlNode after validated is:%n%n%s%n", validated.toString());

            final RexBuilder rexBuilder = new RexBuilder(factory);
            final RelOptCluster cluster = RelOptCluster.create(planner, rexBuilder);

            // init SqlToRelConverter config
            final SqlToRelConverter.Config sqlconfig = SqlToRelConverter.config()
                    .withTrimUnusedFields(false);

            // SqlNode toRelNode
            final SqlToRelConverter sqlToRelConverter = new SqlToRelConverter(new ViewExpanderImpl(),
                    validator, calciteCatalogReader, cluster, frameworkConfig.getConvertletTable(), sqlconfig);
            RelRoot root = sqlToRelConverter.convertQuery(validated, false, true);

            RelNode relNode = root.rel;
            System.out.printf("%nThe relational expression string before optimized is:%n%n%s", RelOptUtil.toString(relNode));

            RelTraitSet desiredTraits =
                    relNode.getCluster().traitSet().replace(EnumerableConvention.INSTANCE);
            relNode = planner.changeTraits(relNode, desiredTraits);

            planner.setRoot(relNode);
            relNode = planner.findBestExp();
            System.out.printf("%nThe Best relational expression string:%n%n");
            System.out.println(RelOptUtil.toString(relNode, SqlExplainLevel.ALL_ATTRIBUTES));

            long startTime = System.currentTimeMillis();
            RelRunner runner = connection.unwrap(RelRunner.class);
            PreparedStatement preparedStatement = runner.prepare(relNode);
            ResultSet resultSet = preparedStatement.executeQuery();
            long endTime = System.currentTimeMillis();

            long queryTime = endTime - startTime;
            System.out.println("query time: " + queryTime);

            // print query result
//            final StringBuilder buf = new StringBuilder();
//
//            while (resultSet.next()) {
//                int n = resultSet.getMetaData().getColumnCount();
//                for (int i = 1; i <= n; i++) {
//                    buf.append(i > 1 ? "; " : "")
//                            .append(resultSet.getMetaData().getColumnLabel(i))
//                            .append("=")
//                            .append(resultSet.getObject(i));
//                }
//                System.out.println(buf);
//                buf.setLength(0);
//            }
        } catch (SQLException | SqlParseException e) {
            e.printStackTrace();
        }
    }
}

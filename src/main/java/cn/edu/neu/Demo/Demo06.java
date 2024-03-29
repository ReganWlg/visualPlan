package cn.edu.neu.Demo;

import org.apache.calcite.adapter.enumerable.EnumerableConvention;
import org.apache.calcite.adapter.enumerable.EnumerableRules;
import org.apache.calcite.avatica.util.Casing;
import org.apache.calcite.config.CalciteConnectionConfigImpl;
import org.apache.calcite.config.CalciteConnectionProperty;
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

        String dbms = "postgresql";
        String ip = "localhost";
        String port = "5432";
        String database = "tpc";
        String jdbcDriver = null;
        String jdbcSchema = "tpch";
        String jdbcUrl = String.format("jdbc:%s://%s:%s/%s", dbms, ip, port, database);
        String jdbcUser = "postgres";
        String jdbcPassword = "regan0429";

//        String dbms = "mysql";
//        String ip = "localhost";
//        String port = "3306";
//        String database = "information_schema?useSSL=true&serverTimezone=UTC";
//        String jdbcDriver = null;
//        String jdbcSchema = "information_schema";
//        String jdbcUrl = String.format("jdbc:%s://%s:%s/%s", dbms, ip, port, database);
//        String jdbcUser = "root";
//        String jdbcPassword = "123456";

        if (dbms.equals("postgresql")) {
            jdbcDriver = "org.postgresql.Driver";
        } else if (dbms.equals("mysql")) {
            jdbcDriver = "com.mysql.cj.jdbc.Driver";
        }

        Properties config = new Properties();
        config.put("model",
                "inline:" +
                "{\n" +
                "  version: '1.0',\n" +
                "  defaultSchema: '" + jdbcSchema + "',\n" +
                "  schemas: [\n" +
                "    {\n" +
                "      name: '" + jdbcSchema + "',\n" +
                "      type: 'jdbc',\n" +
                "      jdbcDriver: '" + jdbcDriver + "',\n" +
                "      jdbcUrl: '" + jdbcUrl + "',\n" +
                "      jdbcSchema: '" + jdbcSchema + "',\n" +
                "      jdbcUser: '" + jdbcUser + "',\n" +
                "      jdbcPassword: '" + jdbcPassword + "'\n" +
                "    }\n" +
                "  ]\n" +
                "}" );
        config.put("lex", "MYSQL");
        String sql = "Select p.p_name, p.p_mfgr," +
                "p.p_retailprice, ps.ps_supplycost\n" +
                "From part p, partsupp ps\n" +
                "Where p.p_retailprice>ps.ps_supplycost\n" +
                "Limit 200 ";
//        String sql = "select * from VIEWS";

        Connection connection = DriverManager.getConnection("jdbc:calcite:", config);
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

        } catch (SQLException | SqlParseException e) {
            e.printStackTrace();
        }
    }
}

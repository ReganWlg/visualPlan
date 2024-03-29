# 查询计划可视化

# 1. 项目代码仓库

[https://github.com/ReganWlg/visualPlan](https://github.com/ReganWlg/visualPlan)

# 2. 项目目的

从 MySQL、PostgreSQL、Calcite查询优化器中，获取查询计划数据，然后对数据进行可视化处理

# 3. 参考对象

## 3.1 SqlServer

![SqlServer](./Images/SqlServer.jpg)

## 3.2 MySQL workbench

![MySQL workbench](./Images/MySQL_workbench.jpeg)

## 3.3 Greenplum DB

![Greenplum DB](./Images/Greenplum_DB.jpg)

# 4. 查询计划数据获取

在初步对的调研中，有 3 种方法可以获取查询计划，我们最终选择使用 mysql analyze。

## 4.1 mysql explain

- 语法：explain \<DQL\>
- 例如：explain select * from VIEWS;
- 结果：

    ![mysql explain](./Images/mysql_explain.png)

- 缺点：没有真正地执行，没有具体的执行时间；数据项不够。
- 优点：速度快，数据已经格式化了。

## 4.2 mysql analyze

- 语法：explain analyze \<DQL\>
- 例如：explain analyze explain analyze;
- 结果：（窗口问题，显示不全）

    ![mysql analyze](./Images/mysql_analyze.png)

- 缺点：因为真正地执行 DQL，所以速度慢；数据没有格式化，需要我们手动格式化。
- 优点：数据项满足我们的要求。

## 4.3 mysql profile

- 语法：

    set profiling = 1;
    \<DQL\>
    show profile for query 1;

- 例如：

    set profiling = 1;
    select * from VIEWS;
    show profile for query 1;

- 结果：

    ![mysql profile](./Images/mysql_profile.png)

- 缺点：因为真正地执行 DQL，所以速度慢；数据项不够。
- 优点：有具体的执行时间；数据已经格式化了。

# 5. 数据库通用接口

## 5.1 JDBC

java 的通用 dbms 接口协议。

## 5.2 ODBC

除了 java 的，C++ 的实现最好。

考虑到与 calcite 结合，所以使用 JDBC。

# 6. 可视化框架

会采用原生 java 的框架，javafx

为了与 calcite 的结合；

# 7. 目前的效果

## 1月16日更新

```cpp
-> Nested loop left join  (cost=0.70 rows=1)
    -> Table scan on t1  (cost=0.35 rows=1)
    -> Filter: (t3.b is null)  (cost=0.70 rows=1)
        -> Nested loop left join  (cost=0.70 rows=1)
            -> Table scan on t2  (cost=0.35 rows=1)
            -> Filter: (t3.a = t2.a)  (cost=0.35 rows=1)
                -> Table scan on t3  (cost=0.35 rows=1)
```

![demo](./Images/demo.png)

## 4月5日更新

1. 连接数据库界面

   ![connect DB stage](./Images/connect_DB_stage.png)

   - 数据库连接成功时，进入主界面
   - 数据库连接失败时，弹出错误提示框

     ![connect DB failed alert](./Images/connect_DB_failed_alert.png)

2. 主界面

   主界面初始状态没有查询执行计划，左侧显示当前数据库连接信息，以及待查询的SQL语句，右侧为空白。

   ![main stage](./Images/main_stage.png)

   - 断开数据库
  
     点击“断开数据库”按钮，将会断开当前数据库，返回至连接数据库界面
     ![disconnect DB](./Images/disconnect_DB.png)

   - 查询执行计划

     点击“查询执行计划”按钮，将对当前SQL语句进行查询，显示在右侧，可重复编辑SQL语句进行多次查询
     ![query execution plan](./Images/query_execution_plan.png)

## 4月8日更新

1. 查询SQL语句有误时增加了错误提示框
   ![query execution plan failed alert](./Images/query_execution_plan_failed_alert.png)

2. 优化主界面查询执行计划树的显示效果
   ![optimize the display of visual plan tree](./Images/optimize_the_display_of_visual_plan_tree.png)

## 4月12日更新

1. 进一步优化主界面查询执行计划树的显示效果
   ![optimize the display of visual plan tree 2](./Images/optimize_the_display_of_visual_plan_tree_2.png)

## 4月26日更新

1. 支持PostgreSQL查询
2. 修改数据库连接界面、主界面的部分字体大小，优化显示效果
   ![queries based on PostgreSQL 1](./Images/queries_based_on_PostgreSQL_1.png)
   ![queries based on PostgreSQL 2](./Images/queries_based_on_PostgreSQL_2.png)

## 5月3日更新

1. PostgreSQL查询可以针对不同operator显示更多信息，包括结点类型等；
2. 针对PostgreSQL需要显示的内容对结点的长宽进行了优化，与MySQL结点的长宽进行了区分。
   ![queries based on PostgreSQL 3](./Images/queries_based_on_PostgreSQL_3.png)

## 5月8日更新

1. 可以可视化Calcite优化后的查询执行计划
   ![queries based on Calcite](./Images/queries_based_on_Calcite.png)

## 5月14日更新

1. 使用JFoenix优化Button、Combox、Dialog等控件的显示效果；
2. 对部分控件使用css样式进行修饰；
   ![optimize the display based on JFoenix and css 1](./Images/optimize_the_display_based_on_JFoenix_and_css_1.png)
   ![optimize the display based on JFoenix and css 2](./Images/optimize_the_display_based_on_JFoenix_and_css_2.png)
3. 基于JFoenix新增一个工具类DialogBuilder用于快速构建弹框。
   ![new class to quickly build dialog](./Images/new_class_to_quickly_build_dialog.png)

## 5月15日更新

1. 进一步使用JFoenix控件优化界面显示效果；
   ![optimize the display based on JFoenix and css 3](./Images/optimize_the_display_based_on_JFoenix_and_css_3.png)
2. 新增切换“缩略模式”和“详细模式”的功能；
   ![thumbnail mode](./Images/thumbnail_mode.png)
3. 优化Calcite查询的显示效果。
   ![optimize the display of calcite visual plan tree](./Images/optimize_the_display_of_calcite_visual_plan_tree.png)

## 5月29日更新

1. 新增“连接新数据库”功能；
   ![connect new DBMS 1](./Images/connect_new_DBMS_1.png)
   ![connect new DBMS 2](./Images/connect_new_DBMS_2.png)
2. VisualPlanTreeGeneratorFactory部分代码优化；
3. MainStage.fxml部分代码优化。

## 6月1日更新1

1. 新增点击每个节点可以在主界面右侧显示节点详细信息的功能；
   ![display node detail](./Images/display_node_detail.png)
2. 增加了MySQL的AccessPath类型；
3. 增加了Calcite的AccessPath类型；
4. 调整了drowNode函数。

## 6月1日更新2

1. SQL查询出错时弹出对话框。
   ![pop a dialog when an SQL query fails](./Images/pop_a_dialog_when_an_SQL_query_fails.png)

## 6月16日更新

1. 添加TPC-H测试.txt文件，里边记录了所有TPC-H的22个查询语句。最后一次提交了，毕设完成了。

# 8. 以后的任务

## 1月16日新增

1. 未来在可视化中会强调某些操作的重要性。

    - 经过讨论，可能会根据操作的耗时来决定结点的显示大小或者颜色。
2. 如何测试？
    - 打算采用 MySQL 官方的测试的文档。这样能保证结果正确性与分支完整性。
    - 文档在`源码目录/mysql-test/r/*.result`
      ![test](./Images/test.png)

## 4月5日新增

1. 除了在界面中显示执行计划每个operator的信息，还要将整体信息汇总显示，初步计划在左下角SQL文本框下方显示。此部分需要研究有哪些信息可以显示出来以及如何汇总这些信息。
2. 点击“查询执行计划”按钮后，若SQL查询有问题，目前没有错误提示，需要新增一个弹框。（4月8日更新已解决）
3. 目前可视化查询执行计划的每个operator中显示的内容过多，需要精简，同时调整每个RectangleField的尺寸。（4月8日更新已解决，后续有变化再调整）

## 4月12日新增

1. 编写PostgreSQL的后端解析部分，编写PostgreSQL的前端接口。(部分完成)

## 4月26日新增

1. 编写每一个PostgreSQL的operator的解析。（部分完成，没找到官方文档，在网上找的帖子中写的operator）
2. 将MySQL和PostgreSQL的查询用Calcite进行优化，编写Calcite的后端解析部分。（Jdbc部分完成，等待后续测试看是否要加Enumerable部分）

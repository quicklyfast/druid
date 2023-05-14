package com.alibaba.druid.sql.dialect.doris.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.dialect.doris.ast.statement.DorisCreateResourceStatement;
import com.alibaba.druid.sql.dialect.doris.ast.statement.DorisCreateTableStatement;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import junit.framework.TestCase;

public class DorisStatementParserTest extends TestCase {

    public void testParseCreateResourceStatement() {
        String ddl = "CREATE EXTERNAL RESOURCE `oracle_odbc`\n" +
                "PROPERTIES (\n" +
                "\"type\" = \"odbc_catalog\",\n" +
                "\"host\" = \"192.168.0.1\",\n" +
                "\"port\" = \"8086\",\n" +
                "\"user\" = \"test\",\n" +
                "\"password\" = \"test\",\n" +
                "\"database\" = \"test\",\n" +
                "\"odbc_type\" = \"oracle\",\n" +
                "\"driver\" = \"Oracle 19 ODBC driver\"\n" +
                ");";
        DorisStatementParser parser = new DorisStatementParser(ddl);
        DorisCreateResourceStatement stmt = (DorisCreateResourceStatement) parser.parseCreate();

        assertEquals(new SQLIdentifierExpr("oracle_odbc"), stmt.getName());

        assertEquals(true, stmt.isExternal());

        SQLAssignItem type = new SQLAssignItem(
                new SQLCharExpr("type"),
                new SQLCharExpr("odbc_catalog")
        );

        assertEquals(
                type,
                stmt.getProperties().getProperties().get(0)
        );

        System.out.println(
                stmt.toString(VisitorFeature.OutputPrettyFormat)
        );
    }

    public void testParseCreateTableStatement() {
        String ddl = "CREATE TABLE example_db.table_hash\n" +
                "(\n" +
                "    k1 DATE,\n" +
                "    k2 DECIMAL(10, 2) DEFAULT \"10.5\",\n" +
                "    k3 CHAR(10) COMMENT \"string column\",\n" +
                "    k4 INT NOT NULL DEFAULT \"1\" COMMENT \"int column\"\n" +
                ")\n" +
                "DUPLICATE KEY(k1, k2)\n" +
                "COMMENT \"my first table\"\n" +
                "PARTITION BY RANGE(k1)\n" +
                "(\n" +
                "    PARTITION p1 VALUES LESS THAN (\"2020-02-01\"),\n" +
                "    PARTITION p2 VALUES LESS THAN (\"2020-03-01\"),\n" +
                "    PARTITION p3 VALUES LESS THAN (\"2020-04-01\")\n" +
                ")\n" +
                "DISTRIBUTED BY HASH(k1) BUCKETS 32\n" +
                "PROPERTIES (\n" +
                "    \"replication_num\" = \"1\"\n" +
                ") as select * from test limit 10;";

        DorisStatementParser parser = new DorisStatementParser(ddl);
//        DorisCreateTableStatement stmt = (DorisCreateTableStatement) SQLUtils.parseSingleStatement(ddl, DbType.doris);
        DorisCreateTableStatement stmt = (DorisCreateTableStatement) parser.parseCreateTable();
        System.out.println(stmt.toString());
    }

    public void testMysql() {
        String ddl = "(a, b, c) in ((1,2,3), (4,5,6))";
        SQLExpr expr = SQLUtils.toSQLExpr(ddl, DbType.mysql);
        System.out.println(expr);
    }
}
package com.alibaba.druid.sql.dialect.doris.parser;

import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.dialect.doris.ast.statement.DorisCreateResourceStatement;
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
}
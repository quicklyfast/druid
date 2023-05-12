package com.alibaba.druid.sql.dialect.doris.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.dialect.doris.ast.expr.DorisPropertiesExpr;
import junit.framework.TestCase;

public class DorisExprParserTest extends TestCase {

    public void testParseProperties() {
        String exprStr = "PROPERTIES\n" +
                "(\n" +
                "   \"type\" = \"s3\",\n" +
                "   \"AWS_ENDPOINT\" = \"bj.s3.com\",\n" +
                "   \"AWS_REGION\" = \"bj\",\n" +
                "   \"AWS_ACCESS_KEY\" = \"bbb\",\n" +
                "   \"AWS_SECRET_KEY\" = \"aaaa\",\n" +
                "   \"AWS_ROOT_PATH\" = \"/path/to/root\",\n" +
                "   \"AWS_BUCKET\" = \"test-bucket\",\n" +
                ");";
        DorisExprParser parser = new DorisExprParser(exprStr);

        DorisPropertiesExpr props = parser.parseProperties();
        System.out.println(SQLUtils.toSQLString(props, DbType.doris, new SQLUtils.FormatOption(false, true)));
    }
}
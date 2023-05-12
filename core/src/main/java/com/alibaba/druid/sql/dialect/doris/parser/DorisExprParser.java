package com.alibaba.druid.sql.dialect.doris.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.dialect.doris.ast.expr.DorisPropertiesExpr;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.FnvHash;

import java.util.Arrays;

public class DorisExprParser extends SQLExprParser {
    public static final String[] AGGREGATE_FUNCTIONS;

    public static final long[] AGGREGATE_FUNCTIONS_CODES;

    static {
        String[] strings = {
                "SUM",
                "MAX",
                "MIN",
                "REPLACE",
                "HLL_UNION",
                "BITMAP_UNION",
                "REPLACE_IF_NOT_NULL",
        };

        AGGREGATE_FUNCTIONS_CODES = FnvHash.fnv1a_64_lower(strings, true);
        AGGREGATE_FUNCTIONS = new String[AGGREGATE_FUNCTIONS_CODES.length];
        for (String str : strings) {
            long hash = FnvHash.fnv1a_64_lower(str);
            int index = Arrays.binarySearch(AGGREGATE_FUNCTIONS_CODES, hash);
            AGGREGATE_FUNCTIONS[index] = str;
        }
    }

    public DorisExprParser(String sql) {
        this(new DorisLexer(sql));
        this.lexer.nextToken();
    }

    public DorisExprParser(String sql, DbType dbType, SQLParserFeature... features) {
        super(sql, dbType, features);
    }

    public DorisExprParser(Lexer lexer) {
        super(lexer, DbType.doris);
        this.aggregateFunctions = AGGREGATE_FUNCTIONS;
        this.aggregateFunctionHashCodes = AGGREGATE_FUNCTIONS_CODES;
    }

    public DorisExprParser(String sql, boolean keepComments) {
        this(new DorisLexer(sql, true, keepComments));
        this.lexer.nextToken();
    }

    public DorisExprParser(String sql, boolean skipComment, boolean keepComments) {
        this(new DorisLexer(sql, skipComment, keepComments));
        this.lexer.nextToken();
    }

    public DorisExprParser(Lexer lexer, DbType dbType) {
        super(lexer, dbType);
    }

    public DorisExprParser(String sql, SQLParserFeature... features) {
        super(new DorisLexer(sql, features), DbType.doris);
        this.lexer.nextToken();
    }

    public DorisPropertiesExpr parseProperties() {
        DorisPropertiesExpr propertiesExpr = new DorisPropertiesExpr();

        accept(Token.PROPERTIES);
        accept(Token.LPAREN);
        for (;;) {
            if (Token.RPAREN == lexer.token()) {
                break;
            }
            propertiesExpr.addProperties(parseAssignItem(true, propertiesExpr));

            if (Token.COMMA == lexer.token()) {
                accept(Token.COMMA);
            }
        }

        accept(Token.RPAREN);
        return propertiesExpr;
    }
}

package com.alibaba.druid.sql.dialect.doris.parser;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.doris.ast.expr.DorisPropertiesExpr;
import com.alibaba.druid.sql.dialect.doris.ast.statement.DorisCreateResourceStatement;
import com.alibaba.druid.sql.parser.*;
import com.alibaba.druid.util.FnvHash;

public class DorisStatementParser extends SQLStatementParser {
    public DorisStatementParser(String sql) {
        super(new DorisExprParser(sql));
    }

    public DorisStatementParser(String sql, SQLParserFeature... features) {
        super(new DorisExprParser(sql, features));
    }

    public DorisStatementParser(String sql, boolean keepComments) {
        super(new DorisExprParser(sql, keepComments));
    }

    public DorisStatementParser(String sql, boolean skipComment, boolean keepComments) {
        super(new DorisExprParser(sql, skipComment, keepComments));
    }

    public DorisStatementParser(Lexer lexer) {
        super(new DorisExprParser(lexer));
    }

    public SQLCreateTableParser getSQLCreateTableParser() {
        throw new RuntimeException();
    }

    public DorisExprParser getDorisExprParser() {
        return (DorisExprParser) this.exprParser;
    }

    @Override
    public SQLStatement parseCreate() {
        Lexer.SavePoint savePoint = lexer.markOut();
        lexer.nextToken();

        // create external source
        if (lexer.identifierEquals(FnvHash.Constants.EXTERNAL)) {
            acceptIdentifier("EXTERNAL");
        }

        if (lexer.identifierEquals(FnvHash.Constants.RESOURCE)) {
            lexer.reset(savePoint);
            return parseCreateResourceStatement();
        }

        lexer.reset(savePoint);
        return super.parseCreate();
    }

    private DorisCreateResourceStatement parseCreateResourceStatement() {
        DorisCreateResourceStatement stmt = new DorisCreateResourceStatement();
        accept(Token.CREATE);
        // create external source
        if (lexer.identifierEquals(FnvHash.Constants.EXTERNAL)) {
            acceptIdentifier("EXTERNAL");
            stmt.setExternal(true);
        }

        acceptIdentifier("RESOURCE");

        DorisExprParser exprParser = this.getDorisExprParser();
        stmt.setName(exprParser.name());

        DorisPropertiesExpr properties = exprParser.parseProperties();
        stmt.setProperties(properties);

        return stmt;
    }

}

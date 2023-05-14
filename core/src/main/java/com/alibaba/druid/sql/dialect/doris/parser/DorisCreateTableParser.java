package com.alibaba.druid.sql.dialect.doris.parser;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.dialect.doris.ast.statement.DorisCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlSelectParser;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.FnvHash;


public class DorisCreateTableParser extends SQLCreateTableParser {
    public DorisCreateTableParser(Lexer lexer) {
        super(new DorisExprParser(lexer));
    }

    public DorisCreateTableParser(String sql) {
        super(new DorisExprParser(sql));
    }

    public DorisCreateTableParser(SQLExprParser exprParser) {
        super(exprParser);
    }

    public void parseCreateTableRest(SQLCreateTableStatement stmt) {
        DorisCreateTableStatement dstmt = (DorisCreateTableStatement) stmt;

        if (lexer.identifierEquals(FnvHash.Constants.ENGINE)) {
            acceptIdentifier("ENGINE");
            if (lexer.token() == Token.EQ) {
                accept(Token.EQ);
            }
            stmt.setEngine(
                    this.exprParser.expr()
            );
        }

        if (lexer.identifierEquals(FnvHash.Constants.DUPLICATE)
                || lexer.identifierEquals(FnvHash.Constants.AGGREGATE)
                || lexer.identifierEquals(FnvHash.Constants.UNIQUE)
                || lexer.identifierEquals(FnvHash.Constants.PRIMARY)) {
            String modelName = lexer.stringVal();
            lexer.nextToken();
            accept(Token.KEY);
            SQLIndexDefinition model = new SQLIndexDefinition();
            model.setType(modelName);
            model.setKey(true);
            this.exprParser.parseIndexRest(model, dstmt);
            dstmt.setModel(model);
        }

        if (lexer.token() == Token.COMMENT) {
            accept(Token.COMMENT);
            dstmt.setComment(new SQLCharExpr(SQLUtils.normalize(lexer.stringVal())));
            lexer.nextToken();
        }


        if (lexer.token() == Token.PARTITION) {
            dstmt.setPartitioning(parsePartitionBy());
        }

        if (lexer.identifierEquals(FnvHash.Constants.DISTRIBUTED)) {
            lexer.nextToken();
            accept(Token.BY);
            SQLName hash = this.exprParser.name();
            dstmt.setDistributeByType(hash);
            accept(Token.LPAREN);
            for (; ; ) {
                if (lexer.token() == Token.RPAREN) {
                    break;
                }
                dstmt.addDistributeBy(exprParser.name());
                if (lexer.token() == Token.COMMA) {
                    accept(Token.COMMA);
                }
            }
            accept(Token.RPAREN);
            if (lexer.identifierEquals(FnvHash.Constants.BUCKETS)) {
                lexer.nextToken();
                int bucket = lexer.integerValue().intValue();
                stmt.setBuckets(bucket);
                lexer.nextToken();
            }
        }

        if (lexer.token() == Token.PROPERTIES) {
            dstmt.setPropertiesExpr(((DorisExprParser) exprParser).parseProperties());
        }

        if (lexer.token() == Token.AS) {
            accept(Token.AS);
            SQLSelect query = new MySqlSelectParser(this.exprParser).select();
            dstmt.setSelect(query);
        }
    }

    @Override
    public SQLPartitionBy parsePartitionBy() {
        accept(Token.PARTITION);
        accept(Token.BY);
        acceptIdentifier("RANGE");
        accept(Token.LPAREN);
        SQLPartitionByRange partitioning = new SQLPartitionByRange();
        for (; ; ) {
            if (lexer.token() == Token.RPAREN) {
                break;
            }
            partitioning.addColumn(exprParser.expr());
            if (lexer.token() == Token.COMMA) {
                accept(Token.COMMA);
            }
        }

        accept(Token.RPAREN);

        accept(Token.LPAREN);

        for (; ; ) {
            if (lexer.token() == Token.RPAREN) {
                break;
            }
            if (Token.PARTITION == lexer.token()) {
                accept(Token.PARTITION);
                SQLPartition partitionDef = new SQLPartition();
                partitionDef.setName(exprParser.name());
                partitionDef.setValues(exprParser.parsePartitionValues());
                partitioning.addPartition(partitionDef);
            } else {
                accept(Token.FROM);
            }

            if (lexer.token() == Token.COMMA) {
                accept(Token.COMMA);
            }
        }

        accept(Token.RPAREN);

        return partitioning;
    }

    protected DorisCreateTableStatement newCreateStatement() {
        return new DorisCreateTableStatement(dbType);
    }

}

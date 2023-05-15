package com.alibaba.druid.sql.dialect.doris.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.doris.ast.statement.DorisCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlSelectParser;
import com.alibaba.druid.sql.parser.*;
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

    public SQLCreateTableStatement parseCreateTable(boolean acceptCreate) {
        SQLCreateTableStatement createTable = newCreateStatement();
        createTable.setDbType(getDbType());

        if (acceptCreate) {
            if (lexer.hasComment() && lexer.isKeepComments()) {
                createTable.addBeforeComment(lexer.readAndResetComments());
            }

            accept(Token.CREATE);
        }

        if (lexer.identifierEquals(FnvHash.Constants.EXTERNAL)) {
            lexer.nextToken();
            createTable.setExternal(true);
        }

        accept(Token.TABLE);

        if (lexer.token() == Token.IF || lexer.identifierEquals("IF")) {
            lexer.nextToken();
            accept(Token.NOT);
            accept(Token.EXISTS);

            createTable.setIfNotExiists(true);
        }

        createTable.setName(this.exprParser.name());

        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();

            for (; ; ) {
                Token token = lexer.token();
                if (lexer.identifierEquals(FnvHash.Constants.SUPPLEMENTAL)
                        && DbType.oracle == dbType) {
                    SQLTableElement element = this.parseCreateTableSupplementalLogingProps();
                    element.setParent(createTable);
                    createTable.getTableElementList().add(element);
                } else if (token == Token.IDENTIFIER //
                        || token == Token.LITERAL_ALIAS) {
                    SQLColumnDefinition column = this.exprParser.parseColumn(createTable);
                    column.setParent(createTable);
                    createTable.getTableElementList().add(column);
                } else if (token == Token.PRIMARY //
                        || token == Token.UNIQUE //
                        || token == Token.CHECK //
                        || token == Token.CONSTRAINT
                        || token == Token.FOREIGN) {
                    SQLConstraint constraint = this.exprParser.parseConstaint();
                    constraint.setParent(createTable);
                    createTable.getTableElementList().add((SQLTableElement) constraint);
                } else if (token == Token.TABLESPACE) {
                    throw new ParserException("TODO " + lexer.info());
                } else {
                    SQLColumnDefinition column = this.exprParser.parseColumn();
                    createTable.getTableElementList().add(column);
                }

                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();

                    if (lexer.token() == Token.RPAREN) { // compatible for sql server
                        break;
                    }
                    continue;
                }

                break;
            }

            accept(Token.RPAREN);

            if (lexer.identifierEquals(FnvHash.Constants.INHERITS)) {
                lexer.nextToken();
                accept(Token.LPAREN);
                SQLName inherits = this.exprParser.name();
                createTable.setInherits(new SQLExprTableSource(inherits));
                accept(Token.RPAREN);
            }
        }

        parseCreateTableRest(createTable);

        return createTable;
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

            if (lexer.identifierEquals("HASH")) {
                SQLName distributeByType = this.exprParser.name();
                dstmt.setDistributeByType(distributeByType);
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
            } else if (lexer.identifierEquals("RANDOM")) {
                SQLName distributeByType = this.exprParser.name();
                dstmt.setDistributeByType(distributeByType);
            } else {
                throw new ParserException("syntax error, expect RANDOM or HASH, actual " + lexer.token() + ", " + lexer.info());
            }

            if (lexer.identifierEquals(FnvHash.Constants.BUCKETS)) {
                lexer.nextToken();
                SQLExpr bucket = exprParser.expr();
                ((DorisCreateTableStatement) stmt).setBucket(bucket);
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

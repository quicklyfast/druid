package com.alibaba.druid.sql.dialect.doris.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLIndexDefinition;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLPartitionBy;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.doris.ast.expr.DorisPropertiesExpr;
import com.alibaba.druid.sql.dialect.doris.ast.statement.DorisCreateResourceStatement;
import com.alibaba.druid.sql.dialect.doris.ast.statement.DorisCreateTableStatement;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

import java.util.List;
import java.util.Locale;

public class DorisOutputVisitor extends SQLASTOutputVisitor implements DorisASTVisitor {
    {
        this.dbType = DbType.doris;
        this.shardingSupport = true;
        this.quote = '`';
    }

    public DorisOutputVisitor(Appendable appender) {
        super(appender);
    }

    public DorisOutputVisitor(Appendable appender, DbType dbType) {
        super(appender, dbType);
    }

    public DorisOutputVisitor(Appendable appender, boolean parameterized) {
        super(appender, parameterized);
    }

    @Override
    public boolean visit(DorisPropertiesExpr x) {
        print0(ucase ? "PROPERTIES " : "properties ");
        incrementIndent();
        println("(");
        List<SQLAssignItem> props = x.getProperties();
        for (int i = 0, size = props.size(); i < size; ++i) {
            if (i != 0) {
                println(",");
            }
            printExpr(props.get(i));
        }
        decrementIndent();
        println();
        print(")");
        return true;
    }

    @Override
    public boolean visit(DorisCreateResourceStatement x) {
        print0(ucase ? "CREATE " : "create ");

        if (x.isExternal()) {
            print0(ucase ? "EXTERNAL " : "external ");
        }

        print0(ucase ? "RESOURCE " : "resource ");

        x.getName().accept(this);

        print(' ');

        x.getProperties().accept(this);

        return true;
    }

    public boolean visit(SQLColumnDefinition x) {
        super.visit(x);
        if (x.getAggType() != null) {
            print(' ');
            print0(ucase ? x.getAggType().getText().toUpperCase(Locale.ROOT) : x.getAggType().getText().toLowerCase(Locale.ROOT));
        }
        if (x.getBitmap() != null) {
            print(' ');
            print0(ucase ? "USING " : "using ");
            print0(ucase ? x.getBitmap().getText().toUpperCase(Locale.ROOT) : x.getBitmap().getText().toLowerCase(Locale.ROOT));
        }
        if (x.getIndexComment() != null) {
            print(' ');
            print0(ucase ? "COMMENT " : "comment ");
            x.getIndexComment().accept(this);
        }
        return false;
    }

    @Override
    public boolean visit(SQLCreateTableStatement x) {
        return visit((DorisCreateTableStatement) x);
    }

    protected void printCreateTable(SQLCreateTableStatement x, boolean printSelect) {
        print0(ucase ? "CREATE " : "create ");

        if (x.isExternal()) {
            print0(ucase ? "EXTERNAL " : "external ");
        }

        final SQLCreateTableStatement.Type tableType = x.getType();
        if (SQLCreateTableStatement.Type.GLOBAL_TEMPORARY.equals(tableType)) {
            print0(ucase ? "GLOBAL TEMPORARY " : "global temporary ");
        } else if (SQLCreateTableStatement.Type.LOCAL_TEMPORARY.equals(tableType)) {
            print0(ucase ? "LOCAL TEMPORARY " : "local temporary ");
        } else if (SQLCreateTableStatement.Type.SHADOW.equals(tableType)) {
            print0(ucase ? "SHADOW " : "shadow ");
        }

        if (x.isDimension()) {
            print0(ucase ? "DIMENSION " : "dimension ");
        }

        print0(ucase ? "TABLE " : "table ");

        if (x.isIfNotExists()) {
            print0(ucase ? "IF NOT EXISTS " : "if not exists ");
        }

        printTableSourceExpr(x.getName());

        printTableElements(x.getTableElementList());

        SQLExprTableSource inherits = x.getInherits();
        if (inherits != null) {
            print0(ucase ? " INHERITS (" : " inherits (");
            inherits.accept(this);
            print(')');
        }

        SQLExpr storedAs = x.getStoredAs();
        if (storedAs != null) {
            print0(ucase ? " STORE AS " : " store as ");
            printExpr(storedAs, parameterized);
        }
    }

    public boolean visit(DorisCreateTableStatement x) {
        printCreateTable(x, true);

        SQLExpr engine = x.getEngine();
        if (engine != null) {
            print0(ucase ? " ENGINE = " : " engine = ");
            engine.accept(this);
        }

        SQLIndexDefinition model = x.getModel();
        if (model != null) {
            println();
            model.accept(this);
        }

        SQLExpr comment = x.getComment();
        if (comment != null) {
            println();
            print0(ucase ? "COMMENT " : "comment ");
            comment.accept(this);
        }

        SQLPartitionBy partitionBy = x.getPartitioning();
        if (partitionBy != null) {
            println();
            print0(ucase ? "PARTITION BY " : "partition by ");
            partitionBy.accept(this);
        }

        if (x.getDistributeByType() != null) {
            println();
            print0(ucase ? "DISTRIBUTED BY " : "distributed by ");

            SQLName distributeByType = x.getDistributeByType();

            if ("HASH".equalsIgnoreCase(distributeByType.getSimpleName())) {
                print0(ucase ? "HASH(" : "hash(");
                printAndAccept(x.getDistributeBy(), ",");
                print0(")");
            } else if ("RANDOM".equalsIgnoreCase(distributeByType.getSimpleName())) {
                print0(ucase ? "RANDOM" : "random");
            }

            SQLExpr bucket = x.getBucket();
            if (bucket != null) {
                print0(ucase ? " BUCKETS " : " buckets ");
                bucket.accept(this);
            }
        }

        DorisPropertiesExpr properties = x.getPropertiesExpr();
        if (properties != null) {
            println();
            properties.accept(this);
        }

        SQLSelect select = x.getSelect();
        if (select != null) {
            println();
            print0(ucase ? "AS" : "as");

            println();
            visit(select);
        }

        return false;
    }
}

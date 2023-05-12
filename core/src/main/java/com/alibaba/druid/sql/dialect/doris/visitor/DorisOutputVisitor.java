package com.alibaba.druid.sql.dialect.doris.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.dialect.doris.ast.expr.DorisPropertiesExpr;
import com.alibaba.druid.sql.dialect.doris.ast.statement.DorisCreateResourceStatement;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

import java.util.List;

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
}

package com.alibaba.druid.sql.dialect.doris.visitor;

import com.alibaba.druid.sql.dialect.doris.ast.expr.DorisPropertiesExpr;
import com.alibaba.druid.sql.dialect.doris.ast.statement.DorisCreateResourceStatement;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface DorisASTVisitor extends SQLASTVisitor {
    default boolean visit(DorisPropertiesExpr x) {
        return true;
    }

    default void endVisit(DorisPropertiesExpr x) {
    }

    default boolean visit(DorisCreateResourceStatement x) {
        return true;
    }

    default void endVisit(DorisCreateResourceStatement x) {
    }
}

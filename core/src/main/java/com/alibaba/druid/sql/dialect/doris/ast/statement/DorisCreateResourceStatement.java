package com.alibaba.druid.sql.dialect.doris.ast.statement;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLCreateStatement;
import com.alibaba.druid.sql.ast.statement.SQLDDLStatement;
import com.alibaba.druid.sql.dialect.doris.ast.expr.DorisPropertiesExpr;
import com.alibaba.druid.sql.dialect.doris.visitor.DorisASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class DorisCreateResourceStatement extends SQLStatementImpl implements SQLDDLStatement, SQLCreateStatement {
    private SQLName name;
    private DorisPropertiesExpr properties;
    private boolean external;

    public DorisCreateResourceStatement() {
        this(DbType.doris);
    }

    public DorisCreateResourceStatement(DbType dbType) {
        super(dbType);
    }

    @Override
    public SQLName getName() {
        return name;
    }

    public void setName(SQLName name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = new SQLIdentifierExpr(name);
        this.name.setParent(this);
    }

    public DorisPropertiesExpr getProperties() {
        return properties;
    }

    public void setProperties(DorisPropertiesExpr properties) {
        properties.setParent(parent);
        this.properties = properties;
    }

    public boolean isExternal() {
        return external;
    }

    public void setExternal(boolean external) {
        this.external = external;
    }

    @Override
    protected void accept0(SQLASTVisitor v) {
        if (v instanceof DorisASTVisitor) {
            DorisASTVisitor vv = (DorisASTVisitor) v;
            vv.visit(this);
            vv.endVisit(this);
        } else {
            super.accept0(v);
        }
    }
}

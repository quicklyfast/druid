package com.alibaba.druid.sql.dialect.doris.ast.expr;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.dialect.doris.visitor.DorisASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class DorisPropertiesExpr extends SQLExprImpl implements DorisExpr, SQLReplaceable, SQLDbTypedObject {
    private List<SQLAssignItem> properties = new ArrayList<>();
    protected DbType dbType;

    public DorisPropertiesExpr() {
        this(DbType.doris);
    }

    public DorisPropertiesExpr(DbType dbType) {
        this.dbType = dbType;
    }

    public List<SQLAssignItem> getProperties() {
        return properties;
    }

    public void setProperties(List<SQLAssignItem> properties) {
        this.properties = properties;
    }

    public void addProperties(String target, String value) {
        addProperties(new SQLIdentifierExpr(target), new SQLCharExpr(value));
    }

    public void addProperties(SQLIdentifierExpr target, SQLCharExpr value) {
        properties.add(
                new SQLAssignItem(target, value)
        );
    }

    public void addProperties(SQLAssignItem x) {
        if (x != null) {
            x.setParent(this);
        }

        properties.add(x);
    }

    @Override
    public DbType getDbType() {
        return dbType;
    }

    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {
        int length = properties.size();
        for (int i = 0; i < length; i++) {
            SQLAssignItem assignItem = properties.get(i);
            if (assignItem == expr) {
                target.setParent(this);
                properties.set(i, (SQLAssignItem) target);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DorisPropertiesExpr that = (DorisPropertiesExpr) o;

        return properties != null ? properties.equals(that.properties) : that.properties == null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + properties.hashCode();
        return result;
    }

    @Override
    public SQLExpr clone() {
        DorisPropertiesExpr expr = new DorisPropertiesExpr();
        List<SQLAssignItem> props = new ArrayList<>();
        for (SQLAssignItem item: getProperties()) {
            props.add(item.clone());
        }
        expr.properties = props;
        return expr;
    }

    @Override
    protected void accept0(SQLASTVisitor v) {
        ((DorisASTVisitor)v).visit(this);
    }

    @Override
    public void output(StringBuffer buf) {
        super.output(buf);
    }

    @Override
    public void output(Appendable buf) {
        super.output(buf);
    }
}

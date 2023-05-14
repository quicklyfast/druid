package com.alibaba.druid.sql.dialect.doris.ast.statement;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLIndexDefinition;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLCreateStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLDDLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.dialect.doris.ast.expr.DorisPropertiesExpr;

import java.util.ArrayList;
import java.util.List;

public class DorisCreateTableStatement extends SQLCreateTableStatement implements SQLDDLStatement, SQLCreateStatement {
    protected DorisPropertiesExpr propertiesExpr;
    protected SQLIndexDefinition model;

    protected SQLName distributeByType;
    protected List<SQLName> distributeBy = new ArrayList<SQLName>();

    public DorisCreateTableStatement() {
        this(DbType.doris);
    }

    public DorisCreateTableStatement(DbType dbType) {
        super(dbType);
    }

    public DorisPropertiesExpr getPropertiesExpr() {
        return propertiesExpr;
    }

    public void setPropertiesExpr(DorisPropertiesExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.propertiesExpr = x;
    }

    public SQLIndexDefinition getModel() {
        return model;
    }

    public void setModel(SQLIndexDefinition x) {
        if (x != null) {
            x.setParent(this);
        }
        this.model = x;
    }

    public SQLName getDistributeByType() {
        return distributeByType;
    }

    public void setDistributeByType(SQLName distributeByType) {
        this.distributeByType = distributeByType;
    }

    public List<SQLName> getDistributeBy() {
        return distributeBy;
    }

    public void setDistributeBy(List<SQLName> distributeBy) {
        this.distributeBy = distributeBy;
    }

    public void addDistributeBy(SQLName x) {
        if (x != null) {
            x.setParent(this);
        }
        distributeBy.add(x);
    }

    @Override
    public SQLSelect getSelect() {
        return super.getSelect();
    }
}

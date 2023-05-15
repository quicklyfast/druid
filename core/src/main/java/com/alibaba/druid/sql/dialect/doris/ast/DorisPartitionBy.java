package com.alibaba.druid.sql.dialect.doris.ast;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLPartitionBy;
import com.alibaba.druid.sql.dialect.doris.visitor.DorisASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class DorisPartitionBy extends SQLPartitionBy implements DorisObject {
    protected DbType dbType;

    public DorisPartitionBy() {
        this(DbType.doris);
    }

    public DorisPartitionBy(DbType dbType) {
        this.dbType = dbType;
    }

    @Override
    public DbType getDbType() {
        return this.dbType;
    }

    @Override
    protected void accept0(SQLASTVisitor v) {
    }

    @Override
    public SQLPartitionBy clone() {
        return null;
    }

    @Override
    public void accept0(DorisASTVisitor visitor) {
    }
}

package com.alibaba.druid.sql.dialect.doris.ast;

import com.alibaba.druid.sql.ast.SQLDbTypedObject;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.dialect.doris.visitor.DorisASTVisitor;

public interface DorisObject extends SQLObject, SQLDbTypedObject {
    void accept0(DorisASTVisitor v);
}

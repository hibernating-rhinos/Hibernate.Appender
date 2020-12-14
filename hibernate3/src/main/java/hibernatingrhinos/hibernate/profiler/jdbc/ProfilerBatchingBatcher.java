//-----------------------------------------------------------------------
// <copyright file="ProfilerBatchingBatcher.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.jdbc;

import org.apache.log4j.NDC;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.jdbc.ConnectionManager;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProfilerBatchingBatcher extends AbstractProfilerBatcher {

    protected StringBuffer batchSql;

    public ProfilerBatchingBatcher(ConnectionManager connectionManager, Interceptor interceptor, boolean batch) {
        super(connectionManager, interceptor, batch);
        batchSql = new StringBuffer();
    }

    /**
     * @see org.hibernate.jdbc.AbstractBatcher#prepareBatchCallableStatement(java.lang.String)
     */
    public CallableStatement prepareBatchCallableStatement(String sql) throws SQLException, HibernateException {
        batchSql.append("Batch command: ").append(sql).append("\n");
    
        NDC.push("/* batch statement */");
        CallableStatement prepareBatchCallableStatement;
        try {
            prepareBatchCallableStatement = super.prepareBatchCallableStatement(sql);
        } finally {
            NDC.pop();
        }
        
        return prepareBatchCallableStatement;
    }

    /**
     * @see org.hibernate.jdbc.AbstractBatcher#prepareBatchStatement(java.lang.String)
     */
    public PreparedStatement prepareBatchStatement(String sql) throws SQLException, HibernateException {
        batchSql.append("Batch command: ").append(sql).append("\n");

        NDC.push("/* batch statement */");
        PreparedStatement prepareBatchStatement;
        try {
            prepareBatchStatement = super.prepareBatchStatement(sql);
        } finally {
            NDC.pop();
        }

        return prepareBatchStatement;
    }

    /**
     * @see org.hibernate.jdbc.BatchingBatcher#doExecuteBatch(java.sql.PreparedStatement)
     */
    @Override
    protected void doExecuteBatch(PreparedStatement ps) throws SQLException, HibernateException {
        if (log.isDebugEnabled()) {
            log.debug(batchSql.toString());
            batchSql = new StringBuffer();
        }
        super.doExecuteBatch(ps);
    }

    @Override
    protected void logSql(PreparedStatement statement) {
        //do nothing
    }

}

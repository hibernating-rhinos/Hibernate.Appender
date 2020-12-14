//-----------------------------------------------------------------------
// <copyright file="SpyBatchingBatcher.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.jdbc;

import net.sf.log4jdbc.hibernateprofiler.jdbc4.PreparedStatementSpy;
import org.apache.log4j.NDC;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.ScrollMode;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.jdbc.ConnectionManager;
import org.hibernate.jdbc.util.FormatStyle;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SpyBatchingBatcher extends AbstractProfilerBatcher {

    private SessionFactoryImplementor factory;
    private ThreadLocal<Boolean> executingBatch = new ThreadLocal<Boolean>();

    public SpyBatchingBatcher(ConnectionManager connectionManager, Interceptor interceptor, boolean batch) {
        super(connectionManager, interceptor, batch);
        factory = connectionManager.getFactory();
        executingBatch.set(false);
    }

    @Override
    public CallableStatement prepareCallableStatement(String sql) throws SQLException, HibernateException {
        NDC.push("/* spying */");
        try {
            return super.prepareCallableStatement(sql);
        } finally {
            NDC.pop();
        }
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException, HibernateException {
        NDC.push("/* spying */");
        try {
            return super.prepareStatement(sql);
        } finally {
            NDC.pop();
        }
    }

    @Override
    public PreparedStatement prepareStatement(String sql, boolean getGeneratedKeys) throws SQLException, HibernateException {
        NDC.push("/* spying */");
        try {
            return super.prepareStatement(sql, getGeneratedKeys);
        } finally {
            NDC.pop();
        }
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException, HibernateException {
        NDC.push("/* spying */");
        try {
            return super.prepareStatement(sql, columnNames);
        } finally {
            NDC.pop();
        }
    }

    @Override
    public PreparedStatement prepareSelectStatement(String sql) throws SQLException, HibernateException {
        NDC.push("/* spying */");
        try {
            return super.prepareSelectStatement(sql);
        } finally {
            NDC.pop();
        }
    }

    @Override
    public PreparedStatement prepareQueryStatement(String sql, boolean scrollable, ScrollMode scrollMode) throws SQLException, HibernateException {
        NDC.push("/* spying */");
        try {
            return super.prepareQueryStatement(sql, scrollable, scrollMode);
        } finally {
            NDC.pop();
        }
    }

    @Override
    public CallableStatement prepareCallableQueryStatement(String sql, boolean scrollable, ScrollMode scrollMode) throws SQLException, HibernateException {
        NDC.push("/* spying */");
        try {
            return super.prepareCallableQueryStatement(sql, scrollable, scrollMode);
        } finally {
            NDC.pop();
        }            
    }

    @Override
    public PreparedStatement prepareBatchStatement(String sql) throws SQLException, HibernateException {
        NDC.push("/* spying */");
        try {
            return super.prepareBatchStatement(sql);
        } finally {
            NDC.pop();
        }
    }

    @Override
    public CallableStatement prepareBatchCallableStatement(String sql) throws SQLException, HibernateException {
        NDC.push("/* spying */");
        try {
            return super.prepareBatchCallableStatement(sql);
        } finally {
            NDC.pop();
        }
    }

    @Override
    public void closeStatement(PreparedStatement ps) throws SQLException {
        NDC.push("/* spying */");
        try {
            super.closeStatement(ps);
        } finally {
            NDC.pop();
        }

        if (!executingBatch.get()) logSql(ps);
    }

    @Override
    public void executeBatch() throws HibernateException {
        executingBatch.set(true);
        try {
            super.executeBatch();
        } finally {
            executingBatch.set(false);
        }
    }

     protected void doExecuteBatch(PreparedStatement ps) throws SQLException, HibernateException {
        onBatchExecuting(ps);
        super.doExecuteBatch(ps);
    }

    protected void onBatchExecuting(PreparedStatement ps) {
        if (!log.isDebugEnabled()) return;

        PreparedStatementSpy preparedStatementSpy = (PreparedStatementSpy)ps;
        StringBuilder batched = new StringBuilder();
        for (String sql : preparedStatementSpy.dumpBatch()) {
            batched.append("Batch command: ").append(sql).append("\n");
        }
        
        log.debug(batched.toString());
    }

    protected void logSql(PreparedStatement statement) {
        PreparedStatementSpy ps = (PreparedStatementSpy)statement;
        factory.getSettings().getSqlStatementLogger().logStatement(ps.adoNetStyleDumpedSql(), FormatStyle.NONE);
    }
}

//-----------------------------------------------------------------------
// <copyright file="AbstractProfilerBatcher.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.jdbc;

import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.jdbc.BatchingBatcher;
import org.hibernate.jdbc.ConnectionManager;
import org.hibernate.jdbc.Expectation;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class AbstractProfilerBatcher extends BatchingBatcher {

    private boolean batch;

    public AbstractProfilerBatcher(ConnectionManager connectionManager, Interceptor interceptor, boolean batch) {
        super(connectionManager, interceptor);
        this.batch = batch;
    }

    @Override
    public void addToBatch(Expectation expectation) throws SQLException, HibernateException {
        if (batch) {
            super.addToBatch(expectation);
            return;
        }

        PreparedStatement statement = getStatement();
        final int rowCount = statement.executeUpdate();
        logSql(statement);
        expectation.verifyOutcome(rowCount, statement, 0);
    }

    protected abstract void logSql(PreparedStatement statement);
}

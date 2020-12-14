//-----------------------------------------------------------------------
// <copyright file="AbstractProfilerBatcher.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.jdbc;

import java.sql.PreparedStatement;

import org.apache.log4j.NDC;
import org.hibernate.engine.jdbc.batch.internal.BatchingBatch;
import org.hibernate.engine.jdbc.batch.spi.BatchKey;
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractProfilerBatcher extends BatchingBatch {

    protected static final Logger log = LoggerFactory.getLogger("org.hibernate.jdbc.AbstractBatcher");
    private final String header;
    protected ProfilerBatchObserver observer;

    public AbstractProfilerBatcher(BatchKey key, JdbcCoordinator jdbcCoordinator, ProfilerBatchObserver observer, int batchSize, String header) {
        super(key, jdbcCoordinator, batchSize);
        this.header = header;
        this.observer = observer;
    }

    @Override
    public PreparedStatement getBatchStatement(String sql, boolean callable) {
        NDC.push(header);
        try {
            PreparedStatement pstmt = super.getBatchStatement(sql, callable);
            observer.addStatement(sql, pstmt);
            return pstmt;
        } finally {
            NDC.pop();
        }
    }
}

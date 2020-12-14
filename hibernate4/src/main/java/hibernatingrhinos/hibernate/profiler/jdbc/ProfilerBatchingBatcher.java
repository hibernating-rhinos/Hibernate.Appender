//-----------------------------------------------------------------------
// <copyright file="ProfilerBatchingBatcher.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.jdbc;

import java.sql.PreparedStatement;

import org.apache.log4j.NDC;
import org.hibernate.engine.jdbc.batch.spi.BatchKey;
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;


public class ProfilerBatchingBatcher extends AbstractProfilerBatcher {

    private static final String NDC_HEADER = "/* batch statement */";

    protected StringBuffer batchSql;

    public ProfilerBatchingBatcher(BatchKey key, JdbcCoordinator jdbcCoordinator,  int batchSize) {
        super(key, jdbcCoordinator, null, batchSize, "");
        batchSql = new StringBuffer();
    }

    @Override
    public PreparedStatement getBatchStatement(String sql, boolean callable) {
        batchSql.append("Batch command: ").append(sql).append("\n");
        NDC.push(NDC_HEADER);
        PreparedStatement result;
        try {
          result = super.getBatchStatement(sql, callable);
        } finally {
            NDC.pop();
        }
        return result;
    }

    @Override
    public void addToBatch() {
        super.addToBatch();
    }

    @Override
    protected void doExecuteBatch() {
        if (log.isDebugEnabled()) {
            log.debug(batchSql.toString());
            batchSql = new StringBuffer();
        }
        super.doExecuteBatch();
    }
}

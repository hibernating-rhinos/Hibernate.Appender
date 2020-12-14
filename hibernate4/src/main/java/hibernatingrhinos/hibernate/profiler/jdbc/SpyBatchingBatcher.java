//-----------------------------------------------------------------------
// <copyright file="SpyBatchingBatcher.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.jdbc;

import java.sql.PreparedStatement;

import org.apache.log4j.NDC;
import org.hibernate.engine.jdbc.batch.spi.BatchKey;
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
import org.hibernate.engine.spi.SessionFactoryImplementor;


public class SpyBatchingBatcher extends AbstractProfilerBatcher {

    private SessionFactoryImplementor factory;
    private ThreadLocal<Boolean> executingBatch = new ThreadLocal<Boolean>();

    private static final String NDC_HEADER = "/* spying */";

    public SpyBatchingBatcher(BatchKey key, JdbcCoordinator jdbcCoordinator,  int batchSize) {
        super(key, jdbcCoordinator, null, batchSize, "");
    }

    @Override
    public PreparedStatement getBatchStatement(String sql, boolean callable) {
        NDC.push(NDC_HEADER);
        PreparedStatement result;
        try {
          result = super.getBatchStatement(sql, callable);
        } finally {
            NDC.pop();
        }
        return result;
    }
 }

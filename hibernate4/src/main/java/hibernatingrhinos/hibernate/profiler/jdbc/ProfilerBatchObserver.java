//-----------------------------------------------------------------------
// <copyright file="ProfilerBatchingBatcherFactory.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.jdbc;

import java.sql.PreparedStatement;
import java.util.LinkedHashMap;
import java.util.Map;

import org.hibernate.engine.jdbc.batch.spi.BatchObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProfilerBatchObserver implements BatchObserver {

    protected static final Logger log = LoggerFactory.getLogger("org.hibernate.jdbc.AbstractBatcher");

    private Map<String, PreparedStatement> statements = new LinkedHashMap<String, PreparedStatement>();
    private StringBuffer batchSql = new StringBuffer();

    @Override
    public void batchExplicitlyExecuted() {
        process();
    }

    @Override
    public void batchImplicitlyExecuted() {
        process();
    }

    private void process() {
        if (log.isDebugEnabled()) {
            log.debug(batchSql.toString());
            batchSql = new StringBuffer();
        }
    }

    public void addStatement(String sql, PreparedStatement statement) {
        batchSql.append("Batch command: ").append(sql).append("\n");
        statements.put(sql, statement);
    }
}

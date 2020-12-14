//-----------------------------------------------------------------------
// <copyright file="ProfilerBatchingBatcherFactory.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.jdbc;

import java.util.Map;

import net.sf.log4jdbc.hibernateprofiler.jdbc4.ConnectionSpy;

import org.hibernate.cfg.Environment;
import org.hibernate.engine.jdbc.batch.spi.Batch;
import org.hibernate.engine.jdbc.batch.spi.BatchBuilder;
import org.hibernate.engine.jdbc.batch.spi.BatchKey;
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.service.spi.Configurable;
import org.jboss.logging.Logger;

public class ProfilerBatchingBatcherFactory implements BatchBuilder, Configurable {

    private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, ProfilerBatchingBatcherFactory.class.getName() );

    private int size;

    public ProfilerBatchingBatcherFactory() {
    }

    @Override
    public void configure(Map configurationValues) {
        size = ConfigurationHelper.getInt( Environment.STATEMENT_BATCH_SIZE, configurationValues, size );
    }

    public ProfilerBatchingBatcherFactory(int size) {
        this.size = size;
    }

    public void setJdbcBatchSize(int size) {
        this.size = size;
    }

    @Override
    public Batch buildBatch(BatchKey key, JdbcCoordinator jdbcCoordinator) {
        LOG.tracef( "Building batch [size=%s]", size );
        boolean useSpy = ConnectionSpy.class.getName().equals(jdbcCoordinator.getLogicalConnection().getConnection().getClass().getName());
        ProfilerBatchObserver observer = new ProfilerBatchObserver();
        AbstractProfilerBatcher result;
        if (useSpy) {
            result = new AbstractProfilerBatcher(key, jdbcCoordinator, observer, size, "/* spying */");
        } else {
            result = new AbstractProfilerBatcher(key, jdbcCoordinator, observer, size, "/* batch statement */");
        }
        result.addObserver(observer);
        return result;
    }

    @Override
    public String getManagementDomain() {
        return null; // use Hibernate default domain
    }

    @Override
    public String getManagementServiceType() {
        return null;  // use Hibernate default scheme
    }

    @Override
    public Object getManagementBean() {
        return this;
    }

//    public Batcher createBatcher(ConnectionManager connectionManager, Interceptor interceptor) {
//        boolean batching = connectionManager.getFactory().getSettings().getJdbcBatchSize() > 0;
//        return "net.sf.log4jdbc.hibernateprofiler.jdbc4.ConnectionSpy".equals(connectionManager.getConnection().getClass().getName()) ?
//                 new SpyBatchingBatcher(connectionManager, interceptor, batching) :
//                 new ProfilerBatchingBatcher(connectionManager, interceptor, batching);
//    }

}

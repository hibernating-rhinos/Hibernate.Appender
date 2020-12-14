//-----------------------------------------------------------------------
// <copyright file="ProfilerBatchingBatcherFactory.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.jdbc;

import org.hibernate.Interceptor;
import org.hibernate.jdbc.Batcher;
import org.hibernate.jdbc.BatcherFactory;
import org.hibernate.jdbc.ConnectionManager;

public class ProfilerBatchingBatcherFactory implements BatcherFactory {

    public Batcher createBatcher(ConnectionManager connectionManager, Interceptor interceptor) {
        boolean batching = connectionManager.getFactory().getSettings().getJdbcBatchSize() > 0;
        return "net.sf.log4jdbc.hibernateprofiler.jdbc4.ConnectionSpy".equals(connectionManager.getConnection().getClass().getName()) ?
                 new SpyBatchingBatcher(connectionManager, interceptor, batching) :
                 new ProfilerBatchingBatcher(connectionManager, interceptor, batching);
    }
    
}

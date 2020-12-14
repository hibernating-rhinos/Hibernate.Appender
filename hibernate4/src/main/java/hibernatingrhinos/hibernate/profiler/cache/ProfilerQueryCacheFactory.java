//-----------------------------------------------------------------------
// <copyright file="ProfilerQueryCacheFactory.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.cache;

import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.cache.spi.QueryCache;
import org.hibernate.cache.spi.QueryCacheFactory;
import org.hibernate.cache.spi.UpdateTimestampsCache;
import org.hibernate.cfg.Settings;

public class ProfilerQueryCacheFactory implements QueryCacheFactory {

    /**
     * @see org.hibernate.cache.QueryCacheFactory#getQueryCache(java.lang.String,
     *      org.hibernate.cache.UpdateTimestampsCache,
     *      org.hibernate.cfg.Settings, java.util.Properties)
     */
    public QueryCache getQueryCache(String regionName, UpdateTimestampsCache updateTimestampsCache, Settings settings, Properties props)
            throws HibernateException {
        return new ProfilerQueryCache(settings, props, updateTimestampsCache, regionName);
    }

}

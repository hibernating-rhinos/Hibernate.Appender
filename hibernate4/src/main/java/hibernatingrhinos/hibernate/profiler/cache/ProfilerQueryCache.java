//-----------------------------------------------------------------------
// <copyright file="ProfilerQueryCache.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.cache;

import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.cache.internal.StandardQueryCache;
import org.hibernate.cache.spi.QueryKey;
import org.hibernate.cache.spi.UpdateTimestampsCache;
import org.hibernate.cfg.Settings;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.Type;

/**
 * Extends the standard query cache to log a message on
 * a cache hit.
 */
public class ProfilerQueryCache extends StandardQueryCache {

    private static final Logger logger = LogManager.getLogger(ProfilerQueryCache.class);

    /**
     * @param settings
     * @param props
     * @param updateTimestampsCache
     * @param regionName
     * @throws HibernateException
     */
    public ProfilerQueryCache(Settings settings, Properties props, UpdateTimestampsCache updateTimestampsCache, String regionName) throws HibernateException {
        super(settings, props, updateTimestampsCache, regionName);
    }

    /**
     * @see org.hibernate.cache.StandardQueryCache#get(org.hibernate.cache.QueryKey,
     *      org.hibernate.type.Type[], boolean, java.util.Set,
     *      org.hibernate.engine.SessionImplementor)
     */
    public List get(QueryKey key, Type[] returnTypes, boolean isNaturalKeyLookup, Set spaces, SessionImplementor session) throws HibernateException {
        List result = super.get(key, returnTypes, isNaturalKeyLookup, spaces, session);

        if (result != null && logger.isDebugEnabled()) {
            logger.debug("returning cached query results for: " + key);
        }

        return result;
    }
}

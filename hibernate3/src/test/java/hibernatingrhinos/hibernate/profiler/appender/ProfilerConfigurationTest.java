//-----------------------------------------------------------------------
// <copyright file="ProfilerConfigurationTests.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.appender;

import org.hibernate.cfg.Environment;
import org.junit.Test;

import static org.junit.Assert.*;

public class ProfilerConfigurationTest {

    @Test
    public void can_replace_url_with_spy_string() {
        if (!JavaHelper.isJava6()) return;
        
        ProfilerConfiguration configuration = new ProfilerConfiguration(true);
        String config = configuration.configure("hibernate.cache.query_cache_factory=hibernatingrhinos.hibernate.profiler.cache.ProfilerQueryCacheFactory\r" + Environment.URL + "=jdbc:hsqldb:mem:db\r");
        assertTrue(config.indexOf("jdbc:log4jdbc:hsqldb:mem:db") > 0);
    }

    @Test
    public void can_replace_url_with_spy_string_with_tabs() {
        if (!JavaHelper.isJava6()) return;

        ProfilerConfiguration configuration = new ProfilerConfiguration(true);
        String config = configuration.configure("       hibernate.cache.query_cache_factory=hibernatingrhinos.hibernate.profiler.cache.ProfilerQueryCacheFactory\r      " + Environment.URL + "=jdbc:hsqldb:mem:db\r");
        assertTrue(config.indexOf("jdbc:log4jdbc:hsqldb:mem:db") > 0);
    }

}

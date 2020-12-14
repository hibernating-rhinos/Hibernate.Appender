//-----------------------------------------------------------------------
// <copyright file="ProfilerBeanFactoryPostProcessor.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.spring;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import net.sf.log4jdbc.hibernateprofiler.jdbc4.ConnectionSpy;

import org.springframework.jdbc.datasource.DelegatingDataSource;

/**
 * Wrapping {@link DataSource} with {@link ConnectionSpy}.
 *
 */
public class ConnectionWrappingDataSource extends DelegatingDataSource {

    public ConnectionWrappingDataSource(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection result =  super.getConnection();
        return new ConnectionSpy(result);
    }
}

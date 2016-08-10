package org.devoware.example.config.db;

import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import org.apache.tomcat.jdbc.pool.DataSourceProxy;
import org.apache.tomcat.jdbc.pool.PoolConfiguration;

/**
 * A {@link ManagedDataSource} which is backed by a Tomcat pooled {@link javax.sql.DataSource}.
 */
public class ManagedPooledDataSource extends DataSourceProxy implements ManagedDataSource {

    /**
     * Create a new data source with the given connection pool configuration.
     *
     * @param config the connection pool configuration
     */
    public ManagedPooledDataSource(PoolConfiguration config) {
        super(config);
    }

    // JDK6 has JDBC 4.0 which doesn't have this -- don't add @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("Doesn't use java.util.logging");
    }

    @Override
    public void start() throws Exception {
        createPool();
    }

    @Override
    public void stop() throws Exception {
        close();
    }
}

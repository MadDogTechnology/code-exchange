package org.devoware.example.config.db;

import javax.sql.DataSource;

import org.devoware.example.lifecycle.Managed;

public interface ManagedDataSource extends DataSource, Managed {

}

package org.devoware.example.config;

import javax.inject.Singleton;
import javax.sql.DataSource;

import org.devoware.example.config.db.ManagedDataSource;
import org.devoware.example.setup.Environment;

import dagger.Module;
import dagger.Provides;

@Module
public class ConfigurationModule {

  private Dagger2SampleConfiguration config;
  private Environment environment;

  public ConfigurationModule(Dagger2SampleConfiguration config, Environment environment) {
    this.config = config;
    this.environment = environment;
  }

  @Provides
  @Singleton
  public Dagger2SampleConfiguration provideConfiguration() {
    return config;
  }

  @Provides
  @Singleton
  public DataSource provideDataSource() {
    ManagedDataSource dataSource = config.getDataSourceFactory().build("<default>");
    environment.manage(dataSource);
    return dataSource;
  }

}

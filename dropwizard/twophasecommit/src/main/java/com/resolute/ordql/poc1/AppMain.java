package com.resolute.ordql.poc1;

import javax.sql.DataSource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.resolute.ordql.poc1.AppMain.MyAppConfiguration;
import com.resolute.ordql.poc1.dao.TwoPhaseDaoImpl;
import com.resolute.ordql.poc1.resources.TPCResource;

import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class AppMain extends Application<MyAppConfiguration> {

  public static final Logger log = LoggerFactory.getLogger(AppMain.class);

  public static void main(String[] args) throws Exception {
    new AppMain().run(args);
  }


  @Override
  public void initialize(Bootstrap<MyAppConfiguration> bootstrap) {
    bootstrap.addBundle(new AssetsBundle("/assets", "/demo", "index.html"));
  }


  @Override
  public void run(MyAppConfiguration configuration, Environment environment) throws Exception {
    DataSource dataSource =
        configuration.getDataSourceFactory().build(environment.metrics(), "postgres");
    DataSource dataSource20 =
        configuration.getDataSourceFactory20().build(environment.metrics(), "postgres");

    environment.jersey().register(new TPCResource(new TwoPhaseDaoImpl(dataSource, dataSource20)));

    log.info("Application started");
  }


  public static class MyAppConfiguration extends Configuration {

    @Valid
    @NotNull
    private DataSourceFactory database = new DataSourceFactory();

    @JsonProperty("database")
    public void setDataSourceFactory(DataSourceFactory factory) {
      this.database = factory;
    }

    @JsonProperty("database")
    public DataSourceFactory getDataSourceFactory() {
      return database;
    }

    @Valid
    @NotNull
    private DataSourceFactory database20 = new DataSourceFactory();

    @JsonProperty("database20")
    public void setDataSourceFactory20(DataSourceFactory factory) {
      this.database20 = factory;
    }

    @JsonProperty("database20")
    public DataSourceFactory getDataSourceFactory20() {
      return database20;
    }

  }


}

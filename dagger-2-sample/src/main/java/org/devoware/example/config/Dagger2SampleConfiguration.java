package org.devoware.example.config;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.devoware.example.config.db.DataSourceFactory;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Dagger2SampleConfiguration {
  @Valid
  @NotNull
  @JsonProperty
  private DataSourceFactory database = new DataSourceFactory();

  public DataSourceFactory getDataSourceFactory() {
      return database;
  }
}

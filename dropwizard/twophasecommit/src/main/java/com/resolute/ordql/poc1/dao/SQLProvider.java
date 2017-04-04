package com.resolute.ordql.poc1.dao;

public interface SQLProvider {

  public final static String SQLINSERT = "INSERT INTO pointnames(name) VALUES (?)";

  default String getSQLInsert30() {
    return SQLINSERT;
  }

  default String getSQLInsert20() {
    return SQLINSERT;
  }

}

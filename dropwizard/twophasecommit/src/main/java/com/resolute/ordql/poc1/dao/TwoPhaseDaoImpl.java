package com.resolute.ordql.poc1.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TwoPhaseDaoImpl implements PhaseDao {

  private static final Logger log = LoggerFactory.getLogger(TwoPhaseDaoImpl.class);

  private final DataSource cpds30;
  private final DataSource cpds20;

  private final SQLProvider sqlprovider;

  public TwoPhaseDaoImpl(DataSource cpds30, DataSource cpds20) {
    this(cpds30, cpds20, null);
  }

  public TwoPhaseDaoImpl(DataSource cpds30, DataSource cpds20, SQLProvider sqlprovider) {
    this.cpds30 = cpds30;
    this.cpds20 = cpds20;
    if (sqlprovider == null) {
      this.sqlprovider = new SQLProvider() {};
    } else {
      this.sqlprovider = sqlprovider;
    }
  }

  @Override
  public List<String> queryNames(String query) {
    List<String> results = new ArrayList<String>();
    try (Connection connection = cpds30.getConnection();) {
      Statement statement = connection.createStatement();
      ResultSet resultSet = statement.executeQuery(query);
      while (resultSet.next()) {
        results.add(resultSet.getString(1));
      }
    } catch (SQLException e) {
      log.error("JDBC queryNames(String) failed.", e);
    }
    return results;
  }

  @Override
  public void close() {}

  @Override
  public void addName(String name) {

    Connection conn30 = null;
    Connection conn20 = null;
    try {
      conn30 = cpds30.getConnection();
      conn20 = cpds20.getConnection();
      if (conn30 != null && conn20 != null) {
        conn30.setAutoCommit(false);
        conn20.setAutoCommit(false);
        try (PreparedStatement stmt30 = conn30.prepareStatement(sqlprovider.getSQLInsert30());
            PreparedStatement stmt20 = conn20.prepareStatement(sqlprovider.getSQLInsert20())) {
          stmt30.setString(1, name);
          stmt30.executeUpdate();
          stmt20.setString(1, name);
          stmt20.executeUpdate();

          conn30.commit();
          conn20.commit();
        }
      }
    } catch (SQLException e) {
      log.error("Exception occurred.  Rolling back both transactions.", e);
      // by this time, it's clear exception occurred
      try {
        conn30.rollback();
      } catch (SQLException e2) {
        log.error("Rollback failed", e2);
      }
      try {
        conn20.rollback();
      } catch (SQLException e2) {
        log.error("Rollback failed", e2);
      }
    } finally {
      if (conn30 != null) {
        try {
          conn30.setAutoCommit(true);
        } catch (SQLException e) {
          log.error("failed to set connection auto commit", e);
        }
        try {
          conn30.close();
        } catch (SQLException e) {
          log.error("failed to close connection", e);
        }
      }
      if (conn20 != null) {
        try {
          conn20.setAutoCommit(true);
        } catch (SQLException e) {
          log.error("failed to set connection auto commit", e);
        }
        try {
          conn20.close();
        } catch (SQLException e) {
          log.error("failed to close connection", e);
        }
      }
    }

  }

  /**
   * Oversimplified approach
   */
  @Deprecated
  private void addNameAuto(String name) {
    try (Connection conn30 = cpds30.getConnection(); Connection conn20 = cpds20.getConnection();) {
      conn30.setAutoCommit(false);
      conn20.setAutoCommit(false);
      try (PreparedStatement stmt30 = conn30.prepareStatement(sqlprovider.getSQLInsert30());
          PreparedStatement stmt20 = conn20.prepareStatement(sqlprovider.getSQLInsert20())) {
        log.info("Autocommit for conn30 is {}", conn30.getAutoCommit());
        log.info("Autocommit for conn20 is {}", conn20.getAutoCommit());

        stmt30.setString(1, name);
        stmt30.executeUpdate();
        stmt20.setString(1, name);
        stmt20.executeUpdate();
      }
      conn30.setAutoCommit(true);
      conn20.setAutoCommit(true);

    } catch (SQLException e) {
      throw new RuntimeException("A problem occurred.", e);
    } finally {
    }
  }

  @Override
  public void deleteName(String name) {}

  @Override
  public void changeName(String oldName, String newName) {}

}

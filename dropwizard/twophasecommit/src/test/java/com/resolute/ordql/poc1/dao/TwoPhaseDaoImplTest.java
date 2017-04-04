package com.resolute.ordql.poc1.dao;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class TwoPhaseDaoImplTest {

  private static final Logger log = LoggerFactory.getLogger(TwoPhaseDaoImplTest.class);

  private static ComboPooledDataSource cpds30;
  private static ComboPooledDataSource cpds20;

  private static final String INSERTSQL = SQLProvider.SQLINSERT;
  private static final String SELECTSQL = "SELECT oneid, name FROM pointnames";
  private static final String DELETESQL = "DELETE FROM pointnames";
  private static final String SETSEQUENCESQL = "ALTER SEQUENCE pointnames_oneid_seq RESTART WITH 1";
  private static final List<String> SEEDS;

  static {
    SEEDS = new ArrayList<String>();
    for(int i=0; i< 10; i++) {
      SEEDS.add(getGoodName());
    }
  }

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    JsonNode node = mapper.readTree(new File("src/config/appconfig.yaml"));

    cpds30 = new ComboPooledDataSource();
    cpds30.setDriverClass(node.get("database").get("driverClass").textValue());
    cpds30.setJdbcUrl(node.get("database").get("url").textValue());
    cpds30.setUser(node.get("database").get("user").textValue());
    cpds30.setPassword(node.get("database").get("password").textValue());
    cpds30.setInitialPoolSize(1);
    cpds30.setMinPoolSize(1);
    cpds30.setMaxPoolSize(1);
    cpds30.setMaxStatements(180);

    cpds20 = new ComboPooledDataSource();
    cpds20.setDriverClass(node.get("database20").get("driverClass").textValue());
    cpds20.setJdbcUrl(node.get("database20").get("url").textValue());
    cpds20.setUser(node.get("database20").get("user").textValue());
    cpds20.setPassword(node.get("database20").get("password").textValue());
    cpds20.setInitialPoolSize(1);
    cpds20.setMinPoolSize(1);
    cpds20.setMaxPoolSize(1);
    cpds20.setMaxStatements(180);

    // empty the tables
    try (Connection conn30 = cpds30.getConnection(); Connection conn20 = cpds20.getConnection();) {
      try (PreparedStatement stmt30 = conn30.prepareStatement(DELETESQL);
          PreparedStatement stmt20 = conn20.prepareStatement(DELETESQL)) {
        stmt30.executeUpdate();
        stmt20.executeUpdate();
      }
      try (PreparedStatement stmt30 = conn30.prepareStatement(SETSEQUENCESQL);
          PreparedStatement stmt20 = conn20.prepareStatement(SETSEQUENCESQL)) {
        stmt30.execute();
        stmt20.execute();
      }

      try (PreparedStatement stmt30 = conn30.prepareStatement(INSERTSQL);
          PreparedStatement stmt20 = conn20.prepareStatement(INSERTSQL)) {
        for (String seed : SEEDS) {
          stmt30.setString(1, seed);
          stmt30.addBatch();
          stmt20.setString(1, seed);
          stmt20.addBatch();
        }
        stmt30.executeBatch();
        stmt20.executeBatch();
      }
    } catch (SQLException e) {
      throw new RuntimeException("A problem occurred.", e);
    } finally {
    }
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    cpds30.close();
    cpds20.close();
  }

  @Before
  public void setUp() throws Exception {}

  @After
  public void tearDown() throws Exception {}

  @Test
  public void testTrueTrue() throws Exception {
    this.confirmAutocommitStillTrue();
    String name = getGoodName();
    try (PhaseDao dao = new TwoPhaseDaoImpl(cpds30, cpds20);) {
      dao.addName(name);
    }
    // confirm
    Map<Integer, String> results30 = getTableContent(cpds30);
    Map<Integer, String> results20 = getTableContent(cpds20);
    log.info("{}", results30);
    log.info("{}", results20);
    Assert.assertEquals(results30, results20);
    Assert.assertTrue(results30.containsValue(name));
    Assert.assertTrue(results20.containsValue(name));
  }

  @Test
  public void testFalseTrue() throws Exception {
    this.confirmAutocommitStillTrue();
    String name = getGoodName();
    DisruptiveSQLProvider sqlprovider = new DisruptiveSQLProvider();
    try (PhaseDao dao = new TwoPhaseDaoImpl(cpds30, cpds20, sqlprovider);) {
      sqlprovider.interrupt30 = true;
      dao.addName(name);
    }
    this.confirmAutocommitStillTrue();
    // confirm
    Map<Integer, String> results30 = getTableContent(cpds30);
    Map<Integer, String> results20 = getTableContent(cpds20);
    log.info("{}", results30);
    log.info("{}", results20);
    Assert.assertEquals(results30, results20);
    Assert.assertFalse(results30.containsValue(name));
    Assert.assertFalse(results20.containsValue(name));
  }


  @Test
  public void testTrueFalse() throws Exception {
    this.confirmAutocommitStillTrue();
    String name = getGoodName();
    DisruptiveSQLProvider sqlprovider = new DisruptiveSQLProvider();
    try (PhaseDao dao = new TwoPhaseDaoImpl(cpds30, cpds20, sqlprovider);) {
      sqlprovider.interrupt20 = true;
      dao.addName(name);
    }
    this.confirmAutocommitStillTrue();
    // confirm
    Map<Integer, String> results30 = getTableContent(cpds30);
    Map<Integer, String> results20 = getTableContent(cpds20);
    log.info("{}", results30);
    log.info("{}", results20);
    Assert.assertEquals(results30, results20);
    Assert.assertFalse(results30.containsValue(name));
    Assert.assertFalse(results20.containsValue(name));
  }

  @Test
  public void testFalseFalse() throws Exception {
    this.confirmAutocommitStillTrue();
    String name = getGoodName();
    DisruptiveSQLProvider sqlprovider = new DisruptiveSQLProvider();
    try (PhaseDao dao = new TwoPhaseDaoImpl(cpds30, cpds20, sqlprovider);) {
      sqlprovider.interrupt30 = true;
      sqlprovider.interrupt20 = true;
      dao.addName(name);
    }
    this.confirmAutocommitStillTrue();
    // confirm
    Map<Integer, String> results30 = getTableContent(cpds30);
    Map<Integer, String> results20 = getTableContent(cpds20);
    log.info("{}", results30);
    log.info("{}", results20);
    Assert.assertEquals(results30, results20);
    Assert.assertFalse(results30.containsValue(name));
    Assert.assertFalse(results20.containsValue(name));
  }

  private static String getGoodName() {
    return RandomStringUtils.randomAlphanumeric(RandomUtils.nextInt(20, 250));
  }

  private static Map<Integer, String> getTableContent(ComboPooledDataSource cpds) {
    HashMap<Integer, String> results = new HashMap<Integer, String>();
    try (Connection conn = cpds.getConnection();
        PreparedStatement stmt = conn.prepareStatement(SELECTSQL);) {
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        results.put(rs.getInt(1), rs.getString(2));
      }
    } catch (SQLException e) {
      throw new RuntimeException("A problem occurred.", e);
    }
    return results;
  }

  private void confirmAutocommitStillTrue() throws SQLException {
    try (Connection conn30 = cpds30.getConnection(); Connection conn20 = cpds20.getConnection();) {
      Assert.assertTrue(conn30.getAutoCommit());
      Assert.assertTrue(conn20.getAutoCommit());
    }
  }

  private static class DisruptiveSQLProvider implements SQLProvider {

    private boolean interrupt30 = false;
    private boolean interrupt20 = false;

    @Override
    public String getSQLInsert30() {
      String results = SQLProvider.super.getSQLInsert30();
      if (interrupt30 == true) {
        interrupt30 = false;
        results = "BAD" + results;
      }
      return results;
    }

    @Override
    public String getSQLInsert20() {
      String results = SQLProvider.super.getSQLInsert20();
      if (interrupt20 == true) {
        interrupt20 = false;
        results = "BAD" + results;
      }
      return results;
    }

  }
}

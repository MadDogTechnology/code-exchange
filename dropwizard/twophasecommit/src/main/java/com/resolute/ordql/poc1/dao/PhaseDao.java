package com.resolute.ordql.poc1.dao;

import java.util.List;

public interface PhaseDao extends AutoCloseable {

  public List<String> queryNames(String query);

  public void addName(String name);

  public void deleteName(String name);

  public void changeName(String oldName, String newName);

}


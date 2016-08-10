package org.devoware.example.dao;

import java.util.List;

import javax.sql.DataSource;

import org.devoware.example.model.User;

public interface UserDao {

  public User getUser(int id);
  
  public List<User> getUsers();
  
  public DataSource getDataSource();
}

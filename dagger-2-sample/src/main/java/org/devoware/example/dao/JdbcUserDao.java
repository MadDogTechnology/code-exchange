package org.devoware.example.dao;

import static java.util.Objects.requireNonNull;

import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.sql.DataSource;

import org.devoware.example.model.User;

import com.google.common.collect.Lists;

class JdbcUserDao implements UserDao {
  
  private DataSource dataSource;
  
  @Inject
  JdbcUserDao(@Nonnull DataSource dataSource) {
    this.dataSource = requireNonNull(dataSource);
  }

  @Override
  @CheckForNull
  public User getUser(int id) {
    if (id == 1) {
        return new User(id, "cdevoto", "Carlos", "Devoto");
    }
    return null;
  }

  @Override
  @Nonnull
  public List<User> getUsers() {
    return Lists.newArrayList(getUser(1));
  }

  public DataSource getDataSource() {
    return dataSource;
  }
}

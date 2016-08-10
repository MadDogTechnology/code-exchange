package org.devoware.example.dao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DaoModule {

  @Provides
  @Singleton
  public static UserDao provideUserDao(JdbcUserDao dao) {
    return dao;
  }

}

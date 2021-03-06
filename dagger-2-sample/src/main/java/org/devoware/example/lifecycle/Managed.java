package org.devoware.example.lifecycle;

/**
 * An interface for objects which need to be started and stopped as the application is started or
 * stopped.
 */
public interface Managed {
  public static final int HIGH_PRIORITY = 10;
  public static final int MEDIUM_PRIORITY = 20;
  public static final int LOW_PRIORITY = 30;

  default public void start() throws Exception {}

  default public void stop() throws Exception {}

  default public int getPriority() {
    return MEDIUM_PRIORITY;
  }
}

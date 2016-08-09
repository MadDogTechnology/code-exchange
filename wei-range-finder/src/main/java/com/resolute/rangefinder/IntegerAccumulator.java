package com.resolute.rangefinder;

import java.util.function.Consumer;

public abstract class IntegerAccumulator<T extends TimeInterval> implements Consumer<T> {
  private int value;

  @Override
  public void accept(T t) {
    value += accumulate(t);

  }
  
  public int get () {
    return value;
  }

  protected abstract int accumulate(T t);


}

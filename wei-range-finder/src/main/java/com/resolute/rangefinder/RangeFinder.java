package com.resolute.rangefinder;

import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.Sets;
import com.google.common.collect.TreeRangeMap;

public class RangeFinder<T extends TimeInterval> {

  private final Map<T, Range<Long>> rangeMap = Maps.newLinkedHashMap();
  
  @Nonnull 
  public static <T extends TimeInterval> Builder<T> builder () {
    return new Builder<>();
  }
  
  @Nonnull 
  public static <T extends TimeInterval> Builder<T> builder (Consumer<Builder<T>> consumer) {
    Builder<T> builder = new Builder<>();
    consumer.accept(builder);
    return builder;
  }

  private RangeFinder(@Nonnull Builder<T> builder) {
    this.rangeMap.putAll(builder.rangeMap);
  }
  
  public Set<T> getAllRecordsContaining(long timestamp) {
    Set<T> records = Sets.newLinkedHashSet();
    for (Map.Entry<T, Range<Long>> entry : rangeMap.entrySet()) {
      if (entry.getValue().contains(timestamp)) {
        records.add(entry.getKey());
      }
    }
    return records;
  }
  
  public static class Builder<T extends TimeInterval> {
    private final Map<T, Range<Long>> rangeMap = Maps.newLinkedHashMap();
    
    private Builder () {}
    
    @Nonnull
    public Builder<T> addRecord(@Nonnull T record) {
      requireNonNull(record);
      rangeMap.put(record, Range.closed(record.getStartTime(), record.getEndTime()));
      return this;
    }
    
    @Nonnull
    public RangeFinder<T> build() {
      return new RangeFinder<>(this);
    }
    
  }
}

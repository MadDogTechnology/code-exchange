package org.devoware.example.setup;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import org.devoware.example.lifecycle.Managed;

import com.google.common.collect.Lists;

public class Environment {
  private final PriorityQueue<Managed> managed =
      new PriorityQueue<Managed>(5, new Comparator<Managed>() {

        @Override
        public int compare(Managed lc1, Managed lc2) {
          return lc1.getPriority() - lc2.getPriority();
        }

      });

  public Environment() {}

  public void manage(Managed managed) {
    this.managed.add(managed);
  }

  public List<Managed> getManagedResources() {
    return Lists.newArrayList(managed);
  }

}

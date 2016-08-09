package com.resolute.model;

import com.resolute.rangefinder.TimeInterval;

public class EquipmentRow implements TimeInterval {
  private String equipmentName;
  private long startTime;
  private long endTime;
  private int numCoils;
  
  public EquipmentRow(String equipmentName, long startTime, long endTime, int numCoils) {
    super();
    this.equipmentName = equipmentName;
    this.startTime = startTime;
    this.endTime = endTime;
    this.numCoils = numCoils;
  }

  public String getEquipmentName() {
    return equipmentName;
  }

  @Override
  public long getStartTime() {
    return startTime;
  }

  @Override
  public long getEndTime() {
    return endTime;
  }

  public int getNumCoils() {
    return numCoils;
  }

  @Override
  public String toString() {
    return "EquipmentRow [equipmentName=" + equipmentName + ", startTime=" + startTime
        + ", endTime=" + endTime + ", numCoils=" + numCoils + "]";
  }

}

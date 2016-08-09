package com.resolute.rangefinder;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.resolute.model.EquipmentRow;

public class RangeFinderTest {
  private static final String dateTimeFormatPattern = "yyyy/MM/dd HH:mm:ss";
  
  private final EquipmentRow [] rows = {
      new EquipmentRow("slitter1", parseTime("10:07"), parseTime("10:21"), 5),
      new EquipmentRow("slitter1", parseTime("10:08"), parseTime("10:22"), 5),
      new EquipmentRow("slitter1", parseTime("10:15"), parseTime("10:30"), 5),
  };
  
  private RangeFinder<EquipmentRow> rangeFinder;
  
  @Before
  public void setup () {
    rangeFinder = RangeFinder.<EquipmentRow>builder((builder) -> {
      for (EquipmentRow row : rows) {
        builder.addRecord(row);
      }
    }).build();
  }
  
  
  @Test
  public void test_range_finder() {

    int numCoils = getNumCoils("10:10");
    assertThat(numCoils, equalTo(10));
    
    numCoils = getNumCoils("10:15");
    assertThat(numCoils, equalTo(15));
    
    numCoils = getNumCoils("10:20");
    assertThat(numCoils, equalTo(15));
    
    numCoils = getNumCoils("10:25");
    assertThat(numCoils, equalTo(5));
    
    numCoils = getNumCoils("10:30");
    assertThat(numCoils, equalTo(5));
  }


  private int getNumCoils(String timestamp) {
    System.out.println("Rows containing timestamp " + timestamp + ":");
    int numCoils = 0;
    for (EquipmentRow row : rangeFinder.getAllRecordsContaining(parseTime(timestamp))) {
      System.out.println("\tstartTime: " + formatTime(row.getStartTime()) + ", endTime: " + formatTime(row.getEndTime()) + " numCoils: " + row.getNumCoils());
      numCoils += row.getNumCoils();
    }
    return numCoils;
  }
  
  private static long parseTime(String time) {
    DateFormat df = new SimpleDateFormat(dateTimeFormatPattern);
    try {
       return df.parse("2016/8/9 " + time + ":00").getTime();
    } catch (ParseException ex) {
      throw new RuntimeException(ex);
    }
  }

  private static String formatTime(long time) {
    DateFormat df = new SimpleDateFormat("HH:mm");
    return df.format(time);
  }
}

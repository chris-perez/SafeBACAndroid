package com.example.chris.safebacandroid;

/**
 * Created by Chris on 5/4/2016.
 */
public class DrinkLogItem {
  Long id;
  String name;
  String type;
  Double abv;
  Double volume;
  Long date;

  public DrinkLogItem(Long id, String name, String type, Double abv, Double volume, Long date) {
    this.id = id;
    this.name = name;
    this.type = type;
    this.abv = abv;
    this.volume = volume;
    this.date = date;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

  public Double getAbv() {
    return abv;
  }

  public Double getVolume() {
    return volume;
  }

  public Long getDate() {
    return date;
  }
}

package com.example.chris.safebacandroid;

/**
 * Created by Chris on 5/5/2016.
 */
public class FriendItem {
  Long id;
  String name;
  String email;
  Double bac;
  Boolean bacVisible;

  public FriendItem(Long id, String name, String email, Double bac, Boolean bacVisible) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.bac = bac;
    this.bacVisible = bacVisible;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }

  public Double getBac() {
    return bac;
  }

  public Boolean getBacVisible() {
    return bacVisible;
  }

  public void setBacVisible(Boolean bacVisible) {
    this.bacVisible = bacVisible;
  }
}

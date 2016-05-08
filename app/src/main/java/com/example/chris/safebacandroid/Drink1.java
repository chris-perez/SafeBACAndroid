package com.example.chris.safebacandroid;

/**
 * Created by Steel on 5/4/16.
 */
public class Drink1 {

    private int id;
    private String name, type;
    private double abv;

    public Drink1(int id, String name, double abv, String type){
        this.id = id;
        this.name = name;
        this.abv = abv;
        this.type = type;
    }

    public int getId(){
        return this.id;
    }
    public String getName(){
        return this.name;
    }
    public double getAbv(){
        return this.abv;
    }
    public String getType(){
        return this.type;
    }

}

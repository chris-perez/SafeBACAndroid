package com.example.chris.safebacandroid;

import java.util.Comparator;

/**
 * Created by Steel on 4/25/16.
 */
public class drink implements Comparable<drink>{

    public String name;
    public int type;       // 0 = beer, 1 = wine, 2 = cocktail, 3 = liguor
    public double abv;

    public drink(){
        super();
        this.name = "";
        this.type = -1;
        this.abv = 0.0;
    }

    public drink(String name, int type, double abv){
        super();
        this.name = name;
        this.type = type;
        this.abv = abv;


    }

    public String getName(){
        return name;
    }

    public void setName(String newName){
        this.name = newName;
    }

    public double getAbv(){
        return abv;
    }

    public void setAbv(double newAbv){
        this.abv = newAbv;
    }

    public int compareTo(drink compareBooze){
        double compareAbv = (compareBooze).getAbv();

        return (int)(this.abv - compareAbv);

    }


    public void print_booze(){
        System.out.println("Type: "+type+", Name: "+name+", ABV: "+abv);
    }





}

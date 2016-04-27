package com.example.chris.safebacandroid;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

/**
 * Created by Steel on 4/25/16.
 */
public class drink_container {

    ArrayList<drink> catalogue;


    public drink_container(){

        catalogue = new ArrayList<drink>();

    }

    public void add_drink(drink drink){
        catalogue.add(drink);
    }

    public void add_drink(String name, int type, double abv){
        catalogue.add(new drink(name,type,abv));
    }

    public void list_drinks(){
        for (int i = 0; i < catalogue.size(); i++){
            catalogue.get(i).print_booze();
        }

    }

    public ArrayList<String> string_list_alcCont(){
        ArrayList<String> list_temp = new ArrayList<String>();
        for (int i = 0; i < catalogue.size(); i++){
            list_temp.add(""+catalogue.get(i).abv);
        }
        return list_temp;
    }


    public ArrayList<drink> get_type_list(int type){
        ArrayList<drink> temp_list = new ArrayList<>();
        for (int i = 0; i < catalogue.size(); i++){
            if (catalogue.get(i).type == (type-1)){
                temp_list.add(catalogue.get(i));
            }
        }

        return temp_list;

    }


    public ArrayList<String> string_list_names(){

        ArrayList<String> list_temp = new ArrayList<String>();
        for (int i = 0; i < catalogue.size(); i++){
            list_temp.add(catalogue.get(i).name);
        }
        return list_temp;
    }


    public void import_drinks(){
        ArrayList<drink> temp_cat = new ArrayList<drink>();
        String line;

        String workingDir = System.getProperty("user.dir");
        System.out.println("    "+workingDir);

        String myFile01 = "/Users/Steel/IdeaProjects/UItemp/app/src/main/res/m_data.txt";
        //String myFile02 = "m_data.txt";

        try {
            BufferedReader reader = new BufferedReader(new FileReader(myFile01));
            while ((line = reader.readLine()) != null) {

                String[] parts = line.split(" '|' ");

                drink temp_boo = new drink(parts[1], Integer.parseInt(parts[0]), Double.parseDouble(parts[2]));
                System.out.println(temp_boo.name+" "+temp_boo.abv);
                temp_cat.add(temp_boo);
            }
            reader.close();
        }catch(Exception e){
            System.out.println("Exception Occerred.");
        }
        catalogue = temp_cat;
    }

    public void export_drinks(){

        String line;

        try{

            BufferedWriter writer = new BufferedWriter(new FileWriter("/Users/Steel/IdeaProjects/UItemp/app/src/main/res/m_data.txt"));

            for (int i = 0; i < catalogue.size(); i++){
                drink temp = catalogue.get(i);
                line = temp.type + " '"+temp.name+"' "+temp.abv;
                writer.write(line);

                if (i < catalogue.size()-1) {
                    writer.newLine();
                }


            }

            writer.close();


        }catch(Exception e){
            System.out.println("Exception Occured.");
        }




    }

    public void populate(){

        add_drink("Harpoon IPA", 0, 5.9);
        add_drink("Sam Adams Lager", 0, 4.9);
        add_drink("Kendall Jackson", 1, 13.5);
        add_drink("Avion Silver", 3, 40.0);
        add_drink("Mojito", 2, 9.3);
        add_drink("Jameson", 3, 40.2);
        add_drink("Belvedere", 3, 40.0);
        add_drink("Macallan 18", 3, 43.0);
        add_drink("Guinness", 0, 4.3);
    }


    public static void main(String []args){

        drink_container cat = new drink_container();
        cat.import_drinks();

        cat.catalogue.get(1).print_booze();

        drink boo_1 = new drink("Belvedere", 3, 40.0);
        drink boo_2 = new drink("Macallan 18 Years", 3, 43.0);
        drink boo_3 = new drink("Guinness", 0, 4.3);

        cat.add_drink(boo_1);
        cat.add_drink(boo_2);
        cat.add_drink(boo_3);

        cat.export_drinks();



    }



}

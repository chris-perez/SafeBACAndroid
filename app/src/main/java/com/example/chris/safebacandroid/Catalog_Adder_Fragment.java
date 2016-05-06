package com.example.chris.safebacandroid;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * Created by Steel on 5/1/16.
 */
public class Catalog_Adder_Fragment extends Fragment implements View.OnClickListener{


    private View view;
    private CheckBox beer_check;
    private CheckBox wine_check;
    private CheckBox cocktail_check;
    private CheckBox current;
    private String current_type;
    private EditText drink_name;
    private EditText abv_num;
    private EditText qty_num;
    private String DRINK_NAME;
    private String DRINK_ABV;
    private String DRINK_QTY;
    private Button submit_butt;
    private Button cancel_butt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.catalogue_adder_fragment, container, false);

        beer_check = (CheckBox)view.findViewById(R.id.beer_check);
        beer_check.setOnClickListener(this);
        wine_check = (CheckBox)view.findViewById(R.id.wine_check);
        wine_check.setOnClickListener(this);
        cocktail_check = (CheckBox)view.findViewById(R.id.cocktail_check);
        cocktail_check.setOnClickListener(this);
        submit_butt = (Button)view.findViewById(R.id.finish_add_drink_button);
        submit_butt.setOnClickListener(this);
        cancel_butt = (Button)view.findViewById(R.id.cancel_add_drink_button);
        cancel_butt.setOnClickListener(this);

        return view;
    }
    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.beer_check:
                beer_check.setChecked(true);
                if(current != null){
                    current.setChecked(false);
                }
                current = beer_check;
                current_type = "beer";
                break;
            case R.id.wine_check:
                wine_check.setChecked(true);
                if(current != null){
                    current.setChecked(false);
                }
                current = wine_check;
                current_type = "wine";
                break;
            case R.id.cocktail_check:
                cocktail_check.setChecked(true);
                if(current != null){
                    current.setChecked(false);
                }
                current = cocktail_check;
                current_type = "other";
                break;
            case R.id.finish_add_drink_button:
                submit_drink();
                break;

            case R.id.cancel_add_drink_button:
                ((Catalog_Browser_Activity)getActivity()).open_adder();
                break;
            default:

                break;
        }
        if(current!=null){
            current.setChecked(true);
        }
    }

    public boolean isDouble(String num){
        try{
            Double.parseDouble(num);
            return true;
        }catch(NumberFormatException e){
            return false;
        }
    }


    public void submit_drink(){

        drink_name = (EditText)view.findViewById(R.id.new_drink_name_box);
        abv_num = (EditText)view.findViewById(R.id.new_drink_abv_box);
        qty_num = (EditText)view.findViewById(R.id.new_drink_qty_input);

        if ((drink_name != null) && (abv_num != null) && (isDouble(abv_num.getText().toString())) && (isDouble(qty_num.getText().toString()))){
            DRINK_NAME = drink_name.getText().toString();
            DRINK_ABV = abv_num.getText().toString();
            DRINK_QTY = qty_num.getText().toString();


            //((Catalog_Browser_Activity)getActivity()).submit_custom_drink(current_type, DRINK_NAME, DRINK_ABV, DRINK_QTY);
            ((Catalog_Browser_Activity)getActivity()).open_adder();
        }

    }
}

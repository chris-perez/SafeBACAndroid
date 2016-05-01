package com.example.chris.safebacandroid;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Steel on 4/25/16.
 */
public class Catalogue_Browser_Activity extends Activity implements View.OnClickListener{

    public static final String ADDER_TAG = "adder_tag";

    int booze_type;
    drink_container drink_list;
    ListView list;
    EditText filt;
    ArrayList<drink> current_list, filtered_list;
    String filter_input;
    Custom_List_Adapter adapter;
    View current_selection;

    Button add_drink;
    Button update_bac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogue_browser);

        Bundle par = getIntent().getExtras();
        booze_type = par.getInt("Label");
        show_label();

        add_drink = (Button)findViewById(R.id.new_drink);
        update_bac = (Button)findViewById(R.id.submit_abv);
        add_drink.setOnClickListener(this);
        update_bac.setOnClickListener(this);


        drink_list = new drink_container();
        drink_list.populate();
        current_list = drink_list.get_type_list(booze_type);

        list = (ListView)findViewById(R.id.list_major);
        filt = (EditText)findViewById(R.id.filter_input);

        adapter = new Custom_List_Adapter(this,current_list);
        list.setAdapter(adapter);

        filt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Catalogue_Browser_Activity.this.adapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                view.setBackgroundResource(R.color.lightBlue00);
                if ((current_selection != null) && (current_selection != view)){
                    current_selection.setBackgroundResource(R.color.transparent);
                }
                current_selection = view;

            }
        });

    }


    @Override
    public void onClick(View v){

        switch(v.getId()){
            case R.id.new_drink:
                System.out.println("new_drink button clicked");

                open_adder();

                break;

            case R.id.submit_abv:
                break;
            default:
                break;
        }


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){

        if ((keyCode == KeyEvent.KEYCODE_BACK)){
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }


    public void show_label(){

        ImageView banner = (ImageView)findViewById(R.id.beer_banner);
        if (booze_type == 1){banner.setVisibility(View.VISIBLE);
        }else{banner.setVisibility(View.INVISIBLE);}

        banner = (ImageView)findViewById(R.id.wine_banner);
        if (booze_type == 2){banner.setVisibility(View.VISIBLE);
        }else{banner.setVisibility(View.INVISIBLE);}

        banner = (ImageView)findViewById(R.id.cocktail_banner);
        if (booze_type == 3){banner.setVisibility(View.VISIBLE);
        }else{banner.setVisibility(View.INVISIBLE);}

        banner = (ImageView)findViewById(R.id.liquor_banner);
        if (booze_type == 4){banner.setVisibility(View.VISIBLE);
        }else{banner.setVisibility(View.INVISIBLE);}

    }

    public void open_adder(){

        View toFadeList = findViewById(R.id.list_cont);
        View toFadeBanner = findViewById(R.id.booze_banner);
        View toFadeFilter = findViewById(R.id.filter_ui_container);
        View toFadeSubmit = findViewById(R.id.submit_abv);

        Fragment test = getFragmentManager().findFragmentByTag(ADDER_TAG);
        if (test != null){
            getFragmentManager().popBackStack();
            toFadeList.setAlpha(1.0f);
            toFadeBanner.setAlpha(1.0f);
            toFadeFilter.setAlpha(1.0f);
            toFadeSubmit.setAlpha(1.0f);

        }else{
            Fragment new_frag = new Catalogue_Adder_Fragment();

            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_left,R.anim.slide_right)
                    .add(R.id.new_drink_frag_cont,new_frag,ADDER_TAG).addToBackStack(null).commit();
            toFadeList.setAlpha(0.3f);
            toFadeBanner.setAlpha(0.3f);
            toFadeFilter.setAlpha(0.3f);
            toFadeSubmit.setAlpha(0.3f);

        }

    }

    public void add_drink_database(int type, String name, double abv){

        drink new_booze = new drink(name,type-1,abv);
        drink_list.add_drink(new_booze);

        current_list = drink_list.get_type_list(booze_type);

        adapter = new Custom_List_Adapter(this, current_list);
        list.setAdapter(adapter);

    }


}

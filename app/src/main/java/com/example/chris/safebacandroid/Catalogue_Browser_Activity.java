package com.example.chris.safebacandroid;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Steel on 4/25/16.
 */
public class Catalogue_Browser_Activity extends Activity{


    int booze_type;
    drink_container drink_list;
    ListView list;
    EditText filt;
    ArrayList<drink> current_list, filtered_list;
    String filter_input;
    Custom_List_Adapter adapter;
    View current_selection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogue_browser);

        Bundle par = getIntent().getExtras();
        booze_type = par.getInt("Label");
        show_label();

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

}

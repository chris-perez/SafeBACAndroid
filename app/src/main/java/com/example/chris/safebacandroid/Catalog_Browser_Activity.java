package com.example.chris.safebacandroid;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Steel on 4/25/16.
 */
public class Catalog_Browser_Activity extends Activity implements View.OnClickListener{

    public static final String ADDER_TAG = "adder_tag";

    private String DRINK_TYPE;
    private ListView LIST;
    private EditText FILT;
    private EditText VOL;
    private DrinkListAdapter ADAPTER;
    private View currentSelection;
    private ArrayList<Drink> catalog;

    private Button ADD_BUTTON;
    private Button UPDATE_BUTTON;

    private Boolean frag_open = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog_browser);


        Bundle par = getIntent().getExtras();
        int drType = par.getInt("Label");

        RetrieveCatalogTask catTask = new RetrieveCatalogTask();
        catTask.execute((Void) null);


        show_label(drType);

        ADD_BUTTON = (Button)findViewById(R.id.new_drink);
        ADD_BUTTON.setVisibility(View.GONE);
        UPDATE_BUTTON = (Button)findViewById(R.id.submit_abv);
        //ADD_BUTTON.setOnClickListener(this);
        UPDATE_BUTTON.setOnClickListener(this);

        LIST = (ListView)findViewById(R.id.list_major);
        VOL = (EditText)findViewById(R.id.qtyInput);

        LIST.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                view.setBackgroundResource(R.color.lightBlue00);
                if ((currentSelection != null) && (currentSelection != view)) {
                    currentSelection.setBackgroundResource(R.color.transparent);
                }
                currentSelection = view;

            }
        });

    }
    public boolean isDouble(String num){
        try{
            Double.parseDouble(num);
            return true;
        }catch(NumberFormatException e){
            return false;
        }
    }

    @Override
    public void onClick(View v){

        switch(v.getId()){
            case R.id.new_drink:
                if (!frag_open){
                    open_adder();
                }
                break;
            case R.id.submit_abv:

                if (!frag_open && currentSelection != null && (isDouble(VOL.getText().toString()))){

                    TextView itemId = (TextView)currentSelection.findViewById(R.id.unit_id);

                    submit_drink(itemId.getText().toString(), VOL.getText().toString());

                }

                break;
            default:
                break;
        }


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * shows top left icon to remind user of drink type currently being explored
     * @param drType drink type specification
     */

    public void show_label(int drType){

        ImageView banner = (ImageView)findViewById(R.id.beer_banner);
        if (drType == 1){
            banner.setVisibility(View.VISIBLE);
            DRINK_TYPE = "beer";
        }else{banner.setVisibility(View.INVISIBLE);}

        banner = (ImageView)findViewById(R.id.wine_banner);
        if (drType == 2){
            banner.setVisibility(View.VISIBLE);
            DRINK_TYPE = "wine";
        }else{banner.setVisibility(View.INVISIBLE);}

        banner = (ImageView)findViewById(R.id.liquor_banner);
        if (drType == 3){
            banner.setVisibility(View.VISIBLE);
            DRINK_TYPE = "other";
        }else{banner.setVisibility(View.INVISIBLE);}

    }

    /**
     * expander for custom drink fragment
     */
    public void open_adder(){
        Fragment test = getFragmentManager().findFragmentByTag(ADDER_TAG);
        if (test != null){
            getFragmentManager().popBackStack();
            frag_open = false;
        }else{
            /*Fragment new_frag = new Catalog_Adder_Fragment();
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_left,R.anim.slide_right)
                    .add(R.id.new_drink_frag_cont,new_frag,ADDER_TAG).addToBackStack(null).commit();
            frag_open = true;*/
        }
    }

    /**
     * sends selected drink to user profile on server
     * @param id id of selected drink
     * @param vol volume of selected drink in fl.oz
     */
    public void submit_drink(String id, String vol){
        SubmitDrinkTask appender = new SubmitDrinkTask(id, vol);
        appender.execute((Void)null);
        finish();
    }

    /**
     * sends custom drink to user profile on server
     * @param name name of new drink
     * @param abv alcohol content of new drink
     * @param type type specifier of new drink
     * @param vol volume of new drink in fl.oz
     */
    public void submit_custom_drink(String name, String abv, String type, String vol){
        SubmitDrinkTask appender = new SubmitDrinkTask(name, abv, type, vol);
        appender.execute((Void)null);
    }

    /**
     * initializes custom adapter based on desired list type
     */
    public void buildAdapter(){
        ADAPTER = new DrinkListAdapter(this, catalog);
        LIST.setAdapter(ADAPTER);

        FILT = (EditText)findViewById(R.id.filter_input);
        FILT.addTextChangedListener(new CustomTextWatcher(FILT));
    }

    /**
     * initializes a text watcher for user volume input
     */
    private class CustomTextWatcher implements TextWatcher{

        private EditText editText;

        public CustomTextWatcher(EditText editText){
            this.editText = editText;
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after){

        }
        public void onTextChanged(CharSequence s, int start, int before, int count){
            Catalog_Browser_Activity.this.ADAPTER.getFilter().filter(s.toString());
        }
        public void afterTextChanged(Editable s){

        }
    }

    /**
     * custom task for acquiring server information to submit drink for abv
     */
    private class SubmitDrinkTask extends AsyncTask<Void, Void, JSONObject>{

        private String name;
        private String abv;
        private String type;
        private String vol;
        private String id;

        public SubmitDrinkTask(String id, String vol){
            this.vol = vol;
            this.id = id;
            this.name = "";
            this.abv = "";
            this.type = "";

        }

        public SubmitDrinkTask(String name, String abv, String type, String vol){
            this.name = name;
            this.abv = abv;
            this.type = type;
            this.vol = vol;
        }

        @Override
        protected JSONObject doInBackground(Void... params){
            JSONObject request = new JSONObject();
            try{
                request.put("name", this.name);
                request.put("abv", this.abv);
                request.put("type", this.type);
                request.put("id", this.id);
                request.put("volume", this.vol);
            }catch (JSONException e){
                e.printStackTrace();
                return null;
            }
            return APICaller.submitDrink(request);
        }
    }

    /**
     * custom task for acquiring server information for applying list
     */
    private class RetrieveCatalogTask extends AsyncTask<Void, Void, JSONArray>{

        ArrayList<Drink> drinksReciever;

        @Override
        protected JSONArray doInBackground(Void... params){
            return APICaller.openCatalog();
        }

        @Override
        protected void onPostExecute(JSONArray result){
            drinksReciever = new ArrayList<>();

            try{
                JSONObject jOb = null;
                for (int i = 0; i < result.length(); i++){
                    jOb = result.getJSONObject(i);
                    int idTemp = jOb.getInt("id");
                    String nameTemp = jOb.getString("name");
                    double abvTemp = jOb.getDouble("abv");
                    String typeTemp = jOb.getString("type");
                    if (typeTemp.equals(DRINK_TYPE)) {
                        drinksReciever.add(new Drink(idTemp, nameTemp, abvTemp, typeTemp));
                    }
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
            catalog = drinksReciever;
            System.out.println("---> CATALOG SIZE: "+catalog.size());
            buildAdapter();
        }
    }
}

package com.example.chris.safebacandroid;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DrinkLogActivity extends Activity {
  ListView drinkListView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_drink_history);

    drinkListView = (ListView) findViewById(R.id.drink_list);
    drinkListView.setAdapter(new DrinkLogListAdapter(this, R.layout.drink_log_item, new ArrayList<DrinkLogItem>()));
  }

  @Override
  protected void onStart() {
    super.onStart();
    new GetDrinkLogTask().execute((Void)null);
  }

  /**
   * Sets the adapter for the drink list view
   * @param drinkLogItems items to be shown in the drinkListView
   */
  private void setAdapter(List<DrinkLogItem> drinkLogItems) {
    drinkListView.setAdapter(new DrinkLogListAdapter(this, R.layout.drink_log_item, drinkLogItems));

  }

  /**
   * Handles the call to the server to get the Drink Log
   */
  public class GetDrinkLogTask extends AsyncTask<Void, Void, JSONArray> {
    @Override
    protected JSONArray doInBackground(Void... params) {
      return APICaller.getDrinkLog();
    }

    @Override
    protected void onPostExecute(JSONArray jsonArray) {
      List<DrinkLogItem> drinkLogItems = new ArrayList<>();
      try {
        for (int i = 0; i < jsonArray.length(); i++) {
          JSONObject drinkJson = jsonArray.getJSONObject(i);
          Long id = drinkJson.getLong("id");
          String name = drinkJson.getString("name");
          Double abv = drinkJson.getDouble("abv");
          String type = drinkJson.getString("type");
          Double volume = drinkJson.getDouble("volume");
          Long time = drinkJson.getLong("time");
          drinkLogItems.add(new DrinkLogItem(id, name, type, abv, volume, time));
        }

        setAdapter(drinkLogItems);
      } catch (JSONException e){
        e.printStackTrace();
      }
    }
  }
}

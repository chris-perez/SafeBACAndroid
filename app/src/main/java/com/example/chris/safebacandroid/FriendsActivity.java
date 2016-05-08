package com.example.chris.safebacandroid;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FriendsActivity extends Activity {
  ListView friendsListView;
  EditText friendEmailEditText;
  Button addButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_friends);

    friendsListView = (ListView) findViewById(R.id.friends_list);
    addButton = (Button) findViewById(R.id.add_friend_button);
    friendEmailEditText = (EditText) findViewById(R.id.friend_email);

    addButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        new AddFriendTask(friendEmailEditText.getText().toString()).execute((Void)null);
      }
    });

    new GetFriendsTask().execute((Void)null);
  }

  /**
   * Sets the adapter for the friendsListView
   * @param friendItems items to be shown in the friendsListView
   */
  private void setAdapter(List<FriendItem> friendItems) {
    friendsListView.setAdapter(new FriendAdapter(this, R.layout.friend_list_item, friendItems));
  }

  /**
   * Parses json array of friends and calls the methods to display them.
   * @param jsonArray json array of friends.
   */
  private void loadFriends(JSONArray jsonArray) {
    List<FriendItem> friendsList = new ArrayList<>();
    try {
      for (int i = 0; i < jsonArray.length(); i++) {
        JSONObject friendObject = jsonArray.getJSONObject(i);
        Long id = friendObject.getLong("id");
        String name = friendObject.getString("name");
        Boolean bacVisible = friendObject.getBoolean("visible");
        Double bac;
        if (friendObject.has("bac")) {
          bac = friendObject.getDouble("bac");
        } else {
          bac = -1.0;
        }
        friendsList.add(new FriendItem(id, name, "", bac, bacVisible));
      }
      setAdapter(friendsList);
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  /**
   * Handles call to the server to get list of friends.
   */
  public class GetFriendsTask extends AsyncTask<Void, Void, JSONArray> {
    @Override
    protected JSONArray doInBackground(Void... params) {
      return APICaller.getFriends();
    }

    @Override
    protected void onPostExecute(JSONArray jsonArray) {
      loadFriends(jsonArray);
    }
  }

  /**
   * Handles call to the server to add a friend by email.
   */
  public class AddFriendTask extends AsyncTask<Void, Void, JSONArray> {
    String email;
    public AddFriendTask(String email) {
      this.email = email;
    }

    @Override
    protected JSONArray doInBackground(Void... params) {
      try {
        return APICaller.addFriend(email);
      } catch (Error e) {
        e.printStackTrace();
        return null;
      }
    }

    @Override
    protected void onPostExecute(JSONArray jsonArray) {
      if (jsonArray == null) {
        Toast toast = Toast.makeText(getApplicationContext(), "Email not found.", Toast.LENGTH_SHORT);
        toast.show();
      } else {
        Toast toast = Toast.makeText(getApplicationContext(), "Friend added.", Toast.LENGTH_SHORT);
        toast.show();
        loadFriends(jsonArray);
      }
    }
  }
}

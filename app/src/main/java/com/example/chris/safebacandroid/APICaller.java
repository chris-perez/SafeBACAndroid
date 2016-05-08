package com.example.chris.safebacandroid;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by cperez3 on 6/16/2015.
 */
public class APICaller {
  static final String BASE_URL = "http://ec2-52-34-240-224.us-west-2.compute.amazonaws.com:9000";
  public static String authID = "";
  static final String MANAGE_AUTH_PATH = "/user/auth";
  static final String MANAGE_PROFILE_PATH = "/user/profile";
  static final String MANAGE_DRINKS_PATH = "/drinks";
  static final String DRINK_HISTORY_PATH = "/drinks/history";
  static final String FRIENDS_PATH = "/user/friends";

  static final String GET = "GET", PUT = "PUT", POST = "POST", DELETE = "DELETE";

  /**
   * Calls the Create Account method on the server.
   * @param request account info as json
   * @return user info as json, including authID
   */
  public static JSONObject createAccount(JSONObject request) {
    return getJsonObject(call(MANAGE_AUTH_PATH, POST, null, request));
  }

  /**
   * Calls the Login method on the server.
   * @param request login info as json
   * @return user info as json, including authID
   */
  public static JSONObject login(JSONObject request) {
    return getJsonObject(call(MANAGE_AUTH_PATH, PUT, null, request));
  }

  /**
   * Calls the Update Profile method on the server.
   * @param request new user info as json
   * @return user info as json
   */
  public static JSONObject updateProfile(JSONObject request) {
    return getJsonObject(call(MANAGE_PROFILE_PATH, PUT, null, request));
  }

  /**
   * Calls the Get Drink History method on the server.
   * @return array of drinks consumed as json
   */
  public static JSONArray getDrinkLog() {
    return getJsonArray(call(DRINK_HISTORY_PATH, GET, null, null));
  }

  /**
   * Calls the Get Friends method on the server.
   * @return list of friend user info as json
   */
  public static JSONArray getFriends() {
    return getJsonArray(call(FRIENDS_PATH, GET, null, null));
  }

  /**
   * Calls the Add Friend method on the server.
   * @param email email of friend to add
   * @return list of all friend user info as json
   */
  public static JSONArray addFriend(String email) {
    Map<String, String> params = new HashMap<>();
    params.put("email", email);
    return getJsonArray(call(FRIENDS_PATH, PUT, params, new JSONObject()));
  }

  /**
   * Calls the Set BAC Visibility method on the server
   * @param id id of friend to set visibility for
   * @param visible if the user's BAC should be visible to the given friend
   */
  public static void setVisibility(Long id, Boolean visible) {
    Map<String, String> params = new HashMap<>();
    params.put("id", id+"");
    params.put("visible", visible+"");
    call(FRIENDS_PATH, POST, params, new JSONObject());
  }

  /**
   * Calls the Get Profile method on the server.
   * @return user profile info as json
   */
  public static JSONObject getProfile() {
    return getJsonObject(call(MANAGE_PROFILE_PATH, GET, null, null));
  }

  /**
   * Calls the Get Catalog method on the server
   * @return list of all drinks in the database as json
   */
  public static JSONArray openCatalog(){
    return getJsonArray(call(MANAGE_DRINKS_PATH, GET, null, null));
  }

  /**
   * unused function
   */
  public static JSONObject submitDrink(JSONObject request) {
    return getJsonObject(call(MANAGE_DRINKS_PATH, PUT, null, request));
  }

  public static String getCall(){
    return call("/drinks","GET",null, null);
  }

  /**
   * Makes a call to the server and returns its response as a String.
   * @param path relative path of the http method
   * @param method type of request (GET, PUT, POST, DELETE)
   * @param params map of any url parameters
   * @param object json to server
   * @return server response as string
   */
  private static String call(String path, String method, Map<String, String> params, JSONObject object) {
    // These two need to be declared outside the try/catch
    // so that they can be closed in the finally block.
    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;

    try {
      // Construct the URL for the OpenWeatherMap query
      // Possible parameters are avaiable at OWM's forecast API page, at
      // http://openweathermap.org/API#forecast


      Uri.Builder uriBuilder = Uri.parse(BASE_URL + path).buildUpon();

      if (params != null) {
        Iterator iterator = params.entrySet().iterator();
        while (iterator.hasNext()) {
          Map.Entry pair = (Map.Entry)iterator.next();
          uriBuilder.appendQueryParameter(pair.getKey().toString(), pair.getValue().toString());
        }
      }

      Uri builtUri = uriBuilder.build();
      URL url = new URL(builtUri.toString());

      // Create the request to OpenWeatherMap, and open the connection
      urlConnection = (HttpURLConnection) url.openConnection();
      urlConnection.setRequestMethod(method.toUpperCase());
      urlConnection.setRequestProperty("Content-Type", "text/json");
      if (!authID.isEmpty()) {
        urlConnection.setRequestProperty("X-Auth-Token", authID);
      }
      urlConnection.setDoInput(true);
      urlConnection.setDoOutput(false);

      if (object != null) {
        urlConnection.getOutputStream().write(object.toString().getBytes());
      }

      urlConnection.connect();
      int responseCode = urlConnection.getResponseCode();
      String responseMessage  = urlConnection.getResponseMessage();
      Log.i("Response code", Integer.toString(responseCode));
      Log.i("Response message", responseMessage);

      InputStream inputStream;
      if (responseCode >= 500) {
        inputStream = urlConnection.getErrorStream();
      } else if (responseCode >= 300) {
        inputStream = urlConnection.getErrorStream();
      } else {
          inputStream = urlConnection.getInputStream();
      }

      // Read the input stream into a String

      StringBuffer buffer = new StringBuffer();
      if (inputStream == null) {
        // Nothing to do.
        Log.e("APICaller", "No input stream.");
        return null;
      }
      reader = new BufferedReader(new InputStreamReader(inputStream));

      String line;
      while ((line = reader.readLine()) != null) {
        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
        // But it does make debugging a *lot* easier if you print out the completed
        // buffer for debugging.
        buffer.append(line).append("\n");
      }
      Log.i("Response string", buffer.toString());

      if (buffer.length() == 0) {
        // Stream was empty.  No point in parsing.
        Log.e("APICaller", "Empty response.");
        return null;
      }
      return buffer.toString();
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    } finally {
      if (urlConnection != null) {
        urlConnection.disconnect();
      }
      if (reader != null) {
        try {
          reader.close();
        } catch (final IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * Converts a string to a JsonObject
   * @param result object as string
   * @return object as JSONObject
   */
  private static JSONObject getJsonObject(String result) {
    try {
      JSONObject content = new JSONObject(result);
      Log.i("APICaller Response", content.toString());
      return content;
    } catch (JSONException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Converts a string to a JSONArray
   * @param result object as string
   * @return object as JSONArray
   */

  private static JSONArray getJsonArray(String result) {
    try {
      JSONArray content = new JSONArray(result);
      Log.i("APICaller Response", content.toString());
      return content;
    } catch (JSONException e) {
      try {
        JSONObject content = new JSONObject(result);
        String message = content.getString("error");
        Log.e("APICaller", message);
      } catch (JSONException e2){}
      e.printStackTrace();
      return null;
    }
  }
}

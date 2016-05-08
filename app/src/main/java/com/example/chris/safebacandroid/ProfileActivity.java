package com.example.chris.safebacandroid;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ProfileActivity extends Activity {
  private EditText mNameView;
  private EditText mEmailView;
  private EditText mBirthdateView;

  private EditText mWeightView;
  private Spinner mSexSpinner;

  private DatePickerDialog mBirthdateDialog;
  private SimpleDateFormat dateFormatter;

  private Button mUpdateButton;

  ArrayAdapter<CharSequence> adapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_profile);
    mNameView = (EditText) findViewById(R.id.name);
    // Set up the login form.
    mEmailView = (EditText) findViewById(R.id.email);

    dateFormatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
    mBirthdateView = (EditText) findViewById(R.id.birthdate);
    mBirthdateView.setInputType(InputType.TYPE_NULL);
    mBirthdateView.requestFocus();
    setDateTimeField();

    mWeightView = (EditText) findViewById(R.id.weight);
    mSexSpinner = (Spinner) findViewById(R.id.sex);

    adapter = ArrayAdapter.createFromResource(this, R.array.sex_array,
        android.R.layout.simple_spinner_item);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    mSexSpinner.setAdapter(adapter);

    mUpdateButton = (Button) findViewById(R.id.button_update);
    mUpdateButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        updateProfile();
      }
    });

    new GetProfileTask().execute((Void) null);
  }

  /**
   * Parses form information and runs an UpdateProfileTask
   */
  private void updateProfile() {
    // Store values at the time of the login attempt.
    String name = mNameView.getText().toString();
    String email = mEmailView.getText().toString();
    String birthdate = mBirthdateView.getText().toString();
    int weight = Integer.parseInt(mWeightView.getText().toString());
    String sex = mSexSpinner.getSelectedItem().toString();

    long date = -1;

    boolean cancel = false;
    View focusView = null;

    if (TextUtils.isEmpty(birthdate)) {
      mBirthdateView.setError(getString(R.string.error_field_required));
      focusView = mBirthdateView;
      cancel = true;
    } else {
      try {
        date = dateFormatter.parse(birthdate).getTime();
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }

    if (TextUtils.isEmpty(name)) {
      mNameView.setError(getString(R.string.error_field_required));
      focusView = mNameView;
      cancel = true;
    }


    // Check for a valid email address.
    if (TextUtils.isEmpty(email)) {
      mEmailView.setError(getString(R.string.error_field_required));
      focusView = mEmailView;
      cancel = true;
    } else if (!isEmailValid(email)) {
      mEmailView.setError(getString(R.string.error_invalid_email));
      focusView = mEmailView;
      cancel = true;
    }

    if (TextUtils.isEmpty(mWeightView.getText())) {
      mWeightView.setError(getString(R.string.error_field_required));
      focusView = mWeightView;
      cancel = true;
    } else if (weight < 0) {
      mWeightView.setError("Weight must be greater than 0.");
      focusView = mWeightView;
      cancel = true;
    }


    if (cancel) {
      // There was an error; don't attempt login and focus the first
      // form field with an error.
      focusView.requestFocus();
    } else {
      // Show a progress spinner, and kick off a background task to
      // perform the user login attempt.
      new UpdateProfileTask(name, email, date, sex, weight).execute((Void) null);
    }
  }

  /**
   * Checks if the email given has the correct format.
   * @param email email in question
   * @return if the email is in the valid format
   */
  private boolean isEmailValid(String email) {
    return email.contains("@");
  }

  /**
   * Handles date format with Date Picker
   */
  private void setDateTimeField() {
    mBirthdateView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mBirthdateDialog.show();
      }
    });

    Calendar newCalendar = Calendar.getInstance();
    mBirthdateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
      @Override
      public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar newDate = Calendar.getInstance();
        newDate.set(year, monthOfYear, dayOfMonth);
        mBirthdateView.setText(dateFormatter.format(newDate.getTime()));
      }

    }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
  }

  /**
   * Handles call to the server to get profile information.
   */
  public class GetProfileTask extends AsyncTask<Void, Void, JSONObject> {
    @Override
    protected JSONObject doInBackground(Void... params) {
      return APICaller.getProfile();
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
      if (jsonObject == null) {
        return;
      }
      try {
        String name = jsonObject.getString("name");
        String email = jsonObject.getString("email");
        String sex = jsonObject.getString("sex");
        int weight = jsonObject.getInt("weight");
        Long birthDate = jsonObject.getLong("birthDate");

        mNameView.setText(name);
        mEmailView.setText(email);
        mBirthdateView.setText(dateFormatter.format(new Date(birthDate)));
        mWeightView.setText(weight+"");
        int spinnerPosition = adapter.getPosition(sex);
        mSexSpinner.setSelection(spinnerPosition);
      }catch (JSONException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Handles call to the server to update profile information.
   */
  public class UpdateProfileTask extends AsyncTask<Void, Void, JSONObject> {
    private final String mName;
    private final String mEmail;
    private final long mDate;
    private final String sex;
    private final int weight;

    UpdateProfileTask(String name, String email, long date, String sex, int weight) {
      mName = name;
      mEmail = email;
      mDate = date;
      this.sex = sex;
      this.weight = weight;
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
      JSONObject request = new JSONObject();
      try {
        request.put("name", mName);
        request.put("email", mEmail);
        request.put("birthDate", mDate);
        request.put("sex", sex);
        request.put("weight", weight);
      } catch (JSONException e) {
        e.printStackTrace();
        return null;
      }
      return APICaller.updateProfile(request);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
      Toast.makeText(getApplicationContext(), values[0].toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPostExecute(final JSONObject response) {
      if (response.has("error")) {
        try {
          Toast.makeText(getApplicationContext(), response.getString("error"), Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
          e.printStackTrace();
        }
      } else {
        Toast toast = Toast.makeText(getApplicationContext(), "Account Updated!", Toast.LENGTH_SHORT);
        toast.show();

        finish();
      }
    }
  }
}

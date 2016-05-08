package com.example.chris.safebacandroid;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.READ_CONTACTS;


/**
 * A login screen that offers login via email/password.
 */
public class SignUp extends Activity implements LoaderCallbacks<Cursor>, OnClickListener{
  public static final String PREFS_NAME = "SafeBACPrefs";
  /**
   * Keep track of the login task to ensure we can cancel it if requested.
   */
  private UserLoginTask mAuthTask = null;

  // UI references.
  private EditText mNameView;
  private EditText mEmailView;
  private EditText mPasswordView;
  private EditText mBirthdateView;

  private EditText mWeightView;
  private Spinner mSexSpinner;

  private Calendar birhtdate;

  private DatePickerDialog mBirthdateDialog;
  private SimpleDateFormat dateFormatter;

  private View mProgressView;
  private View mLoginFormView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sign_up);

    mNameView = (EditText) findViewById(R.id.name);
    // Set up the login form.
    mEmailView = (EditText) findViewById(R.id.email);
    populateAutoComplete();

    mPasswordView = (EditText) findViewById(R.id.password);
    mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
        if (id == R.id.login || id == EditorInfo.IME_NULL) {
          attemptLogin();
          return true;
        }
        return false;
      }
    });

    dateFormatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
    mBirthdateView = (EditText) findViewById(R.id.birthdate);
    mBirthdateView.setInputType(InputType.TYPE_NULL);
    mBirthdateView.requestFocus();
    setDateTimeField();

    mWeightView = (EditText) findViewById(R.id.weight);
    mSexSpinner = (Spinner) findViewById(R.id.sex);

    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.sex_array,
        android.R.layout.simple_spinner_item);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    mSexSpinner.setAdapter(adapter);

    Button mEmailSignInButton = (Button) findViewById(R.id.button_signup_form);
    mEmailSignInButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        attemptLogin();
      }
    });

    mLoginFormView = findViewById(R.id.email_signup_form);
    mProgressView = findViewById(R.id.login_progress);


  }

  private void setDateTimeField() {
    mBirthdateView.setOnClickListener(this);

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

  @Override
  public void onClick(View v) {
    if (v == mBirthdateView) {
      mBirthdateDialog.show();
    }
  }

  private void populateAutoComplete() {
    getLoaderManager().initLoader(0, null, this);
  }


  /**
   * Attempts to sign in or register the account specified by the login form.
   * If there are form errors (invalid email, missing fields, etc.), the
   * errors are presented and no actual login attempt is made.
   */
  public void attemptLogin() {
    if (mAuthTask != null) {
      return;
    }

    // Reset errors.
    mNameView.setError(null);
    mEmailView.setError(null);
    mPasswordView.setError(null);
    mBirthdateView.setError(null);
    mWeightView.setError(null);

    // Store values at the time of the login attempt.
    String name = mNameView.getText().toString();
    String email = mEmailView.getText().toString();
    String password = mPasswordView.getText().toString();
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

    // Check for a valid password, if the user entered one.
    if (TextUtils.isEmpty(password)) {
      mPasswordView.setError(getString(R.string.error_field_required));
      focusView = mPasswordView;
      cancel = true;
    } else if (!isPasswordValid(password)) {
      mPasswordView.setError(getString(R.string.error_invalid_password));
      focusView = mPasswordView;
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
      showProgress(true);
      mAuthTask = new UserLoginTask(name, email, password, date, sex, weight);
      mAuthTask.execute((Void) null);
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
   * Checks that the given password matches password requirements.
   * @param password password in question
   * @return if the password matches password requirements
   */
  private boolean isPasswordValid(String password) {
    return password.length() > 4;
  }

  /**
   * Shows the progress UI and hides the login form.
   */
  @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
  public void showProgress(final boolean show) {
    // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
    // for very easy animations. If available, use these APIs to fade-in
    // the progress spinner.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
      int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

      mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
      mLoginFormView.animate().setDuration(shortAnimTime).alpha(
          show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
          mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
      });

      mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
      mProgressView.animate().setDuration(shortAnimTime).alpha(
          show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
          mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
      });
    } else {
      // The ViewPropertyAnimator APIs are not available, so simply show
      // and hide the relevant UI components.
      mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
      mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    findViewById(R.id.error_message).setVisibility(show ? View.INVISIBLE: View.VISIBLE);
  }

  @Override
  public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
    if (!mayRequestContacts()) {
      return null;
    }
    return new CursorLoader(this,
        // Retrieve data rows for the device user's 'profile' contact.
        Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
            ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

        // Select only email addresses.
        ContactsContract.Contacts.Data.MIMETYPE +
            " = ?", new String[]{ContactsContract.CommonDataKinds.Email
        .CONTENT_ITEM_TYPE},

        // Show primary email addresses first. Note that there won't be
        // a primary email address if the user hasn't specified one.
        ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
  }

  /**
   * @return if the app has contacts permissions from the device
   */
  private boolean mayRequestContacts() {
    return checkCallingOrSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
  }

  @Override
  public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
    List<String> emails = new ArrayList<String>();
    cursor.moveToFirst();
    while (!cursor.isAfterLast()) {
      emails.add(cursor.getString(ProfileQuery.ADDRESS));
      cursor.moveToNext();
    }
  }

  @Override
  public void onLoaderReset(Loader<Cursor> cursorLoader) {

  }

  private interface ProfileQuery {
    String[] PROJECTION = {
        ContactsContract.CommonDataKinds.Email.ADDRESS,
        ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
    };

    int ADDRESS = 0;
    int IS_PRIMARY = 1;
  }

  /**
   * Represents an asynchronous login/registration task used to authenticate
   * the user.
   */
  public class UserLoginTask extends AsyncTask<Void, Void, JSONObject> {

    private final String mName;
    private final String mEmail;
    private final String mPassword;
    private final long mDate;
    private final String sex;
    private final int weight;

    UserLoginTask(String name, String email, String password, long date, String sex, int weight) {
      mName = name;
      mEmail = email;
      mPassword = password;
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
        request.put("password", mPassword);
        request.put("birthDate", mDate);
        request.put("sex", sex);
        request.put("weight", weight);
      } catch (JSONException e) {
        e.printStackTrace();
        return null;
      }
      return APICaller.createAccount(request);
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
        mAuthTask = null;
        showProgress(false);
      } else {
        String authID;
        try {
          authID = response.getString("authID");
        } catch (JSONException e) {
          e.printStackTrace();
          return;
        }

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor settingsEditor = settings.edit();
        settingsEditor.putString("authID", authID);
        settingsEditor.commit();

        Toast toast = Toast.makeText(getApplicationContext(), "Create Account successful!", Toast.LENGTH_SHORT);
        toast.show();
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(intent);
        finish();
      }
    }

    @Override
    protected void onCancelled() {
      mAuthTask = null;
      showProgress(false);
    }
  }
}


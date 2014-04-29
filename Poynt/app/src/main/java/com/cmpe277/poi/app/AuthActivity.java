package com.cmpe277.poi.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class AuthActivity extends Activity {
    private final boolean DEBUG_AUTH = false;

    private final String TAG = "AuthActivity";
    private final String FONT_PATH = "fonts/ExistenceLight.otf";

    private TextView registerButton;
    private TextView loginButton;
    private EditText username;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if ((currentUser != null) && !DEBUG_AUTH) {
            // User is still logged in.  Proceed to show the app's main activity
            finish();
            showMainActivity();
        } else {
            if(DEBUG_AUTH && (currentUser != null))
                ParseUser.logOut();

            // Get references to our buttons
            registerButton = ((TextView)findViewById(R.id.register_button));
            loginButton = ((TextView)findViewById(R.id.login_button));

            // Get references to our fields
            username = ((EditText)findViewById(R.id.username));
            password = ((EditText)findViewById(R.id.password));

            // Set the font type
            setTypeFaces();
        }
    }

    private void setTypeFaces()
    {
        TextView appLabel = (TextView)findViewById(R.id.appLabel);
        /*EditText userName = (EditText)findViewById(R.id.username);
        EditText password = (EditText)findViewById(R.id.password);
        TextView register = (TextView)findViewById(R.id.register_button);
        TextView login = (TextView)findViewById(R.id.login_button);*/

        Typeface tf = Typeface.createFromAsset(getAssets(), FONT_PATH);

        appLabel.setTypeface(tf);
        /*userName.setTypeface(tf);
        password.setTypeface(tf);
        register.setTypeface(tf);
        login.setTypeface(tf);*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.auth, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean fieldsValid(String field1, String field2) {
        return !(field1.isEmpty() || field2.isEmpty());
    }

    public void onClickRegisterButton(View v) {
        String username = this.username.getText().toString();
        String password = this.password.getText().toString();

        if(!fieldsValid(username, password)) {
            showToast("All fields must be filled out.");
            return;
        }

        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);

        Log.v(TAG, "Register button clicked. User:" + username + ", Password:" + password);

        // TODO: Require email in the future!
        //user.setEmail("email@example.com");

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Log.v(TAG, "Registration successful!");
                    finish();
                    showMainActivity();
                } else {
                    String errorMessage = "";

                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                    if(e.getCode() == ParseException.USERNAME_TAKEN)
                    {
                        errorMessage = "Username already taken.";
                    }
                    else if(e.getCode() == ParseException.PASSWORD_MISSING)
                    {
                        errorMessage = "A password is required.";
                    }
                    else if(e.getCode() == ParseException.USERNAME_MISSING)
                    {
                        errorMessage = "A username is required.";
                    }
                    else
                    {
                        errorMessage = "Unknown error occurred. Code: " + e.getCode();
                    }

                    Log.v(TAG, "Register - Error: " + errorMessage);
                    showToast(errorMessage);
                }
            }
        });
    }

    private void showMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up );
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public void onClickLoginButton(View v) {
        Log.v(TAG, "Login button clicked.");
        String username = this.username.getText().toString();
        String password = this.password.getText().toString();

        if(!fieldsValid(username, password)) {
            showToast("All fields must be filled out.");
            return;
        }

        ParseUser.logInInBackground(username, password,
                new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            Log.v(TAG, "Login was successful.");
                            // Hooray! The user is logged in.
                            finish();
                            showMainActivity();
                        } else {
                            Log.v(TAG, "Login was not successful.");

                            // Login failed. Look at the ParseException to see what happened.
                            String errorMessage = "";

                            if(e.getCode() == ParseException.OBJECT_NOT_FOUND)
                            {
                                errorMessage = "Username not found.";
                            }
                            else
                            {
                                errorMessage = "Unknown error occurred. Code: " + e.getCode();
                            }

                            Log.v(TAG, "Login - Error: " + errorMessage);
                            showToast(errorMessage);
                        }
                    }
                });
    }
}

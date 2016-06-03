package eu.alfred.socialgroupsapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import eu.alfred.internal.wrapper.authentication.AuthenticatedUser;
import eu.alfred.internal.wrapper.authentication.AuthenticationException;
import eu.alfred.internal.wrapper.authentication.AuthenticationServerWrapper;
import eu.alfred.internal.wrapper.authentication.login.LoginData;
import eu.alfred.internal.wrapper.authentication.login.LoginDataException;


public class LoginActivity extends FragmentActivity {

    private EditText mEmailView, mPasswordView;
    private SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(preferences.contains("id")) {
            Intent goToMainIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(goToMainIntent);
            finish();
        }

        mEmailView = (EditText) findViewById(R.id.email);
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

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                LoginData.Builder loginBuilder = new LoginData.Builder();

                try {
                    loginBuilder.setEmail(mEmailView.getText().toString());
                } catch (LoginDataException e) {
                    //mEmailView.setError("email invalid");
                    Log.e("LoginDataException", e.toString());
                    return;
                }

                try {
                    loginBuilder.setPassword(mPasswordView.getText().toString());
                } catch (LoginDataException e) {
                    //mPasswordView.setError("password needed");
                    Log.e("LoginDataException", e.toString());
                    return;
                }

                try {
                    AuthenticationServerWrapper authWrapper = new AuthenticationServerWrapper();
                    AuthenticatedUser authenticatedUser = authWrapper.login(loginBuilder.create());
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("id", authenticatedUser.getUserId());
                    Log.d("User Id", authenticatedUser.getUserId());
                    editor.putString("token", authenticatedUser.getAccessToken());
                    editor.commit();

                    Intent goToMainIntent = new Intent(getApplicationContext(), MainActivity.class);
                    //goToMainIntent.putExtra("User", authenticatedUser.getUserId());
                    startActivity(goToMainIntent);
                    finish();
                } catch (AuthenticationException e) {
                    //result.setText(e.getMessage());
                    Log.e("AuthenticationException", e.getMessage());
                }
            }
        }).start();
    }

}

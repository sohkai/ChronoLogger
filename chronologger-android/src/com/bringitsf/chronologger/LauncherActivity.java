package com.bringitsf.chronologger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class LauncherActivity extends Activity {
    
    private static final String TAG = LauncherActivity.class.getName();
    private Button mLoginButton;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //FIXME: go to main activity if they were last there instead of making this activity
        setContentView(R.layout.activity_launcher);
        
        EditText passwordText = (EditText) findViewById(R.id.login_password);
        // Use actionGo on login_password to login if enter is pressed
        passwordText.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    login();
                    handled = true;
                }
                return handled;
            }
        });
        
        mLoginButton = (Button) findViewById(R.id.login_button);
        mLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setEnabled(false);
                login();
            }
        });
        
        TextView forgotPassword = (TextView) findViewById(R.id.login_forget_password);
        forgotPassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                goToForgotPasswordLink();
            }
        });
        
        mProgressBar = (ProgressBar) findViewById(R.id.login_progress_bar);
    }
    
    private void login() {
        mProgressBar.setVisibility(View.VISIBLE);
        //FIXME: web team didn't want to make legit logins, so here we are
        Intent mainIntent = new Intent(this, MainActivity.class);
        EditText username = (EditText) findViewById(R.id.login_username);
        //FIXME: this is terrible but so is this whole login part
        //FIXME: use username
        DefaultSharedPrefs.putString(DefaultSharedPrefs.EXTRA_USER_EMAIL, "user1@jeff.com"/*username.getText().toString()*/);
        Log.i(TAG, "Starting main activity");
        startActivity(mainIntent);
        resetLoginState();
        
    }
    
    private void goToForgotPasswordLink() {
        Toast.makeText(getApplication(), R.string.toast_forgot_password, Toast.LENGTH_SHORT).show();
    }
    
    private void resetLoginState() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mLoginButton.setEnabled(true);
    }
    
}

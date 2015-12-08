package com.example.weiranfang.crowdtaskmanager;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    Button loginButton;
    EditText userNameEditTest, passwordEditText;
    UserLocalStore userLocalStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        userNameEditTest = (EditText) findViewById(R.id.usernameEditText);
        loginButton = (Button) findViewById(R.id.loginButton);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        userLocalStore = new UserLocalStore(this);
    }


    public void clickLoginButton(View view) {
        String username = userNameEditTest.getText().toString();
        String password = passwordEditText.getText().toString();

        User user =  new User(username, password);

        authenticate(user);
    }

    private void authenticate(User user) {
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.fetchUserDataInBackground(user, new GetUserCallBack() {
            @Override
            public void done(User returnedUser) {
                if (returnedUser == null) {
                    showErrorMessage();
                } else {
                    logUserIn(returnedUser);
                }
            }
        });
    }

    private void logUserIn(User returnedUser) {
        userLocalStore.storeUserData(returnedUser);
        userLocalStore.setUserLoggedIn(true);
        startActivity(new Intent(this, MainActivity.class));
    }

    private void showErrorMessage() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoginActivity.this);
        dialogBuilder.setMessage("Incorrect user details");
        dialogBuilder.setPositiveButton("OK", null);
        dialogBuilder.show();
    }

    public void navigateToRegister(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }
}

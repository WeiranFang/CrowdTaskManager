package com.example.weiranfang.crowdtaskmanager;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class UserPreferencesActivity extends AppCompatActivity {

    private TextView usernameTextView;
    private TextView emailTextView;
    private Spinner maxDurationSpinner;
    private EditText minAwardEditText;
    private EditText maxDistanceEditText;

    private UserLocalStore userLocalStore;

    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_preferences);

        usernameTextView = (TextView) findViewById(R.id.usernameTextView);
        emailTextView = (TextView) findViewById(R.id.emailTextView);
        maxDurationSpinner = (Spinner) findViewById(R.id.maxDurationSpinner);
        minAwardEditText = (EditText) findViewById(R.id.minAwardEditText);
        maxDistanceEditText = (EditText) findViewById(R.id.maxDistEditText);

        userLocalStore = new UserLocalStore(this);
        currentUser = userLocalStore.getLoggedInUser();

        usernameTextView.setText(currentUser.username);
        emailTextView.setText(currentUser.email);

        showUserPreferences();
    }

    private void showUserPreferences() {
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.fetchPreferencesInBackground(currentUser, new GetPreferencesCallBack() {
            @Override
            public void done(UserPreferences fetchedPreferences) {
                if (fetchedPreferences == null) {
                    showErrorMessage("No preferences");
                } else {
                    maxDistanceEditText.setText(fetchedPreferences.maxDistance + "", TextView.BufferType.EDITABLE);
                    maxDurationSpinner.setSelection(fetchedPreferences.maxDuration);
                    minAwardEditText.setText(fetchedPreferences.minAward + "", TextView.BufferType.EDITABLE);
                }
            }
        });
    }

    public void clickSaveButton(View view) {
        int minAward;
        try {
            minAward = Integer.parseInt(minAwardEditText.getText().toString());
        } catch (NumberFormatException e) {
            showErrorMessage("Invalid award!");
            return;
        }
        if (minAward < 0) {
            showErrorMessage("Invalid award!");
            return;
        }

        int maxDuration = maxDurationSpinner.getSelectedItemPosition();

        int maxDistance;
        try {
            maxDistance = Integer.parseInt(maxDistanceEditText.getText().toString());
        } catch (NumberFormatException e) {
            showErrorMessage("Invalid distance!");
            return;
        }
        if (maxDistance < 0) {
            showErrorMessage("Invalid distance!");
            return;
        }

        UserPreferences userPreferences = new UserPreferences(currentUser.userId, minAward, maxDuration, maxDistance);

        // Update preferences on the server
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.updatePreferencesInBackground(userPreferences, new GetPreferencesCallBack() {
            @Override
            public void done(UserPreferences returnedUserPreferences) {
                startActivity(new Intent(UserPreferencesActivity.this, MainActivity.class));
            }
        });
    }

    private void showErrorMessage(String message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(UserPreferencesActivity.this);
        dialogBuilder.setMessage("Error:" + message);
        dialogBuilder.setPositiveButton("OK", null);
        dialogBuilder.show();
    }
}

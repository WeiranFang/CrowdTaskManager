package com.example.weiranfang.crowdtaskmanager;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    private UserLocalStore userLocalStore;
    private TextView helloTextView;

    private LocationManager locationManager;

    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userLocalStore = new UserLocalStore(this);

        helloTextView = (TextView) findViewById(R.id.helloTextView);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                userLocalStore.setCurrentLocation(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d("Latitude", "status");
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d("Latitude", "enable");

            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d("Latitude", "disable");
            }
        };

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_logout) {
            logout();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (authenticate()) {
            displayUserDetails();
        } else {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }

    private void displayUserDetails() {
        User currentUser = userLocalStore.getLoggedInUser();
        helloTextView.setText("Hello, " + currentUser.username + "!" + " UserID: " + currentUser.userId);
    }

    private boolean authenticate() {
        return userLocalStore.getUserLoggedIn();
    }

    public void logout() {
        userLocalStore.clearUserData();
        userLocalStore.setUserLoggedIn(false);
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void clickTaskListButton(View view) {
        startActivity(new Intent(this, TaskListActivity.class));
    }

    public void clickCreateTaskButton(View view) {
        startActivity(new Intent(this, CreateTaskActivity.class));
    }


}

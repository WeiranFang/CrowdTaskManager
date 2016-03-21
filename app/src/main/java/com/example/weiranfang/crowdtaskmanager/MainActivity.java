package com.example.weiranfang.crowdtaskmanager;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.WeakHashMap;


public class MainActivity extends AppCompatActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";
    private boolean isReceiverRegistered;
    private BroadcastReceiver mRegistrationBroadcastReceiver;


    private UserLocalStore userLocalStore;
    private TextView helloTextView;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private GoogleMap googleMap;
    private HashMap<LatLng, Task> latLngTaskMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userLocalStore = new UserLocalStore(this);

        // Add listener on the location api
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

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

//        mapFragment.getMapAsync(this);
        googleMap = mapFragment.getMap();

        latLngTaskMap = new HashMap<>();

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    String token = intent.getStringExtra("token");

                    Toast.makeText(getApplicationContext(), "GCM registration token: " + token, Toast.LENGTH_LONG).show();

                } else if (intent.getAction().equals(Config.SENT_TOKEN_TO_SERVER)) {
                    // gcm registration id is stored in our server's MySQL

                    Toast.makeText(getApplicationContext(), "GCM registration token is stored in server!", Toast.LENGTH_LONG).show();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    Toast.makeText(getApplicationContext(), "Push notification is received!", Toast.LENGTH_LONG).show();
                }
            }
        };

        if (checkPlayServices()) {
            registerGCM();
        }

    }

    // starting the service to register with GCM
    private void registerGCM() {
        Intent intent = new Intent(this, GcmIntentService.class);
        intent.putExtra("key", "register");
        startService(intent);
    }

    /**
     * Make connection to the server and download nearby tasks based on user's preferences.
     */
    private void downloadTasks() {
        User currentUser = userLocalStore.getLoggedInUser();
        LatLng currentLatLng = new LatLng(userLocalStore.getCurrentLatitude(), userLocalStore.getCurrentLongitude());
        // Download task data
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.fetchTaskDataInBackground(currentUser, currentLatLng, new GetJsonCallBack() {
            @Override
            public void done(JSONArray fetchedJsonArray) {
                if (fetchedJsonArray == null || fetchedJsonArray.length() == 0) {
                    showErrorMessage();
                } else {
                    ArrayList<Task> tasks = getTaskList(fetchedJsonArray);
                    DataHolder.getInstance().setTasks(tasks);
                    setUpMap(tasks);
                }
            }
        });
    }

    /**
     * Set up the google map that will be displayed on the main page, including the camera and markers.
     * @param tasks
     */
    private void setUpMap(ArrayList<Task> tasks) {
        LatLng currentLatLng = new LatLng(userLocalStore.getCurrentLatitude(), userLocalStore.getCurrentLongitude());
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 6f));

        latLngTaskMap.clear();
        for (Task task : tasks) {
            LatLng taskLatLng = new LatLng(task.geoLat, task.geoLong);
            MarkerOptions markerOptions = new MarkerOptions().position(taskLatLng).title(task.title);
            Marker marker = googleMap.addMarker(markerOptions);
            latLngTaskMap.put(taskLatLng, task);
        }

        // Set up onclick listener once user click on the marker
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(MainActivity.this, TaskDetailActivity.class);
                Task mappedTask = latLngTaskMap.get(marker.getPosition());
                intent.putExtra("task", mappedTask);
                startActivity(intent);
            }
        });
    }

    /**
     * Given jasonArray, parse it to a list of Task.
     * @param jsonArray JSONArray to parse
     * @return List of tasks
     */
    private ArrayList<Task> getTaskList(JSONArray jsonArray) {
        ArrayList<Task> tasks = new ArrayList<Task>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                tasks.add(parseJsonToTask(jsonObject));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tasks;
    }

    /**
     * Given jasonObject, parse it to a Task object.
     * @param jsonObject JSONObject to parse
     * @return Result task parsed by jsonobject
     */
    private Task parseJsonToTask(JSONObject jsonObject) {
        try {
            int taskId = jsonObject.getInt("taskId");
            String title = jsonObject.getString("title");
            String content = jsonObject.getString("content");
            String createTime = jsonObject.getString("createTime");
            int creatorId = jsonObject.getInt("creatorId");
            String category = jsonObject.getString("category");
            String deadline = jsonObject.getString("deadline");
            int duration = jsonObject.getInt("duration");
            int award = jsonObject.getInt("award");
            int participants = jsonObject.getInt("participants");
            String status = jsonObject.getString("status");
            double geoLat = jsonObject.getDouble("geoLat");
            double geoLong = jsonObject.getDouble("geoLong");
            String address = jsonObject.getString("address");

            return new Task(taskId, title, content, createTime, creatorId, category,
                    deadline, duration, award, participants, status, geoLat, geoLong, address);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Set up activities to start once user clicks on the menu items.
     * @param item MenuItem that has been selected by user
     * @return If any item has been clicked
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_user_tasks) {
            startActivity(new Intent(MainActivity.this, TaskOptionActivity.class));
        }
        if (id == R.id.action_preferences) {
            startActivity(new Intent(MainActivity.this, UserPreferencesActivity.class));
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
            displayMainActivity();
        } else {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }

    private void displayMainActivity() {
        User currentUser = userLocalStore.getLoggedInUser();
//        helloTextView.setText("Hello, " + currentUser.username + "!" + " UserID: " + currentUser.userId);
        downloadTasks();
    }

    /**
     * Authenticate current user.
     * @return If current user has loggedIn
     */
    private boolean authenticate() {
        return userLocalStore.getUserLoggedIn();
    }

    /**
     * Logout current user and clean sessions.
     */
    public void logout() {
        userLocalStore.clearUserData();
        userLocalStore.setUserLoggedIn(false);
        startActivity(new Intent(this, LoginActivity.class));
    }

    /**
     * Onclick method once user clicks on the TaskList button.
     * @param view Current view
     */
    public void clickTaskListButton(View view) {
        Intent intent = new Intent(this, TaskListActivity.class);
        intent.putExtra("TaskType", "ALL");
        startActivity(intent);
    }

    /**
     * Onclick method once user clicks on the CreateTask button.
     * @param view Current view
     */
    public void clickCreateTaskButton(View view) {
        startActivity(new Intent(this, CreateTaskActivity.class));
    }

    /**
     * Onclick method once user clicks on the Refresh button.
     * @param view Current view
     */
    public void clickRefreshButton(View view) {
        finish();
        startActivity(getIntent());
    }


    private void showErrorMessage() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage("Downloading Task Failed");
        dialogBuilder.setPositiveButton("OK", null);
        dialogBuilder.show();
    }



    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}

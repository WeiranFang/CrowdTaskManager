package com.example.weiranfang.crowdtaskmanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

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


//        helloTextView = (TextView) findViewById(R.id.helloTextView);

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

    }

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

    private void setUpMap(ArrayList<Task> tasks) {
        LatLng currentLatLng = new LatLng(userLocalStore.getCurrentLatitude(), userLocalStore.getCurrentLongitude());
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 6f));

        // Map each marker to a task
        latLngTaskMap.clear();
        for (Task task : tasks) {
            LatLng taskLatLng = new LatLng(task.geoLat, task.geoLong);
            MarkerOptions markerOptions = new MarkerOptions().position(taskLatLng).title(task.title);
            Marker marker = googleMap.addMarker(markerOptions);
            latLngTaskMap.put(taskLatLng, task);
        }

//        final HashMap<Marker, Task> markerTaskHashMap = map;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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

    private boolean authenticate() {
        return userLocalStore.getUserLoggedIn();
    }

    public void logout() {
        userLocalStore.clearUserData();
        userLocalStore.setUserLoggedIn(false);
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void clickTaskListButton(View view) {
        Intent intent = new Intent(this, TaskListActivity.class);
        intent.putExtra("TaskType", "ALL");
        startActivity(intent);
    }

    public void clickCreateTaskButton(View view) {
        startActivity(new Intent(this, CreateTaskActivity.class));
    }

    public void clickRefreshButton(View view) {
        finish();
        startActivity(getIntent());
    }


//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        //TODO: Add Marker
//        LatLng currentLatLng = new LatLng(userLocalStore.getCurrentLatitude(), userLocalStore.getCurrentLongitude());
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 10f));
////        downloadTasks();
//        for (Task task : DataHolder.getInstance().getTasks()) {
//            LatLng taskLatLng = new LatLng(task.geoLat, task.geoLong);
//            googleMap.addMarker(new MarkerOptions().position(taskLatLng).title(task.title));
//        }
////        googleMap.addMarker(new MarkerOptions().position(currentLatLng).title("Current Location: "));
//    }

    private void showErrorMessage() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage("Downloading Task Failed");
        dialogBuilder.setPositiveButton("OK", null);
        dialogBuilder.show();
    }
}

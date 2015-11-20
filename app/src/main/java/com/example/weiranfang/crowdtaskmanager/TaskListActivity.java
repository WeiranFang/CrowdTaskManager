package com.example.weiranfang.crowdtaskmanager;

import android.app.AlertDialog;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TaskListActivity extends AppCompatActivity {
    public static final float METERS_NEARBY = 20000;

    ListView taskListView;

    UserLocalStore userLocalStore;

    TaskListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        taskListView = (ListView) findViewById(R.id.taskListView);
        userLocalStore = new UserLocalStore(this);

        downloadAndDisplayTasks();
    }

    private void downloadAndDisplayTasks() {
        User currentUser = userLocalStore.getLoggedInUser();

        // Download task data
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.fetchTaskDataInBackground(currentUser, new GetJsonCallBack() {
            @Override
            public void done(JSONArray fetchedJsonArray) {
                if (fetchedJsonArray == null || fetchedJsonArray.length() == 0) {
                    showErrorMessage();
                } else {
//                    displayAllTaskList(fetchedJsonArray);
                    displayNearbyTaskList(fetchedJsonArray);
                }
            }
        });
    }

    private void displayAllTaskList(final JSONArray fetchedJsonArray) {
        final ArrayList<Task> tasks = getAllTaskList(fetchedJsonArray);

        adapter = new TaskListAdapter(this, tasks);
        taskListView.setAdapter(adapter);

        taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TaskListActivity.this, TaskDetailActivity.class);
                intent.putExtra("task", tasks.get(position));
                startActivity(intent);
            }
        });
    }

    private ArrayList<Task> getAllTaskList(JSONArray jsonArray) {
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

    private ArrayList<Task> getNearbyTaskList(JSONArray jsonArray) {
        double currentLatitude = userLocalStore.getCurrentLatitude();
        double currentLongitude = userLocalStore.getCurrentLongitude();
        float[] distance = new float[1];
        ArrayList<Task> tasks = new ArrayList<Task>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Task task = parseJsonToTask(jsonObject);
                Location.distanceBetween(currentLatitude, currentLongitude, task.geoLat, task.geoLong, distance);
                if (distance[0] <= METERS_NEARBY) {
                    tasks.add(task);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tasks;
    }

    private void displayNearbyTaskList(JSONArray fetchedJsonArray) {

        final ArrayList<Task> tasks = getNearbyTaskList(fetchedJsonArray);

        adapter = new TaskListAdapter(this, tasks);
        taskListView.setAdapter(adapter);

        taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TaskListActivity.this, TaskDetailActivity.class);
                intent.putExtra("task", tasks.get(position));
                startActivity(intent);
            }
        });
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

    private void showErrorMessage() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage("Downloading Task Failed");
        dialogBuilder.setPositiveButton("OK", null);
        dialogBuilder.show();
    }


}

package com.example.weiranfang.crowdtaskmanager;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TaskOptionActivity extends AppCompatActivity {
    private UserLocalStore userLocalStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_option);

        userLocalStore = new UserLocalStore(this);

        downloadUserTasks();
    }

    /**
     * Make connection to the server to download both accepted tasks and created tasks from the
     * tasks table in the database, and save these records in the DataHolder class.
     */
    private void downloadUserTasks() {
        User currentUser = userLocalStore.getLoggedInUser();
//        LatLng currentLatLng = new LatLng(userLocalStore.getCurrentLatitude(), userLocalStore.getCurrentLongitude());
        // Download task data
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.fetchUserCreatedTasksInBackground(currentUser, new GetJsonCallBack() {
            @Override
            public void done(JSONArray fetchedJsonArray) {
                if (fetchedJsonArray == null || fetchedJsonArray.length() == 0) {
                    showErrorMessage();
                } else {
                    ArrayList<Task> tasks = getTaskList(fetchedJsonArray);
                    DataHolder.getInstance().setUserCreatedTasks(tasks);
                }
            }
        });

        serverRequests.fetchUserAcceptedTasksInBackground(currentUser, new GetJsonCallBack() {
            @Override
            public void done(JSONArray fetchedJsonArray) {
                if (fetchedJsonArray == null || fetchedJsonArray.length() == 0) {
                    showErrorMessage();
                } else {
                    ArrayList<Task> tasks = getTaskList(fetchedJsonArray);
                    DataHolder.getInstance().setUserAcceptedTasks(tasks);
                }
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

    /**
     * Onclick method once the user click on the CreatedTask Button.
     * @param view Current view
     */
    public void clickCreatedTasksButton(View view) {
        Intent intent = new Intent(this, TaskListActivity.class);
        intent.putExtra("TaskType", "CREATED");
        startActivity(intent);

    }

    /**
     * Onclick method once the user click on the AcceptedTask Button.
     * @param view Current view
     */
    public void clickAcceptedTasksButton(View view) {
        Intent intent = new Intent(this, TaskListActivity.class);
        intent.putExtra("TaskType", "ACCEPTED");
        startActivity(intent);
    }

    private void showErrorMessage() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage("Downloading Task Failed");
        dialogBuilder.setPositiveButton("OK", null);
        dialogBuilder.show();
    }
}

package com.example.weiranfang.crowdtaskmanager;

import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class TaskListActivity extends AppCompatActivity {
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
                    displayTaskList(fetchedJsonArray);
                }
            }
        });
    }

    private void displayTaskList(JSONArray fetchedJsonArray) {
        String[] titles = new String[fetchedJsonArray.length()];
        for (int i = 0; i < titles.length; i++) {
            try {
                JSONObject jsonObject = fetchedJsonArray.getJSONObject(i);
                titles[i] = jsonObject.getString("title");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1,android.R.id.text1, titles);

        adapter = new TaskListAdapter(this, fetchedJsonArray);
        taskListView.setAdapter(adapter);

        taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemValue = (String) taskListView.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),
                        "Position:" + position + "Value:" + itemValue, Toast.LENGTH_LONG).show();
            }
        });
    }



    private void showErrorMessage() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage("Downloading Task Failed");
        dialogBuilder.setPositiveButton("OK", null);
        dialogBuilder.show();
    }


}

package com.example.weiranfang.crowdtaskmanager;

import android.app.AlertDialog;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class TaskListActivity extends AppCompatActivity {
    public static final float METERS_NEARBY = 20000;

    ListView taskListView;

    UserLocalStore userLocalStore;

//    TaskListAdapter adapter;

    Spinner sortTaskSpinner;

    ArrayList<Task> allTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        taskListView = (ListView) findViewById(R.id.taskListView);
        sortTaskSpinner = (Spinner) findViewById(R.id.sortTaskSpinner);

        userLocalStore = new UserLocalStore(this);
        allTasks = DataHolder.getInstance().getTasks();

        TaskListAdapter adapter = new TaskListAdapter(this, allTasks);
        taskListView.setAdapter(adapter);
        taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TaskListActivity.this, TaskDetailActivity.class);
                intent.putExtra("task", allTasks.get(position));
                startActivity(intent);
            }
        });

        sortTaskSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        showTasksByTime();
                        break;
                    case 1:
                        showTasksByLocation();
                        break;
                    case 2:
                        showTasksByDuration();
                        break;
                    case 3:
                        showTasksByAward();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                showTasksByTime();
            }
        });



    }

    private void showTasksByTime() {
        Collections.sort(allTasks, new Comparator<Task>() {
            @Override
            public int compare(Task task1, Task task2) {
                return task2.createTime.compareTo(task1.createTime);
            }
        });

        TaskListAdapter adapter = (TaskListAdapter) taskListView.getAdapter();
        adapter.changeTaskList(allTasks);
        adapter.notifyDataSetChanged();
    }

    private void showTasksByLocation() {
        Collections.sort(allTasks, new Comparator<Task>() {
            @Override
            public int compare(Task task1, Task task2) {
                float[] dist1 = new float[1];
                float[] dist2 = new float[1];
                Location.distanceBetween(userLocalStore.getCurrentLatitude(),
                        userLocalStore.getCurrentLongitude(), task1.geoLat, task1.geoLong, dist1);
                Location.distanceBetween(userLocalStore.getCurrentLatitude(),
                        userLocalStore.getCurrentLongitude(), task2.geoLat, task2.geoLong, dist2);
                if (dist1[0] > dist2[0]) return 1;
                else if (dist1[0] < dist2[0]) return -1;
                else return 0;
            }
        });

        TaskListAdapter adapter = (TaskListAdapter) taskListView.getAdapter();
        adapter.changeTaskList(allTasks);
        adapter.notifyDataSetChanged();
//        final ArrayList<Task> tasks = tempTasks;
//
//        adapter = new TaskListAdapter(this, tasks);
//        taskListView.setAdapter(adapter);
//
//        taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(TaskListActivity.this, TaskDetailActivity.class);
//                intent.putExtra("task", tasks.get(position));
//                startActivity(intent);
//            }
//        });
    }

    private void showTasksByAward() {
//        ArrayList<Task> tempTasks = DataHolder.getInstance().getTasks();

        Collections.sort(allTasks, new Comparator<Task>() {
            @Override
            public int compare(Task task1, Task task2) {
                return task2.award - task1.award;
            }
        });

        TaskListAdapter adapter = (TaskListAdapter) taskListView.getAdapter();
        adapter.changeTaskList(allTasks);
        adapter.notifyDataSetChanged();
//        final ArrayList<Task> tasks = tempTasks;

//        adapter = new TaskListAdapter(this, tasks);
//        taskListView.setAdapter(adapter);
//
//        taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(TaskListActivity.this, TaskDetailActivity.class);
//                intent.putExtra("task", tasks.get(position));
//                startActivity(intent);
//            }
//        });
    }

    private void showTasksByDuration() {
//        ArrayList<Task> tempTasks = DataHolder.getInstance().getTasks();

        Collections.sort(allTasks, new Comparator<Task>() {
            @Override
            public int compare(Task task1, Task task2) {
                return task1.duration - task2.duration;
            }
        });
        TaskListAdapter adapter = (TaskListAdapter) taskListView.getAdapter();
        adapter.changeTaskList(allTasks);
        adapter.notifyDataSetChanged();

//        final ArrayList<Task> tasks = tempTasks;
//        adapter = new TaskListAdapter(this, tasks);
//        taskListView.setAdapter(adapter);
//
//        taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(TaskListActivity.this, TaskDetailActivity.class);
//                intent.putExtra("task", tasks.get(position));
//                startActivity(intent);
//            }
//        });
    }

//    private ArrayList<Task> getAllTaskList(JSONArray jsonArray) {
//        ArrayList<Task> tasks = new ArrayList<Task>();
//        try {
//            for (int i = 0; i < jsonArray.length(); i++) {
//                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                tasks.add(parseJsonToTask(jsonObject));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return tasks;
//    }

//    private ArrayList<Task> getNearbyTaskList(JSONArray jsonArray) {
//        double currentLatitude = userLocalStore.getCurrentLatitude();
//        double currentLongitude = userLocalStore.getCurrentLongitude();
//        float[] distance = new float[1];
//        ArrayList<Task> tasks = new ArrayList<Task>();
//        try {
//            for (int i = 0; i < jsonArray.length(); i++) {
//                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                Task task = parseJsonToTask(jsonObject);
//                Location.distanceBetween(currentLatitude, currentLongitude, task.geoLat, task.geoLong, distance);
//                if (distance[0] <= METERS_NEARBY) {
//                    tasks.add(task);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return tasks;
//    }

//    private void displayNearbyTaskList(JSONArray fetchedJsonArray) {
//
//        final ArrayList<Task> tasks = getNearbyTaskList(fetchedJsonArray);
//
//        adapter = new TaskListAdapter(this, tasks);
//        taskListView.setAdapter(adapter);
//
//        taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(TaskListActivity.this, TaskDetailActivity.class);
//                intent.putExtra("task", tasks.get(position));
//                startActivity(intent);
//            }
//        });
//    }


//    private Task parseJsonToTask(JSONObject jsonObject) {
//        try {
//            int taskId = jsonObject.getInt("taskId");
//            String title = jsonObject.getString("title");
//            String content = jsonObject.getString("content");
//            String createTime = jsonObject.getString("createTime");
//            int creatorId = jsonObject.getInt("creatorId");
//            String category = jsonObject.getString("category");
//            String deadline = jsonObject.getString("deadline");
//            int duration = jsonObject.getInt("duration");
//            int award = jsonObject.getInt("award");
//            int participants = jsonObject.getInt("participants");
//            String status = jsonObject.getString("status");
//            double geoLat = jsonObject.getDouble("geoLat");
//            double geoLong = jsonObject.getDouble("geoLong");
//            String address = jsonObject.getString("address");
//
//            return new Task(taskId, title, content, createTime, creatorId, category,
//                    deadline, duration, award, participants, status, geoLat, geoLong, address);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    private void showErrorMessage() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage("Downloading Task Failed");
        dialogBuilder.setPositiveButton("OK", null);
        dialogBuilder.show();
    }


}

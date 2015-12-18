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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TaskListActivity extends AppCompatActivity {
    public static final float METERS_NEARBY = 20000;

    ListView taskListView;

    UserLocalStore userLocalStore;

//    TaskListAdapter adapter;

    Spinner sortTaskSpinner;

    ArrayList<Task> taskList;

    String taskType = "ALL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        taskListView = (ListView) findViewById(R.id.taskListView);
        sortTaskSpinner = (Spinner) findViewById(R.id.sortTaskSpinner);

        userLocalStore = new UserLocalStore(this);

        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        if(bd != null)
        {
            taskType = (String) bd.get("TaskType");
            if (taskType.equals("ALL")) taskList = DataHolder.getInstance().getTasks();
            else if (taskType.equals("ACCEPTED")) taskList = DataHolder.getInstance().getUserAcceptedTasks();
            else if (taskType.equals("CREATED")) taskList = DataHolder.getInstance().getUserCreatedTasks();
        } else {
            taskList = DataHolder.getInstance().getTasks();
        }

        TaskListAdapter adapter = new TaskListAdapter(this, taskList);
        taskListView.setAdapter(adapter);
        taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TaskListActivity.this, TaskDetailActivity.class);
                intent.putExtra("task", taskList.get(position));
                String buttonType = taskType.equals("ALL") ? "ON" : "OFF";
                intent.putExtra("ButtonType", buttonType);
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
        Collections.sort(taskList, new Comparator<Task>() {
            @Override
            public int compare(Task task1, Task task2) {
                return task2.createTime.compareTo(task1.createTime);
            }
        });

        TaskListAdapter adapter = (TaskListAdapter) taskListView.getAdapter();
        adapter.changeTaskList(taskList);
        adapter.notifyDataSetChanged();
    }

    private void showTasksByLocation() {
        Collections.sort(taskList, new Comparator<Task>() {
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
        adapter.changeTaskList(taskList);
        adapter.notifyDataSetChanged();

    }

    private void showTasksByAward() {
//        ArrayList<Task> tempTasks = DataHolder.getInstance().getTasks();

        Collections.sort(taskList, new Comparator<Task>() {
            @Override
            public int compare(Task task1, Task task2) {
                return task2.award - task1.award;
            }
        });

        TaskListAdapter adapter = (TaskListAdapter) taskListView.getAdapter();
        adapter.changeTaskList(taskList);
        adapter.notifyDataSetChanged();

    }

    private void showTasksByDuration() {
//        ArrayList<Task> tempTasks = DataHolder.getInstance().getTasks();

        Collections.sort(taskList, new Comparator<Task>() {
            @Override
            public int compare(Task task1, Task task2) {
                return task1.duration - task2.duration;
            }
        });
        TaskListAdapter adapter = (TaskListAdapter) taskListView.getAdapter();
        adapter.changeTaskList(taskList);
        adapter.notifyDataSetChanged();

    }


    private void showErrorMessage() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage("Downloading Task Failed");
        dialogBuilder.setPositiveButton("OK", null);
        dialogBuilder.show();
    }


}

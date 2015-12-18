package com.example.weiranfang.crowdtaskmanager;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;

import java.util.ArrayList;

public class TaskDetailActivity extends AppCompatActivity {

    private TextView titleTextView;
    private TextView categoryTextView;
    private TextView durationTextView;
    private TextView awardTextView;
    private TextView createTimeTextView;
    private TextView deadlineTextView;
    private TextView addressTextView;
    private TextView contentTextView;
    private Button acceptButton;

    private UserLocalStore userLocalStore;

    Task task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        userLocalStore = new UserLocalStore(this);

        titleTextView = (TextView) findViewById(R.id.titleTextView);
        categoryTextView = (TextView) findViewById(R.id.categoryTextView);
        durationTextView = (TextView) findViewById(R.id.durationTextView);
        awardTextView = (TextView) findViewById(R.id.awardTextView);
        createTimeTextView = (TextView) findViewById(R.id.createTimeTextView);
        deadlineTextView = (TextView) findViewById(R.id.deadlineTextView);
        addressTextView = (TextView) findViewById(R.id.addressTextView);
        contentTextView = (TextView) findViewById(R.id.contentTextView);
        acceptButton = (Button) findViewById(R.id.acceptButton);

        Intent intent = getIntent();
        task = (Task) intent.getSerializableExtra("task");

        Bundle bd = intent.getExtras();
        String buttonType = null;
        if(bd != null) {
            buttonType = (String) bd.getString("ButtonType");
        }
        if (buttonType != null && buttonType.equals("OFF")) {
            acceptButton.setVisibility(View.GONE);
        }

        titleTextView.setText(task.title);
        categoryTextView.setText(task.category);
        durationTextView.setText(Task.durationArray[task.duration]);
        awardTextView.setText(task.award + "");
        createTimeTextView.setText(task.createTime);
        deadlineTextView.setText(task.deadline);
        if (task.address.trim().length() == 0) {
            addressTextView.setText("None");
        } else {
            addressTextView.setText(task.address);
        }
        contentTextView.setText(task.content);
        
    }

    public void clickAcceptButton(View view) {
        User currentUser = userLocalStore.getLoggedInUser();
//        LatLng currentLatLng = new LatLng(userLocalStore.getCurrentLatitude(), userLocalStore.getCurrentLongitude());
        // Download task data
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.updateParticipationInBackground(currentUser, task, new GetJsonCallBack() {
            @Override
            public void done(JSONArray jsonArray) {
                startActivity(new Intent(TaskDetailActivity.this, MainActivity.class));
            }
        });
    }

}

package com.example.weiranfang.crowdtaskmanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class TaskDetailActivity extends AppCompatActivity {

    private TextView titleTextView;
    private TextView categoryTextView;
    private TextView durationTextView;
    private TextView awardTextView;
    private TextView createTimeTextView;
    private TextView deadlineTextView;
    private TextView addressTextView;
    private TextView contentTextView;

    Task task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        titleTextView = (TextView) findViewById(R.id.titleTextView);
        categoryTextView = (TextView) findViewById(R.id.categoryTextView);
        durationTextView = (TextView) findViewById(R.id.durationTextView);
        awardTextView = (TextView) findViewById(R.id.awardTextView);
        createTimeTextView = (TextView) findViewById(R.id.createTimeTextView);
        deadlineTextView = (TextView) findViewById(R.id.deadlineTextView);
        addressTextView = (TextView) findViewById(R.id.addressTextView);
        contentTextView = (TextView) findViewById(R.id.contentTextView);

        Intent intent = getIntent();
        task = (Task) intent.getSerializableExtra("task");

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
        return;
    }
}

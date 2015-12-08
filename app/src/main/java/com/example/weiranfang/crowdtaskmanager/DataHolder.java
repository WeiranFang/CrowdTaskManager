package com.example.weiranfang.crowdtaskmanager;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by weiranfang on 11/29/15.
 */
public class DataHolder {
    private ArrayList<Task> tasks;

    private static final DataHolder holder = new DataHolder();

    private DataHolder(){}

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    public static DataHolder getInstance() {
        return holder;
    }
}

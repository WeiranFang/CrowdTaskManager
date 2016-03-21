package com.example.weiranfang.crowdtaskmanager;

import java.util.ArrayList;

/**
 * Created by weiranfang on 11/29/15.
 * A Singleton class used for saving local task data.
 */
public class DataHolder {
    private ArrayList<Task> allTasks; // All nearby tasks.
    private ArrayList<Task> userCreatedTasks;
    private ArrayList<Task> userAcceptedTasks;

    private static final DataHolder holder = new DataHolder();

    private DataHolder(){}

    public ArrayList<Task> getTasks() {
        return allTasks;
    }

    public ArrayList<Task> getUserCreatedTasks() {
        return userCreatedTasks;
    }

    public ArrayList<Task> getUserAcceptedTasks() {
        return userAcceptedTasks;
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.allTasks = tasks;
    }

    public void setUserCreatedTasks(ArrayList<Task> userCreatedTasks) {
        this.userCreatedTasks = userCreatedTasks;
    }

    public void setUserAcceptedTasks(ArrayList<Task> userAcceptedTasks) {
        this.userAcceptedTasks = userAcceptedTasks;
    }

    public static DataHolder getInstance() {
        return holder;
    }
}

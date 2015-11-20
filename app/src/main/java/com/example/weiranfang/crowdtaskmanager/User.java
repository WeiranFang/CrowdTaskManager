package com.example.weiranfang.crowdtaskmanager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by weiranfang on 10/25/15.
 */
public class User {
    String username, password, email, createTime;
    String location = "";
    int userId, age;

    /**
     * Constructor used for fetching user from database
     */
    public User(int userId, String username, String password, String email, int age, String createTime) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.age = age;
        this.createTime = createTime;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(String username, String password, String email, int age) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.age = age;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        this.createTime = dateFormat.format(new Date());
    }
}

package com.example.weiranfang.crowdtaskmanager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by weiranfang on 10/25/15.
 */
public class User {
    String username, password, email, location, createTime;
    int userId, age;

    public User(int userId, String username, String password, String email, int age) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.age = age;
        this.location = "";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        this.createTime = dateFormat.format(new Date());
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
        this.location = "";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        this.createTime = dateFormat.format(new Date());
    }
}

package com.example.weiranfang.crowdtaskmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

/**
 * Created by weiranfang on 10/25/15.
 */
public class UserLocalStore {
    public static final String SP_NAME = "userDetails";
    SharedPreferences userLocalDatabase;

    public UserLocalStore(Context context) {
        userLocalDatabase = context.getSharedPreferences(SP_NAME, 0);
    }

    public void storeUserData(User user) {
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putString("username", user.username);
        spEditor.putString("password", user.password);
        spEditor.putInt("age", user.age);
        spEditor.putString("email", user.email);
        spEditor.putInt("userId", user.userId);
        spEditor.putString("createTime", user.createTime);
        spEditor.commit();
    }

    public User getLoggedInUser() {
        String username = userLocalDatabase.getString("username", "");
        int age = userLocalDatabase.getInt("age", -1);
        int userId = userLocalDatabase.getInt("userId", -1);
        String password = userLocalDatabase.getString("password", "");
        String email = userLocalDatabase.getString("email", "");
        String createTime = userLocalDatabase.getString("createTime", "");
        User storedUser = new User(userId, username, password, email, age, createTime);
        return storedUser;
    }

    public void setUserLoggedIn(boolean loggedIn) {
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putBoolean("loggedIn", loggedIn);
        spEditor.commit();
    }

    public void setCurrentLocation(Location location) {
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putString("latitude", location.getLatitude() + "");
        spEditor.putString("longitude", location.getLongitude() + "");
        spEditor.commit();
    }

    public double getCurrentLatitude(){
        return Double.parseDouble(userLocalDatabase.getString("latitude", "40.52083759"));
    }

    public double getCurrentLongitude() {
        return Double.parseDouble(userLocalDatabase.getString("longitude", "-74.4576809"));
    }

    public void clearUserData() {
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.clear();
        spEditor.commit();
    }

    public boolean getUserLoggedIn() {
        return userLocalDatabase.getBoolean("loggedIn", false);
    }

}

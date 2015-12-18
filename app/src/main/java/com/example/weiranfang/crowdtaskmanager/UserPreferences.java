package com.example.weiranfang.crowdtaskmanager;

import java.io.Serializable;

/**
 * Created by weiranfang on 12/7/15.
 */
public class UserPreferences implements Serializable{
    int userId, minAward, maxDuration, maxDistance;

    public UserPreferences(int userId, int minAward, int maxDuration, int maxDistance) {
        this.userId = userId;
        this.minAward = minAward;
        this.maxDuration = maxDuration;
        this.maxDistance = maxDistance;
    }
}

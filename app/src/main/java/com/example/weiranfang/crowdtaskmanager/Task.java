package com.example.weiranfang.crowdtaskmanager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by weiranfang on 11/6/15.
 */
public class Task {
    int taskId, award, participants, duration, creatorId;
    String title, content, createTime, category, deadline, status;
    double geoLat, geoLong;

    /**
     * Constructor for uploading task
     */
    public Task(int award, int participants, int creatorId, int duration, String title,
                String content, String category, String deadline, double geoLat, double geoLong) {
        this.award = award;
        this.participants = participants;
        this.duration = duration;
        this.title = title;
        this.content = content;
        this.creatorId = creatorId;
        this.category = category;
        this.deadline = deadline;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        this.createTime = dateFormat.format(new Date());
        this.status = "pending";
        this.geoLat = geoLat;
        this.geoLong = geoLong;
    }

    /**
     * Constructor for downloading task
     */
    public Task(int taskId, String title, String content, String createTime, int creatorId,
                String category, String deadline, int duration, int award, int participants,
                String status, double geoLat, double geoLong) {
        this.taskId = taskId;
        this.award = award;
        this.participants = participants;
        this.duration = duration;
        this.title = title;
        this.content = content;
        this.creatorId = creatorId;
        this.category = category;
        this.deadline = deadline;
        this.createTime = createTime;
        this.status = status;
        this.geoLat = geoLat;
        this.geoLong = geoLong;
    }

}

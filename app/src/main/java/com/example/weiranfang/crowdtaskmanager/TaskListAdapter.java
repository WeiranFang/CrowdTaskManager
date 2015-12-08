package com.example.weiranfang.crowdtaskmanager;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by weiranfang on 11/16/15.
 */
public class TaskListAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<Task> taskList;
    private static LayoutInflater inflater = null;

//    public TaskListAdapter(Activity activity, JSONArray jsonArray) {
//        this.activity = activity;
//        this.jsonArray = jsonArray;
//        inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//    }

    public TaskListAdapter(Activity activity, ArrayList<Task> taskList) {
        this.activity = activity;
        this.taskList = taskList;
        inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void changeTaskList(ArrayList<Task> taskList) {
        this.taskList = taskList;
    }

    public static class ViewHolder{

        public TextView titleTextView;
        public TextView createTimeTextView;
        public TextView awardTextView;

    }

    @Override
    public int getCount() {
        return taskList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        ViewHolder holder;
        if (convertView == null) {
            vi = inflater.inflate(R.layout.tast_list_item, null);

            holder = new ViewHolder();
            holder.titleTextView = (TextView) vi.findViewById(R.id.taskTitleTextView);
            holder.createTimeTextView = (TextView) vi.findViewById(R.id.createTimeTextView);
            holder.awardTextView = (TextView) vi.findViewById(R.id.awardTextView);

            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }

        if (taskList.size() <= 0) {
            holder.titleTextView.setText("No Data");
        } else {
            Task task = taskList.get(position);
            holder.titleTextView.setText(task.title);
            holder.awardTextView.setText(task.award + "");
            holder.createTimeTextView.setText(task.createTime);

//            JSONObject jsonObject = null;
//            try {
//                jsonObject = jsonArray.getJSONObject(position);
//                holder.titleTextView.setText(jsonObject.getString("title"));
//                holder.awardTextView.setText(jsonObject.getString("award"));
//                holder.createTimeTextView.setText(jsonObject.getString("createTime"));
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
        }
        return vi;
    }

}

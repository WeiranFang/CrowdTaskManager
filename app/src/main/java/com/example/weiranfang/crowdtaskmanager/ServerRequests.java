package com.example.weiranfang.crowdtaskmanager;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by weiranfang on 10/26/15.
 */
public class ServerRequests {
    ProgressDialog progressDialog;
    public static final int CONNECTION_TIMEOUT = 1000 * 15, READ_TIMEOUT = 1000 * 15;
    public static final String SERVER_ADDRESS = "http://192.168.1.221/crowd/db/";

    public ServerRequests(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing");
        progressDialog.setMessage("Please wait.....");
    }

    public void storeUserDataInBackground(User user, GetUserCallBack callBack) {
        progressDialog.show();
        new StoreUserDataAsyncTask(user, callBack).execute();
    }

    public void fetchUserDataInBackground(User user, GetUserCallBack callBack) {
        progressDialog.show();
        new FetchUserDataInBackground(user, callBack).execute();
    }

    public void storeTaskInBackground(Task task, GetTaskCallBack callBack) {
        progressDialog.show();
        new StoreTaskDataAsyncTask(task, callBack).execute();
    }

    public void fetchTaskDataInBackground(User user, GetJsonCallBack callBack) {
        progressDialog.show();
        new FetchTaskDataAsyncTask(user, callBack).execute();
    }

    public class StoreUserDataAsyncTask extends AsyncTask<Void, Void, Void> {
        User user;
        GetUserCallBack userCallBack;

        public StoreUserDataAsyncTask(User user, GetUserCallBack userCallBack) {
            this.user = user;
            this.userCallBack = userCallBack;
        }

        @Override
        protected Void doInBackground(Void... params) {
            HashMap<String, String> dataToSend = wrapData(user);

            URL url = null;
            HttpURLConnection connection = null;
            try {
                url = new URL(SERVER_ADDRESS + "register.php");
                connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);

                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(dataToSend));
                writer.flush();
                writer.close();
                os.close();

                connection.getResponseCode();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(connection != null) {
                    connection.disconnect();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            userCallBack.done(null);
            super.onPostExecute(aVoid);
        }
    }

    public class FetchUserDataInBackground extends AsyncTask<Void, Void, User> {
        User user;
        GetUserCallBack userCallBack;

        public FetchUserDataInBackground(User user, GetUserCallBack userCallBack) {
            this.user = user;
            this.userCallBack = userCallBack;
        }

        @Override
        protected User doInBackground(Void... params) {
            HashMap<String, String> dataToSend = new HashMap<>();
            dataToSend.put("username", user.username);
            dataToSend.put("password", user.password);

            URL url = null;
            HttpURLConnection connection = null;
            String response = "";
            User returnedUser = null;
            try {
                url = new URL(SERVER_ADDRESS + "fetch_user_data.php");
                connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);

                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(dataToSend));

                writer.flush();
                writer.close();
                os.close();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()));
                    while((line = br.readLine()) != null) {
                        response += line + "\n";
                    }
                } else {
                    response = "";
                }

                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.length() == 0) {
                    returnedUser = null;
                } else {
                    //String username = jsonObject.getString("username");
                    String email = jsonObject.getString("email");
                    int age = jsonObject.getInt("age");
                    int userId = jsonObject.getInt("userId");
                    returnedUser = new User(userId, user.username, user.password, email, age);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(connection != null) {
                    connection.disconnect();
                }
            }
            return returnedUser;
        }

        @Override
        protected void onPostExecute(User returnedUser) {
            progressDialog.dismiss();
            userCallBack.done(returnedUser);
            super.onPostExecute(returnedUser);
        }
    }

    public class StoreTaskDataAsyncTask extends AsyncTask<Void, Void, Void> {
        Task task;
        GetTaskCallBack taskCallBack;
        public StoreTaskDataAsyncTask(Task task, GetTaskCallBack taskCallBack) {
            this.task = task;
            this.taskCallBack = taskCallBack;
        }

        @Override
        protected Void doInBackground(Void... params) {
            HashMap<String, String> dataToSend = wrapData(task);
            URL url = null;
            HttpURLConnection connection = null;
            try {
                url = new URL(SERVER_ADDRESS + "upload_task.php");
                connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);

                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(dataToSend));
                writer.flush();
                writer.close();
                os.close();

                connection.getResponseCode();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(connection != null) {
                    connection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            taskCallBack.done(null);
            super.onPostExecute(aVoid);
        }
    }

    private class FetchTaskDataAsyncTask extends AsyncTask<Void, Void, JSONArray>{
        User currentUser;
        GetJsonCallBack jsonCallBack;

        public FetchTaskDataAsyncTask(User user, GetJsonCallBack jsonCallBack) {
            this.currentUser = user;
            this.jsonCallBack = jsonCallBack;
        }

        @Override
        protected JSONArray doInBackground(Void... params) {
            HashMap<String, String> dataToSend = new HashMap<>();
            dataToSend.put("userId", currentUser.userId + "");
            //TODO: send current user location to output

            URL url = null;
            HttpURLConnection connection = null;
            String response = "";
            JSONArray fetchedJsonArray = null;

            try {
                url = new URL(SERVER_ADDRESS + "fetch_task_data.php");
                connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);

                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(dataToSend));

                writer.flush();
                writer.close();
                os.close();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()));
                    while((line = br.readLine()) != null) {
                        response += line + "\n";
                    }
                } else {
                    response = "";
                }

                fetchedJsonArray = new JSONArray(response);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }

            return fetchedJsonArray;
        }

        @Override
        protected void onPostExecute(JSONArray fetchedJsonArray) {
            progressDialog.dismiss();
            jsonCallBack.done(fetchedJsonArray);
            super.onPostExecute(fetchedJsonArray);
        }
    }

    private HashMap<String, String> wrapData(Object object) {
        HashMap<String, String> dataToSend = new HashMap<>();

        if (object instanceof Task) {
            Task task = (Task) object;
            dataToSend.put("title", task.title);
            dataToSend.put("content", task.content);
            dataToSend.put("createTime", task.createTime);
            dataToSend.put("creatorId", task.creatorId + "");
            dataToSend.put("category", task.category);
            dataToSend.put("deadline", task.deadline);
//            dataToSend.put("location", task.location);
            dataToSend.put("duration", task.duration + "");
            dataToSend.put("award", task.award + "");
            dataToSend.put("participants", task.participants + "");
            dataToSend.put("status", task.status);
            dataToSend.put("geoLat", task.geoLat + "");
            dataToSend.put("geoLong", task.geoLong + "");
        } else if (object instanceof User) {
            User user = (User) object;
            dataToSend.put("username", user.username);
            dataToSend.put("email", user.email);
            dataToSend.put("age", user.age + "");
            dataToSend.put("password", user.password);
            dataToSend.put("location", user.location);
            dataToSend.put("createTime", user.createTime);
        }
        return dataToSend;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }


}

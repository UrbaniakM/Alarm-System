package com.example.kajet_000.monitoring1;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;

import static android.view.View.GONE;
import static java.lang.Thread.sleep;


public class MonitoringActivity extends AppCompatActivity {

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;

    //defining views
    Button buttonWylacz, buttonStart, buttonZalacz;
    ProgressBar progressBar;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monitoring);

        buttonWylacz = (Button) findViewById(R.id.wylacz);
        buttonStart = (Button) findViewById(R.id.startCon);
        buttonZalacz = (Button) findViewById(R.id.zalacz);

        progressBar = (ProgressBar) findViewById(R.id.progressBar2);

        textView = (TextView) findViewById(R.id.textView);

        buttonWylacz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateHero1(); //zmienia status na 1 wylaczony
                System.out.print("Wylaczono alarm!!\n");
                // task.cancel(true);
            }
        });
        buttonZalacz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateHero2(); //zmienia na status 2 = zalaczony
                System.out.print("Zalaczono alarm!!\n");
                // task.cancel(true);
            }
        });
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.print("Zaczeto nasluchiwac\n");
                /*YourTask task = new YourTask();
                task.execute();*/
                readStatus();
            }
        });
        //readStatus();

    }
    public void sendNotification() {

        textView.setVisibility(View.VISIBLE);

        if (Build.VERSION.SDK_INT < 26) {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.a)
                            .setContentTitle("ALARM UAKTYWNIONY!!!")
                            .setContentText("Aby wyłączyć alarm przejdź do aplikacji");
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(001, mBuilder.build());
            return;
        }
            NotificationManager notificationManager =
                    (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel("default",
                    "Channel name",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Channel description");
            notificationManager.createNotificationChannel(channel);

        Notification notificationBuilder = new NotificationCompat.Builder(this, "default")
        .setContentTitle("ALARM UAKTYWNIONY!!!")
                .setContentText("Aby wyłączyć alarm przejdź do aplikacji")
                .setBadgeIconType(R.mipmap.ic_launcher)
                .setNumber(5)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setAutoCancel(true)
                .build();
        notificationManager.notify(1, notificationBuilder);
        /*

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // The id of the channel.
        String id = "my_channel_01";
        // The user-visible name of the channel.
        CharSequence name = getString(R.string.channel_name);
        // The user-visible description of the channel.
        String description = getString(R.string.channel_desc);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = new NotificationChannel(id, name, importance);
        mNotificationManager.createNotificationChannel(mChannel);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "my_channel_01");
        notificationBuilder.setAutoCancel(true)
               // .setDefaults(Notification.DEFAULT_ALL)
                //.setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.a)
               // .setTicker("Hearty365")
                .setPriority(importance) // this is deprecated in API 26 but you can still use for below 26. check below update for 26 API
                .setContentTitle("ALARM UAKTYWNIONY!!!")
                .setContentText("Aby wyłączyć alarm przejdź do aplikacji");
              //  .setContentInfo("Info");

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notificationBuilder.build());*/
    }
    private class YourTask extends AsyncTask<Void,Void, Void>{

        protected Void doInBackground(Void... params){
           // for(int i = 0; i < 10; i++) {
                readStatus();
               /* try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }*/
            try {
                sleep(100000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    private void readStatus(){
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_READ_STATUS, null, CODE_GET_REQUEST);
        request.execute();
    }

    private void updateHero1() {

        textView.setVisibility(View.INVISIBLE);

        HashMap<String, String> params = new HashMap<>();
        params.put("status","1");
        params.put("urzadzenie","alarm");

        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_UPDATE_HERO, params, CODE_POST_REQUEST);
        request.execute();
    }
    private void updateHero2() {

        HashMap<String, String> params = new HashMap<>();
        params.put("status","2");
        params.put("urzadzenie","alarm");

        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_UPDATE_HERO, params, CODE_POST_REQUEST);
        request.execute();
    }

    //inner class to perform network request extending an AsyncTask
    private class PerformNetworkRequest extends AsyncTask<Void, Void, String> {

        //the url where we need to send the request
        String url;

        //the parameters
        HashMap<String, String> params;

        //the request code to define whether it is a GET or POST
        int requestCode;

        //constructor to initialize values
        PerformNetworkRequest(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        //when the task started displaying a progressbar
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        //this method will give the response from the request
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(GONE);
            if(requestCode == 1024){

                try {
                    JSONObject object = new JSONObject(s);
                    if (!object.getBoolean("error")) {

                            //object.getString("message");
                        //Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
                        //refreshing the herolist after every operation
                        //so we get an updated list
                        //we will create this method right now it is commented
                        //because we haven't created it yet
                        System.out.print(requestCode+"\n");
                        JSONArray wynik = object.getJSONArray("wynik");
                        JSONObject obj = wynik.getJSONObject(0);
                        if(obj.getInt("status") == 3) //jeśli zlodziej
                        {
                            System.out.print("ZLODZIEJ!!!\n");
                            sendNotification();
                        }
                        else
                            System.out.print("Nie ma zlodzieja\n");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        //the network operation will be performed in background
        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();

            if (requestCode == CODE_POST_REQUEST)
                return requestHandler.sendPostRequest(url, params);


            if (requestCode == CODE_GET_REQUEST)
                return requestHandler.sendGetRequest(url);

            return null;
        }
    }
}




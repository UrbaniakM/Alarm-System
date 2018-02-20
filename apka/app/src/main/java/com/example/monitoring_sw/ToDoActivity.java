package com.example.monitoring_sw;


import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceException;
import com.microsoft.windowsazure.mobileservices.http.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.query.Query;
import com.microsoft.windowsazure.mobileservices.table.query.QueryOperations;
import com.microsoft.windowsazure.mobileservices.table.query.QueryOrder;
import com.microsoft.windowsazure.mobileservices.table.sync.MobileServiceSyncContext;
import com.microsoft.windowsazure.mobileservices.table.sync.MobileServiceSyncTable;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.ColumnDataType;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.MobileServiceLocalStoreException;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.SQLiteLocalStore;
import com.microsoft.windowsazure.mobileservices.table.sync.synchandler.SimpleSyncHandler;
import com.squareup.okhttp.OkHttpClient;

import static com.microsoft.windowsazure.mobileservices.table.query.QueryOperations.*;

import android.os.Bundle;
import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.content.Context;


public class ToDoActivity extends Activity implements View.OnClickListener {

    private MobileServiceClient mClient;

    private MobileServiceTable<ToDoItem> mToDoTable;
    private MobileServiceTable<Monitoring> mMonitoringTable;
    private MobileServiceTable<Uzytkownicy> mUzytkownicyTable;

    public List<Monitoring> listaMonitoring;

    private ToDoItemAdapter mAdapter;

    Integer stan = 0;
    Boolean readyToOff = false, wantToOff = false;
    String rola;

    private EditText mTextNewToDo, mLogin, mHaslo;
    private TextView mTekst;
    private Button mZaloguj, mWylacz;

    private ProgressBar mProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do);

        // mProgressBar = (ProgressBar) findViewById(R.id.loadingProgressBar);

        //mProgressBar.setVisibility(ProgressBar.GONE);

        try {
            mClient = new MobileServiceClient(
                    "https://monitoring-sw.azurewebsites.net",
                    this).withFilter(new ProgressFilter());

            mClient.setAndroidHttpClientFactory(new OkHttpClientFactory() {
                @Override
                public OkHttpClient createOkHttpClient() {
                    OkHttpClient client = new OkHttpClient();
                    client.setReadTimeout(20, TimeUnit.SECONDS);
                    client.setWriteTimeout(20, TimeUnit.SECONDS);
                    return client;
                }
            });
            //mToDoTable = mClient.getTable(ToDoItem.class);
            mMonitoringTable = mClient.getTable(Monitoring.class);
            mUzytkownicyTable = mClient.getTable(Uzytkownicy.class);

            logowanie();

            //initLocalStore().get();

            //mTextNewToDo = (EditText) findViewById(R.id.textNewToDo);

            //mAdapter = new ToDoItemAdapter(this, R.layout.row_list_to_do);
            //ListView listViewToDo = (ListView) findViewById(R.id.listViewToDo);
            //listViewToDo.setAdapter(mAdapter);

            //refreshItemsFromTable();

        } catch (MalformedURLException e) {
            createAndShowDialog(new Exception("There was an error creating the Mobile Service. Verify the URL"), "Error");
        } catch (Exception e) {
            createAndShowDialog(e, "Error");
        }
    }

   public void sendNotification(View view) { //https://www.androidauthority.com/how-to-create-android-notifications-707254/

        //Get an instance of NotificationManager//

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Ktoś jest w domu!!! ")
                        .setContentText("Przejdź do aplikacji w celu wyłączenia!");

        // Gets an instance of the NotificationManager service//

        NotificationManager mNotificationManager =

                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // When you issue multiple notifications about the same type of event,
        // it’s best practice for your app to try to update an existing notification
        // with this new information, rather than immediately creating a new notification.
        // If you want to update this notification at a later date, you need to assign it an ID.
        // You can then use this ID whenever you issue a subsequent notification.
        // If the previous notification is still visible, the system will update this existing notification,
        // rather than create a new one. In this example, the notification’s ID is 001//



                mNotificationManager.notify(001, mBuilder.build());
    }
    @Override
    public void onClick(View v) {
        if (v == mZaloguj) { //logowanie
            loginUczestnik();
        }
        else if (v == mWylacz){
            zmienStatusAlarmu();
        }
    }
    public void logowanie(){
        setContentView(R.layout.logowanie);

        mLogin = (EditText) findViewById(R.id.login);
        mHaslo = (EditText) findViewById(R.id.haslo);
        mZaloguj = (Button) findViewById(R.id.zaloguj);

        mZaloguj.setOnClickListener((View.OnClickListener) this);
    }
    public void loginUczestnik(){
        final boolean temp[] = {false};
        final String log = mLogin.getText().toString();
        final String has = mHaslo.getText().toString();
        final Context con=this;
        System.out.print("loginUczestnik Start\n");
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final List<Uzytkownicy> results = mUzytkownicyTable.where().field("nazwa").
                            eq(val(log)).execute().get();;
                    if (!results.isEmpty()) {
                        for (Uzytkownicy item : results) {
                            if (item.getHaslo().toString().equals(has) ) {
                                System.out.print("Zalogowano\n");
                                temp[0] = true;
                                rola = item.getTyp();
                                break;
                            }
                        }
                    }
                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if(temp[0])
                    check1();
                else
                    Toast.makeText(con, "Błędny login, lub hasło!", Toast.LENGTH_SHORT).show();
            }
        };
        runAsyncTask(task);
    }
    public void check1(){
        setContentView(R.layout.glowna);
        mWylacz = (Button) findViewById(R.id.wylacz);
        mWylacz.setOnClickListener((View.OnClickListener) this);
        mTekst = (TextView) findViewById(R.id.tekst);

        checkAlarmFirst();

       /*AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                return null;
            }
        };
        runAsyncTask(task);*/
    }
public void checkAlarm(){
        System.out.print("CheckAlarm START!\n");
        View view = null;
    final View finalView = view;
    AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                Integer a = 0;
                while(a<100) {
                    if(wantToOff) {
                        readyToOff = true;
                        return null;
                    }
                    System.out.print("Kolejne sprawdzanie\n");
                    listaMonitoring = mMonitoringTable.execute().get();
                    for (Monitoring item : listaMonitoring) {
                        if (item.getUrzadzenie().equals("alarm")) {
                            stan = item.getStan();
                            if (item.getStan() == 2) {
                                System.out.print("ALARM WLACZONY  2!!!\n");
                                readyToOff = true;
                                return null;
                            }
                            else
                                System.out.print("Alarm wylaczony  0 lub 1\n");
                        }
                        else
                            System.out.print("Nie alarm\n");
                    }
                    a++;
                    TimeUnit.SECONDS.sleep(5);
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (MobileServiceException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(stan == 2){
                mTekst.setText("KTOŚ JEST W DOMU!!!");
                mWylacz.setText("Wyłącz alarm!");
                wantToOff = true;
                sendNotification(finalView);
            }
        }
    };
    runAsyncTask(task);
}
public void zmienStatusAlarmu(){
    System.out.print("Zmien status START!\n");
    wantToOff = true;
    final Context con = this;
    AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                while(!readyToOff)
                { TimeUnit.SECONDS.sleep(1);
                }
                System.out.print("zaczynam zmieniac\n");
                listaMonitoring = refreshMonitoring();
                System.out.print("Zaaktualizowano do zmiany!\n");
                for (Monitoring item : listaMonitoring) {
                    if (item.getUrzadzenie().equals("alarm")) {
                        if (item.getStan() == 0) {
                            stan = 1; //zalaczony
                            item.setStan(1);
                            try {
                                mMonitoringTable.update(item).get();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                        }else if(rola.equals("uzytkownik"))
                        {
                        }
                        else {
                            stan = 0; //wylaczony
                            item.setStan(0);
                            try {
                                mMonitoringTable.update(item).get();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                System.out.print("Zakonczono zmieniac stan\n");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (MobileServiceException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            wantToOff = false;
            readyToOff = false;
            if(stan == 1){
                mTekst.setText("Alarm załączony!");
                mWylacz.setText("Wyłącz alarm");
            }
            else if(rola.equals("uzytkownik"))
            {
                Toast.makeText(con, "Nie masz uprawnień do wyłączenia alarmu!", Toast.LENGTH_LONG).show();
            }
            else{
                mTekst.setText("Alarm wyłączony!");
                mWylacz.setText("Załącz alarm");
            }
            checkAlarm();
        }
    };
    runAsyncTask(task);
}
private List<Monitoring> refreshMonitoring() throws MobileServiceException, ExecutionException, InterruptedException {
    return mMonitoringTable.execute().get();
}
    public void checkAlarmFirst(){
        System.out.print("CheckAlarm First START!\n");
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                        listaMonitoring = mMonitoringTable.execute().get();
                        for (Monitoring item : listaMonitoring) {
                            if (item.getUrzadzenie().equals("alarm"))
                                stan = item.getStan();
                        }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (MobileServiceException e) {
                    e.printStackTrace();
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if(stan == 2){
                    mTekst.setText("KTOŚ JEST W DOMU!!!");
                    mWylacz.setText("Wyłącz alarm!");
                    readyToOff = true;
                }
                else if(stan == 1){
                    mTekst.setText("Alarm załączony");
                    mWylacz.setText("Wyłącz alarm!");
                    checkAlarm();
                }
                else if(stan == 0){
                    mTekst.setText("Alarm wyłączony");
                    mWylacz.setText("Załącz alarm!");
                    checkAlarm();
                }
            }
        };
        runAsyncTask(task);
    }









    /**
     * Initializes the activity menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    /**
     * Select an option from the menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_refresh) {
            refreshItemsFromTable();
        }

        return true;
    }

    /**
     * Mark an item as completed
     *
     * @param item
     *            The item to mark
     */
    public void checkItem(final ToDoItem item) {
        if (mClient == null) {
            return;
        }

        // Set the item as completed and update it in the table
        item.setComplete(true);

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {

                    checkItemInTable(item);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (item.isComplete()) {
                                mAdapter.remove(item);
                            }
                        }
                    });
                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }

                return null;
            }
        };

        runAsyncTask(task);

    }

    /**
     * Mark an item as completed in the Mobile Service Table
     *
     * @param item
     *            The item to mark
     */
    public void checkItemInTable(ToDoItem item) throws ExecutionException, InterruptedException {
        mToDoTable.update(item).get();
    }

    /**
     * Add a new item
     *
     * @param view
     *            The view that originated the call
     */
    public void addItem(View view) {
        if (mClient == null) {
            return;
        }

        // Create a new item
        final ToDoItem item = new ToDoItem();

        item.setText(mTextNewToDo.getText().toString());
        item.setComplete(false);

        // Insert the new item
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final ToDoItem entity = addItemInTable(item);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!entity.isComplete()){
                                mAdapter.add(entity);
                            }
                        }
                    });
                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }
                return null;
            }
        };

        runAsyncTask(task);

        mTextNewToDo.setText("");
    }

    /**
     * Add an item to the Mobile Service Table
     *
     * @param item
     *            The item to Add
     */
    public ToDoItem addItemInTable(ToDoItem item) throws ExecutionException, InterruptedException {
        ToDoItem entity = mToDoTable.insert(item).get();
        return entity;
    }

    /**
     * Refresh the list with the items in the Table
     */
    private void refreshItemsFromTable() {

        // Get the items that weren't marked as completed and add them in the
        // adapter

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {

                try {
                    final List<ToDoItem> results = refreshItemsFromMobileServiceTable();

                    //Offline Sync
                    //final List<ToDoItem> results = refreshItemsFromMobileServiceTableSyncTable();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.clear();

                            for (ToDoItem item : results) {
                                mAdapter.add(item);
                            }
                        }
                    });
                } catch (final Exception e){
                    createAndShowDialogFromTask(e, "Error");
                }

                return null;
            }
        };

        runAsyncTask(task);
    }

    /**
     * Refresh the list with the items in the Mobile Service Table
     */

    private List<ToDoItem> refreshItemsFromMobileServiceTable() throws ExecutionException, InterruptedException {
        return mToDoTable.where().field("complete").
                eq(val(false)).execute().get();
    }

    //Offline Sync
    /**
     * Refresh the list with the items in the Mobile Service Sync Table
     */
    /*private List<ToDoItem> refreshItemsFromMobileServiceTableSyncTable() throws ExecutionException, InterruptedException {
        //sync the data
        sync().get();
        Query query = QueryOperations.field("complete").
                eq(val(false));
        return mToDoTable.read(query).get();
    }*/

    /**
     * Initialize local storage
     * @return
     * @throws MobileServiceLocalStoreException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private AsyncTask<Void, Void, Void> initLocalStore() throws MobileServiceLocalStoreException, ExecutionException, InterruptedException {

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {

                    MobileServiceSyncContext syncContext = mClient.getSyncContext();

                    if (syncContext.isInitialized())
                        return null;

                    SQLiteLocalStore localStore = new SQLiteLocalStore(mClient.getContext(), "OfflineStore", null, 1);

                    Map<String, ColumnDataType> tableDefinition = new HashMap<String, ColumnDataType>();
                    tableDefinition.put("id", ColumnDataType.String);
                    tableDefinition.put("text", ColumnDataType.String);
                    tableDefinition.put("complete", ColumnDataType.Boolean);

                    localStore.defineTable("ToDoItem", tableDefinition);

                    SimpleSyncHandler handler = new SimpleSyncHandler();

                    syncContext.initialize(localStore, handler).get();

                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }

                return null;
            }
        };

        return runAsyncTask(task);
    }

    //Offline Sync
    /**
     * Sync the current context and the Mobile Service Sync Table
     * @return
     */
    /*
    private AsyncTask<Void, Void, Void> sync() {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    MobileServiceSyncContext syncContext = mClient.getSyncContext();
                    syncContext.push().get();
                    mToDoTable.pull(null).get();
                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }
                return null;
            }
        };
        return runAsyncTask(task);
    }
    */

    /**
     * Creates a dialog and shows it
     *
     * @param exception
     *            The exception to show in the dialog
     * @param title
     *            The dialog title
     */
    private void createAndShowDialogFromTask(final Exception exception, String title) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createAndShowDialog(exception, "Error");
            }
        });
    }


    /**
     * Creates a dialog and shows it
     *
     * @param exception
     *            The exception to show in the dialog
     * @param title
     *            The dialog title
     */
    private void createAndShowDialog(Exception exception, String title) {
        Throwable ex = exception;
        if(exception.getCause() != null){
            ex = exception.getCause();
        }
        createAndShowDialog(ex.getMessage(), title);
    }

    /**
     * Creates a dialog and shows it
     *
     * @param message
     *            The dialog message
     * @param title
     *            The dialog title
     */
    private void createAndShowDialog(final String message, final String title) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }

    /**
     * Run an ASync task on the corresponding executor
     * @param task
     * @return
     */
    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }

    private class ProgressFilter implements ServiceFilter {

        @Override
        public ListenableFuture<ServiceFilterResponse> handleRequest(ServiceFilterRequest request, NextServiceFilterCallback nextServiceFilterCallback) {

            final SettableFuture<ServiceFilterResponse> resultFuture = SettableFuture.create();


            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (mProgressBar != null) mProgressBar.setVisibility(ProgressBar.VISIBLE);
                }
            });

            ListenableFuture<ServiceFilterResponse> future = nextServiceFilterCallback.onNext(request);

            Futures.addCallback(future, new FutureCallback<ServiceFilterResponse>() {
                @Override
                public void onFailure(Throwable e) {
                    resultFuture.setException(e);
                }

                @Override
                public void onSuccess(ServiceFilterResponse response) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (mProgressBar != null) mProgressBar.setVisibility(ProgressBar.GONE);
                        }
                    });

                    resultFuture.set(response);
                }
            });

            return resultFuture;
        }
    }
}
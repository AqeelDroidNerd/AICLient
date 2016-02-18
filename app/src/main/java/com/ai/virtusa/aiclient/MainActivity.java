//ERA

package com.ai.virtusa.aiclient;

import android.app.DialogFragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.ai.virtusa.aiclient.Controls.JSONFormatController;
import com.ai.virtusa.aiclient.Controls.MqttAndroidCient;
import com.ai.virtusa.aiclient.Controls.PostRequestController;
import com.ai.virtusa.aiclient.UI.Message;
import com.ai.virtusa.aiclient.UI.MessageAdapter;
import com.ai.virtusa.aiclient.UI.dialog;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText text;
    static ListView list;
    static String sender;
    static String name;
    public static Button button;
    public static MqttAndroidCient androidCient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(getIntent().getBooleanExtra("EXIT",false)){
            onDestroy();
            finish();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("vAssist");
        toolbar.setTitleTextColor(0xFFF);
        toolbar.setLogo(R.mipmap.ic_launcher);

        list = (ListView) findViewById(R.id.list);
        text = (EditText) this.findViewById(R.id.text);
        button = (Button) findViewById(R.id.button);

        button .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PostRequestController.evaluateResponse("DIRROUTETOBOT");
                button.setVisibility(View.INVISIBLE);
            }

        });
        button.setVisibility(View.INVISIBLE);
        setTitle("Chat with Insight Support");

        Utility.messages = new ArrayList<Message>();
        Utility.adapter = new MessageAdapter(this, Utility.messages);
        list.setAdapter(Utility.adapter);
        name ="Aqeel";
        androidCient = new MqttAndroidCient();
        androidCient.setAct(this);
        PostRequestController.setAct(this);
        if(!isNetworkAvailable()){
            DialogFragment dialog = new dialog();
            dialog.show(getFragmentManager(),"tag");
        }

        /*try {
           androidCient.connect();
        }catch(Exception s){
            Log.d("onCreate: ", "" + s);
        }*/

    }

    public static void addNewMessage(Message m)
    {
        Utility.messages.add(m);
        Utility.adapter.notifyDataSetChanged();
        list.setSelection(Utility.messages.size() - 1);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!isNetworkAvailable()){
            DialogFragment dialog = new dialog();
            dialog.show(getFragmentManager(),"tag");
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(!isNetworkAvailable()){
            DialogFragment dialog = new dialog();
            dialog.show(getFragmentManager(),"tag");
        }
    }

    public void sendMessage(View v)
    {   String newMessage = text.getText().toString();
            if(newMessage.length() > 0)
            {
                text.setText("");
                addNewMessage(new Message(newMessage, true));
                JSONFormatController json = new JSONFormatController();
                new SendMessage().execute(newMessage);
                //androidCient.pub(Utility.topic, result);
            }
    }
    private class SendMessage extends AsyncTask<String, String, String>
    {
        @Override
        protected String doInBackground(String... params) {

            this.publishProgress(String.format("sending data to support"));
            try {
                Thread.sleep(2000);
                //simulate a network call
            }catch (Exception e) {
                e.printStackTrace();
            }
            this.publishProgress(String.format("received data from support", sender));
            try {

                PostRequestController.routeMessage(params[0]);  //simulate a network call
            }catch (Exception e) {
                e.printStackTrace();
            }


            return params[0];


        }
        @Override
        public void onProgressUpdate(String... v) {

            if(Utility.messages.get(Utility.messages.size()-1).isStatusMessage)//check wether we have already added a status message
            {
                Utility.messages.get(Utility.messages.size()-1).setMessage(v[0]); //update the status for that
                Utility.adapter.notifyDataSetChanged();
                list.setSelection(Utility.messages.size() - 1);
            }
            else{
                addNewMessage(new Message(true,v[0])); //add new message, if there is no existing status message
            }
        }
        @Override
        protected void onPostExecute(String text) {
            if(Utility.messages.get(Utility.messages.size()-1).isStatusMessage)//check if there is any status message, now remove it.
            {
                Utility.messages.remove(Utility.messages.size()-1);
                Utility.adapter.notifyDataSetChanged();
            }
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Snackbar.make(this.findViewById(android.R.id.content), "Settings clicked", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();
    }
}

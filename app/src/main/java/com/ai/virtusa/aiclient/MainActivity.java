package com.ai.virtusa.aiclient;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.ai.virtusa.aiclient.Controls.MqttAndroidCient;
import com.ai.virtusa.aiclient.UI.Message;
import com.ai.virtusa.aiclient.UI.MessageAdapter;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    EditText text;
    ListView list;
    boolean sentFlag=false;
    static Random rand = new Random();
    static String sender;
    static String name;
    public static MqttAndroidCient androidCient = new MqttAndroidCient();
    static String topic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       /* Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        list = (ListView) findViewById(R.id.list);
        text = (EditText) this.findViewById(R.id.text);
        setTitle("Chat with Insight Support");

        Utility.messages = new ArrayList<Message>();
        Utility.adapter = new MessageAdapter(this, Utility.messages);
        list.setAdapter(Utility.adapter);
        name ="Aqeel";
        Utility.messages.add(new Message("Hi! " + name + " Welcome to the Insight Support", false));
        Utility.messages.add(new Message("working on speech bubbles.", true));
        Utility.messages.add(new Message("you say!", true));
        Utility.messages.add(new Message("oh thats great. how are you showing them", false));
        Utility.adapter.notifyDataSetChanged();

        addNewMessage(new Message("testing the add function", true));
        addNewMessage(new Message("Coool", false));
        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        androidCient.setAct(this);
        try {
           androidCient.connect();
        }catch(Exception s){
            Log.d("onCreate: ", "" + s);
        }

    }

    public static void addNewMessage(Message m)
    {
        Utility.messages.add(m);
        Utility.adapter.notifyDataSetChanged();
        //list.setSelection(messages.size() - 1);
    }


    public void sendMessage(View v)
    {
        String newMessage = text.getText().toString().trim();
        if(newMessage.length() > 0)
        {
            text.setText("");
            addNewMessage(new Message(newMessage, true));
            new SendMessage().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            androidCient.pub(Utility.topic, newMessage);
        }
    }
    private class SendMessage extends AsyncTask<Void, String, String>
    {
        @Override
        protected String doInBackground(Void... params) {
            try {
                Thread.sleep(2000); //simulate a network call
            }catch (InterruptedException e) {
                e.printStackTrace();
            }

            this.publishProgress(String.format("sending data to support"));
            try {
                Thread.sleep(2000); //simulate a network call
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.publishProgress(String.format("received data from support", sender));
            try {
                Thread.sleep(3000);//simulate a network call
            }catch (InterruptedException e) {
                e.printStackTrace();
            }


            return "hey";


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
            }
            /*
            addNewMessage(new Message(text, false)); // add the orignal message from server.*/
            //sub("chat/aqeel");
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}

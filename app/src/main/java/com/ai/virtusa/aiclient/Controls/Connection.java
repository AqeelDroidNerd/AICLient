package com.ai.virtusa.aiclient.Controls;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Looper;

import com.ai.virtusa.aiclient.MainActivity;
import com.ai.virtusa.aiclient.UI.Message;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ahashim on 2/18/2016.
 */
public class Connection extends AsyncTask<String,Void,StringBuffer> {
    public static int state ;

    public interface AsyncResponse {
        void processFinish(StringBuffer output);
    }
    public AsyncResponse delegate = null;

    public Connection(AsyncResponse delegate){
        this.delegate=delegate;
    }


    @Override
    protected StringBuffer doInBackground(String... strings) {
        StringBuffer response = new StringBuffer();
        try {
            URL obj = new URL(strings[0]);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            //add reuqest header
            con.setRequestMethod("POST");
            //  con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            //    con.setRequestProperty("dataType", "json");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setRequestProperty("Access-Control-Allow-Origin", "*");
            con.setRequestProperty("Access-Control-Allow-Methods", "PUT, GET, POST, DELETE, OPTIONS");
            con.setRequestProperty("Access-Control-Allow-Headers", "Origin, Accept, Content-Type, X-Requested-With, X-CSRF-Token");
            con.setRequestProperty("charset", "utf-8");
            String urlParameters = strings[1] + strings[2];// $scope.usermessage;

            // Send post request
            con.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            //   System.out.println("\nSending 'POST' request to URL : " + POST_URL);
            // System.out.println("Post parameters : " + urlParameters);
            //System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;


            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        }catch(ConnectException e){
            /*response =new StringBuffer();
            response.insert(0,"REROUTETOOPERATOR");
            response.append(strings[2]);*/
            PostRequestController.sendChatOperator(strings[2]);
        }catch(IOException e){
            e.printStackTrace();
        }

        //print result
        System.out.println("CONNECTION PART : "+response.toString());
        return response;
    }

    @Override
    protected void onPostExecute(StringBuffer s) {
        super.onPostExecute(s);
        delegate.processFinish(s);
        System.out.println("CONNECTION POST PART : " + s.toString());
    }


}
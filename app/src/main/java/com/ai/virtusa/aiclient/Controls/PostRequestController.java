package com.ai.virtusa.aiclient.Controls;

/**
 * Created by ahashim on 2/15/2016.
 */

import android.app.Activity;
import android.view.View;

import com.ai.virtusa.aiclient.MainActivity;
import com.ai.virtusa.aiclient.UI.Message;
import com.ai.virtusa.aiclient.Utility;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PostRequestController {

    public static int state;
    private static Activity act;

    public static void setAct(Activity activ) {
        act = activ;
    }

    public static void routeMessage(String usermessage) throws IOException {

        //String message = evaluateResponse(usermessage);

        if (usermessage.trim() == "") {
            System.out.println("[BOTConsole] sayChat: ERROR nothing to send");
        } else {
            if (state == 0) {
                //Add send chat bubble, send the request and clear text box

                try {
                    SendMessageAIML(usermessage);
                    //    msg = evaluateResponse(msg);

//                    UserBubble bubble = new UserBubble("AIML",msg, "S" );
//                    chatController.chatHolder.addRow(chatController.getIDtracker(), bubble.getRoot());

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (state == 1) {
                //Add send chat bubble, send the request and clear text box

                SendMessageAI(usermessage);
                //state=0;
///                msg = evaluateResponse(msg);

             /*   if (state == 0) {
                    msg=SendMessageAIML(usermessage);
                    // return;
                } else if (state == 2) {
                    sendChatOperator(msg);
                    //There is not state change from 0 to 2
                    System.out.println("[BOTConsole] sendDirectivetoBot: ERROR state change from 0 to 2");
                }
                state = 0;*/

//                UserBubble bubble = new UserBubble("AI",msg, "S" );
//                chatController.chatHolder.addRow(chatController.getIDtracker(), bubble.getRoot());

            } else if (state == 2) {

                sendChatOperator(usermessage);

            }

        }

    }



    private static void sendChatOperator(String usermessage) {
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MainActivity.button.setVisibility(View.VISIBLE);
            }
        });

        if(!MainActivity.androidCient.client.isConnected()){
            try {
                MainActivity.androidCient.connect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        JSONFormatController json = new JSONFormatController();
        String result = json.createJSONmessage("Aqeel",usermessage);
        MainActivity.androidCient.pub(Utility.topic,result);
    }

    public static void SendMessageAI(String message) throws IOException {

        String POST_URL = Utility.AI_URL;
        URL obj = new URL(POST_URL);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        //  con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        //    con.setRequestProperty("dataType", "json");
        con.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
        con.setRequestProperty("Access-Control-Allow-Origin","*");
        con.setRequestProperty("Access-Control-Allow-Methods","PUT, GET, POST, DELETE, OPTIONS");
        con.setRequestProperty("Access-Control-Allow-Headers", "Origin, Accept, Content-Type, X-Requested-With, X-CSRF-Token");
        con.setRequestProperty( "charset", "utf-8");
        String urlParameters = "user_question="+message;// $scope.usermessage;

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
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());

        final String res=  evaluateResponse(response.toString());


                if(state==1){

                    act.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Utility.messages.remove(Utility.messages.size()-1);
                            Utility.adapter.notifyDataSetChanged();
                            MainActivity.addNewMessage(new Message(res,false));
                        }
                    });
                    state = 0;
                }
                else if(state==2){
                    sendChatOperator(message);
                    //return;

                }

    }

    public static void SendMessageAIML(String message) throws IOException {
        String POST_URL = Utility.AIML_URL;
        URL obj = new URL(POST_URL);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        //  con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        //    con.setRequestProperty("dataType", "json");
        con.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
        con.setRequestProperty("Access-Control-Allow-Origin","*");
        con.setRequestProperty("Access-Control-Allow-Methods","PUT, GET, POST, DELETE, OPTIONS");
        con.setRequestProperty("Access-Control-Allow-Headers", "Origin, Accept, Content-Type, X-Requested-With, X-CSRF-Token");
        con.setRequestProperty( "charset", "utf-8");
        String urlParameters = "say="+message;// $scope.usermessage;

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        //      System.out.println("\nSending 'POST' request to URL : " + POST_URL);
        //     System.out.println("Post parameters : " + urlParameters);
        //     System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result

        String AIML = JSONFormatController.AIMLreadJSON(response.toString());
        System.out.println(AIML);

        final String res =  evaluateResponse(AIML);

            if(state==1){
                try {
                    SendMessageAI(message);
                    System.out.println("SENDING AI");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if(state==0){
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Utility.messages.remove(Utility.messages.size()-1);
                        Utility.adapter.notifyDataSetChanged();
                        MainActivity.addNewMessage(new Message(res, false));
                    }
                });

            }
            else if(state ==2){
                System.out.println("[BOTConsole] ERROR AIML");
                return;
            }
    }

    public static String evaluateResponse(String msg){

        if (msg.indexOf("DIRROUTETOBOT") > -1) {

            System.out.println("[BOTConsole] evaluateResponse: State change to 0(BOT)");
            if (state == 2) {
                //Change state
                try {
                    MainActivity.androidCient.client.disconnect();
                    MainActivity.addNewMessage(new Message("Operator disconnected",false));
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.button.setVisibility(View.INVISIBLE);
                    }
                });
                //destination = '/topic/chat.';
            }
            state = 0;
            //Remove directive
            return msg.replace("DIRROUTETOBOT", "Operator Disconnected");

        }
                    /*changed traning bot question*/
        else if (msg.indexOf("DIRDONOTTRAIN") > -1) {
            System.out.println("[BOTConsole] evaluateResponse: Do not train the question & answer");
            return msg.replace("DIRDONOTTRAIN", "");

        } else if (msg.indexOf("DIRROUTETOAI") > -1) {
            state = 1;
            System.out.println("[BOTConsole] evaluateResponse: State change to 1(AI)" + state);
            //Change state


            //Remove directive
            return msg.replace("DIRROUTETOAI", "");

        } else if (msg.indexOf("DIRROUTETOOPERATOR") > -1) {

            String replace = "";
            state =2;

            return msg.replace("DIRROUTETOOPERATOR", replace); //.replace("DIRROUTETOOPERATOR", "");



        } else if (msg.indexOf("DIROPENINTRODUCEDVIOLATIONS") > -1) {

            //Fire event to open introduced violations
            //SetTimeout( $rootScope.$broadcast('appTrigger', "open_introduced_violations"), 5000 );


            //Remove directive
            return msg.replace("DIROPENINTRODUCEDVIOLATIONS", "");

        } else if (msg.indexOf("DIROPENRUNTIMEVIOLATIONS") > -1) {

            //Fire event to open introduced violations


            //Remove directive
            return msg.replace("DIROPENRUNTIMEVIOLATIONS", "");


        } else if (msg.indexOf("DIROPENBEACON") > -1) {

            return msg.replace("DIROPENBEACON", "");

        } else if (msg.indexOf("DIRDONOTHING") > -1) {

            //Do nothing
            return msg.replace("DIRDONOTHING", "");

        } else { //No directives

            return msg;
        }
    }


}

package com.ai.virtusa.aiclient.Controls;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.ai.virtusa.aiclient.MainActivity;
import com.ai.virtusa.aiclient.UI.Message;
import com.ai.virtusa.aiclient.Utility;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttAndroidCient implements MqttCallback {
    public static MqttClient client;
    public Activity act;
    public void setAct(Activity act){
        this.act = act;
    }
    public MqttClient connect() {
        try {
            MemoryPersistence persistance = new MemoryPersistence();
            client = new MqttClient(Utility.urlFromat, "AQEEL", persistance);
            client.connect();
            //client.subscribe("chat/+");
            sub("chat/+");


            return client;
        } catch (MqttException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //Sending messages to a particular topic

    public  boolean pub(String topic, String sendMsg) {
        MqttMessage message = new MqttMessage(sendMsg.getBytes());
        if(client != null) {
            try {
                if (!client.isConnected())
                    client.connect();

/*			JSONObject json = new JSONObject();
			try
			{
				json.put("from", "Thuan");
				json.put("msg", displayMessage);
			}
			catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/

                client.publish(topic, message);
//                Toast toast = Toast.makeText(act.getApplicationContext(), topic, Toast.LENGTH_LONG);
//                toast.show();
                return true;

            } catch (MqttPersistenceException e) {
                e.printStackTrace();

            } catch (MqttException e) {
                e.printStackTrace();
            }
        }else{
            connect();
            try {
                client.publish(topic, message);
            } catch (MqttException e) {
                e.printStackTrace();
            }
//            Toast toast = Toast.makeText(act.getApplicationContext(), topic, Toast.LENGTH_LONG);
//            toast.show();
            return true;
        }

        return false;
    }





    @Override
    public void connectionLost(Throwable arg0) {
        // TODO Auto-generated method stub
        new Toast(act.getApplicationContext()).makeText(act.getApplicationContext(),"Connection Lost",Toast.LENGTH_LONG);
    }


    @Override
    public void messageArrived(String topic, final MqttMessage message) throws Exception {
        // TODO Auto-generated method stub
        Utility.displayMessage.add(message);

/*		 	JSONObject json = new JSONObject((Map) message);
			final String json_message = json.optString("msg");*/

		 	/*final JSONObject json = new JSONObject((Map) message);
		 	final String json_message = json.optString("msg");
		 	displayMessage.add(CharSequence.json_message);*/


		 	/*JSONObject jsonObject=new JSONObject((Map) message);
            jsonObject=jsonObject.getJSONObject("1");
            final String msg=jsonObject.getString("msg");*/


        if(!Utility.displayMessage.isEmpty()){

            Log.d("Received", topic + ": " + message + Utility.displayMessage.size());
            final Message msg = new Message(""+message,false);
            JSONFormatController json = new JSONFormatController();
            final String result[] = json.readJSONmessage(""+message);
            final String evaluate = PostRequestController.evaluateResponse(result[0]);
            act.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    try {
                        if (!Utility.displayMessage.isEmpty())

				           /* if(displayMessage.size()){
				            	displayMessage.remove();
				            }*/

                        if(result[2] != null && evaluate != "Operator Disconnected")
                            MainActivity.addNewMessage(new Message(evaluate, false));
                        if(result[2] != null && evaluate == "Operator Disconnected")
                            MainActivity.addNewMessage(new Message(true,evaluate));
                        //Toast.makeText(act.getApplicationContext(), "" + message, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });







        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }


    public boolean sub(String topic) {
        try {
            client.subscribe(topic);
            client.setCallback(this);
            return true;

        } catch (MqttPersistenceException e) {
            e.printStackTrace();

        } catch (MqttException e) {
            e.printStackTrace();
        }
        return false;
    }

}


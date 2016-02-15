package com.ai.virtusa.aiclient;

import com.ai.virtusa.aiclient.UI.Message;
import com.ai.virtusa.aiclient.UI.MessageAdapter;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by ahashim on 2/11/2016.
 */
public class Utility{
        public static String topic = "chat/aqeel";
        public static String url = "192.168.1.103";
        public static String urlFromat = "tcp://" + url + ":1883";
        public static ArrayList<Message> messages;
        public static MessageAdapter adapter;
        public static Queue<MqttMessage> displayMessage = new LinkedList<MqttMessage>();
}

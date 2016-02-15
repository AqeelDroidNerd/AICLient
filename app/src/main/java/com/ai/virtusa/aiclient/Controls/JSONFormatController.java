package com.ai.virtusa.aiclient.Controls;

/**
 * Created by DELL on 2/15/2016.
 */
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Created by mmursith on 1/22/2016.
 */
public class JSONFormatController {


    public static void main(String[] args){
        String json = createJSONmessage("nifras", "Hello");
        System.out.println(json);
        String [] strings = readJSONmessage(json);
        System.out.println(strings[0]);
        System.out.println(strings[1]);
    }

    public static String createJSONmessage(String owner, String text){
        JSONObject obj = new JSONObject();
        obj.put("owner", owner);
        obj.put("text",text);
        obj.put("corre-id", null);
        return obj.toString();


    }

    public static String [] readJSONmessage(String JSON){

        String [] messg = new String[3];
        try {
            // System.out.println("Decoding :"+  JSON);
            JSONObject jsonObject = (JSONObject) (new JSONParser().parse(JSON));

            String text = (String) jsonObject.get("text");
            String owner = (String) jsonObject.get("owner");

            messg[0] = text;
            messg[1] = owner;
            if(jsonObject.containsKey("corre-id"))
                messg[2] = (String) jsonObject.get("corre-id");
            else
                messg[2] ="corre-id";
            // System.out.println(messg);

        } catch (ParseException  e) {
            //
            messg[0] = JSON;
            messg[1] = null;

            if(JSON.contains(",")) {
                String[] str = JSON.split(",");
                if (str[1].contains("\"text\":")) {
                    String[] temp = str[1].split(":");
                    messg[0] = temp[1].substring(1, temp[1].length() - 2);

                } else {
                    messg[0] = JSON;
                    messg[1] = null;
                }
            }

            //    e.printStackTrace();
        }
        catch (Exception e) {
            messg[0] = JSON;
            messg[1] = null;
            if(JSON.contains(",")) {
                String[] str = JSON.split(",");
                if (str[1].contains("\"text\":")) {
                    String[] temp = str[1].split(":");
                    messg[0] = temp[1].substring(1, temp[1].length() - 2);

                } else {
                    messg[0] = JSON;
                    messg[1] = null;
                }
            }

            //  e.printStackTrace();
        }


        return messg;
    }

    public static String AIMLreadJSON(String JSON){

        String text = "";
        try {
            // System.out.println("Decoding :"+  JSON);
            JSONObject jsonObject = (JSONObject) (new JSONParser().parse(JSON));
            text = (String) jsonObject.get("botsay");

            // System.out.println(messg);

        } catch (ParseException  e) {
            //
            //    e.printStackTrace();
            return JSON;
        }
        catch (Exception e) {
            //  e.printStackTrace();
            return null;
        }


        return text;
    }
}
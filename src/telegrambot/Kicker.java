/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package telegrambot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Pau
 */
public class Kicker {
    
    private String[] fixText(String content){
        String contentlow = content.toLowerCase();
        contentlow = contentlow.replace(","," ");
        contentlow = contentlow.replace("."," ");
        contentlow = contentlow.replace("/"," ");
        contentlow = contentlow.replace("?"," ");
        contentlow = contentlow.replace("!"," ");
        return contentlow.split(" ");
    }
    
    boolean kick(String content){
        if (content == null) return false;
        boolean ret=false;
        
        
        String[] words = fixText(content);
        for (int i = 0; i < words.length && !ret; ++i) {
            int j = 0;
            int k = Keys.badwords.length-1;
            while (j <= k && !ret) {
                int m = (j+k)/2;
                int comp = words[i].compareTo(Keys.badwords[m]);
                if (comp < 0) {
                    k = m-1;
                }
                else if (comp > 0) {
                    j = m+1;
                }
                else ret = true;
            }
        }
        return ret;
    }
    
    boolean kickImage(String file_id) throws MalformedURLException, IOException, ParseException{
        URL url = new URL(Keys.bot_base_url + "getFile?file_id=" + file_id);
        
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

        String inputLine;
        while ((inputLine = in.readLine()) != null){

            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(inputLine);
            JSONObject result = (JSONObject) obj.get("result");
            String filepath = (String) result.get("file_path");
            String photo_url_string = "https://api.telegram.org/file/bot288665307:AAFAMAOX8W-U3tXlXaRvCazK8gou1GgdmdE/"+filepath;
            String base_analysis_url_string = "https://api.projectoxford.ai/vision/v1.0/analyze";
            base_analysis_url_string += "?visualFeatures=Adult";
            
            
            URL base_analysis_url = new URL(base_analysis_url_string);
            HttpsURLConnection con = (HttpsURLConnection) base_analysis_url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", Keys.USER_AGENT);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            con.setRequestProperty("Ocp-Apim-Subscription-Key", "e33ca72e0f86493296407cfa187afd07");
            con.setRequestProperty("Content-Type","application/json"); 
            con.setDoOutput(true);
            String photo_url_json = "{\"url\":\""+photo_url_string+"\"}";
            byte[] outputInBytes = photo_url_json.getBytes("UTF-8");
            OutputStream os = con.getOutputStream();
            os.write(outputInBytes);    
            os.close();
            
            
            int responseCode = con.getResponseCode();
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine2;
            StringBuffer response = new StringBuffer();

            while ((inputLine2 = reader.readLine()) != null) {
                    response.append(inputLine2);
            }
            reader.close();
            JSONObject response_obj = (JSONObject) parser.parse(response.toString());
            JSONObject adult = (JSONObject) response_obj.get("adult");
            boolean isAdultContent = (boolean) adult.get("isAdultContent");
            boolean isRacyContent = (boolean) adult.get("isRacyContent");
            
            if (isAdultContent||isRacyContent) return true;
        }
        return false;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package telegrambot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import javax.net.ssl.HttpsURLConnection;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author David
 */
public class OrtoCorrector {
    public void corregirMissatge (Missatge missatge, long chatId) throws Exception {
        if (missatge.text == null) return;
        if (missatge.text.equals("/spellcheck")) return;
        HttpsURLConnection connection = (HttpsURLConnection) Utils.spellCheckURL(missatge).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", Utils.USER_AGENT);
        connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        connection.setRequestProperty("Ocp-Apim-Subscription-Key", "9e0c572f964d4a239128de5941c37f52");// clau antiga 096215d5d28d4d86a83b846dfe2d9f66
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = reader.readLine()) != null) {
                response.append(inputLine);
        }
        reader.close();
        
        JSONParser parser = new JSONParser();
        JSONObject result = (JSONObject) parser.parse(response.toString());
        JSONArray flaggedTokens = (JSONArray) result.get("flaggedTokens");
        
        if (flaggedTokens.size() > 0) {
            String output = missatge.username + ", you have made some spelling mistakes:\n";
            for (int i = 0; i < flaggedTokens.size(); ++i) {
                JSONObject token = (JSONObject) flaggedTokens.get(i);
                output += "- You have written " + token.get("token") + " and maybe you meant ";
                JSONArray suggestions = (JSONArray) token.get("suggestions");
                for (int j = 0; j < suggestions.size(); ++j) {
                    if (j > 0) output += " or ";
                    output += ((JSONObject)suggestions.get(j)).get("suggestion");
                }
                output += "\n";
            }
            URLsender.sendMissatge(output, chatId);
        }
    }
}

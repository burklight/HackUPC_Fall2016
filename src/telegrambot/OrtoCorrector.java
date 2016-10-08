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
    
    
    public void corregirMissatge (Missatge m, long chatId) throws Exception {
        if (m.text == null) return;
        String url = "https://api.cognitive.microsoft.com/bing/v5.0/spellcheck/";
        url += "?text="+URLEncoder.encode(m.text, "UTF-8");
        url += "&mode=spell";
        if (m.anterior != null && m.anterior != null) url += "&preContextText=" + URLEncoder.encode(m.anterior, "UTF-8");
        
        JSONObject head = new JSONObject();
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", Keys.USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setRequestProperty("Ocp-Apim-Subscription-Key", "9e0c572f964d4a239128de5941c37f52");// clau antiga 096215d5d28d4d86a83b846dfe2d9f66
        int responseCode = con.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
        }
        in.close();
        
        JSONParser parser = new JSONParser();
        JSONObject res = (JSONObject) parser.parse(response.toString());
        
        JSONArray ft = (JSONArray) res.get("flaggedTokens");
        
        if (ft.size() > 0) {
            String output = m.username + ", you have made some spelling mistakes:\n";
            for (int i = 0; i < ft.size(); ++i) {
                JSONObject token = (JSONObject) ft.get(i);
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

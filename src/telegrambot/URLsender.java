/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package telegrambot;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import javax.net.ssl.HttpsURLConnection;

/**
 *
 * @author David
 */
public class URLsender {
    
    public static void sendMissatge(String text, long chatId) throws Exception {
        HttpsURLConnection connection = (HttpsURLConnection) Keys.sendMessageURL(chatId, text).openConnection();

        //add reuqest header
        connection.setRequestMethod("POST");
        connection.setRequestProperty("User-Agent", Keys.USER_AGENT);
        connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        String urlParameters = "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";

        // Send post request
        connection.setDoOutput(true);
        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
        outputStream.writeBytes(urlParameters);
        outputStream.flush();
        outputStream.close();

        BufferedReader reader = new BufferedReader(
            new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = reader.readLine()) != null) {
                response.append(inputLine);
        }
        reader.close();
    }
}

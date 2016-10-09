/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package telegrambot;
import java.net.*;
import java.io.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.json.simple.*;
import org.json.simple.parser.*;

public class URLreader {
    
    String missatge_anterior;
    long maxupdate;
    Kicker kicker;
    Set spellcheck;
    
    public URLreader() throws Exception {
        maxupdate = 0;
        kicker = new Kicker();
        spellcheck = new HashSet();
    }
    
    private void enviaMissatge(Missatge missatge) throws Exception{
        if (missatge != null && missatge.text != null) URLsender.sendMissatge(missatge.text, missatge.chat_id);
    }
    
    
    public void tractaMissatge(JSONObject missatge_json) throws IOException, MalformedURLException, ParseException, Exception{
        long updateid = (long) missatge_json.get("update_id");
        maxupdate = updateid;
        JSONObject message_content = (JSONObject) missatge_json.get("message");
        JSONObject message_sender = (JSONObject) message_content.get("from");
        String username = (String) message_sender.get("first_name");
        long user_id = (long) message_sender.get("id");
        JSONObject chat = (JSONObject) message_content.get("chat");
        String content = (String) message_content.get("text");
        long chat_id = (long) chat.get("id");
        
        
        if (content != null && content.equals("/spellcheck")){
            boolean isDisabled = spellcheck.contains(chat_id);
            if (isDisabled) {
                spellcheck.remove(chat_id);
                enviaMissatge(new Missatge("Spell checking enabled.", chat_id, null, null));
            }else {
                spellcheck.add(chat_id);
                enviaMissatge(new Missatge("Spell checking disabled.", chat_id, null, null));
            }
        }
        
        JSONArray photos = (JSONArray) message_content.get("photo");
        boolean kick=false;
        if (photos != null && photos.size() >0){
            JSONObject photo = (JSONObject) photos.get(photos.size()-1);
            String file_id = (String) photo.get("file_id");
            kick=kicker.kickImage(file_id);
            
        }
        kick = kick || kicker.kick(content);
        if (kick) {
            URL kick_url = Keys.kickURL(chat_id, user_id);
            try {
                enviaMissatge(Keys.kickedUserMessage(username, chat_id));
                new BufferedReader(new InputStreamReader(kick_url.openStream()));
            }
            catch(java.io.IOException e) {
                enviaMissatge(Keys.errorKickedUserMessage(username, chat_id));
            }
        }else{
            Missatge missatge = new Missatge(content, chat_id,missatge_anterior,username);
            OrtoCorrector oc = new OrtoCorrector();
            if (!spellcheck.contains(missatge.chat_id)) oc.corregirMissatge(missatge, missatge.chat_id);
            missatge_anterior = missatge.text;
        }
    }
    public void llegeix() throws Exception{
        BufferedReader reader = new BufferedReader(new InputStreamReader(Keys.updateURL(maxupdate+1).openStream()));
        String inputLine;
        String text="";
        while ((inputLine = reader.readLine()) != null){
            text= text+inputLine; //text es tot el que hi ha al link del bot
        }
        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(text);
        JSONArray result = (JSONArray) obj.get("result");
        Iterator iterator = result.iterator();
        while (iterator.hasNext()) {
            JSONObject missatge = (JSONObject) iterator.next();
            tractaMissatge(missatge);
        }     
    }
}
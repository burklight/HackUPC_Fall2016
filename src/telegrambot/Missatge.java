/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package telegrambot;

/**
 *
 * @author Pau
 */
public class Missatge {
    String text;
    long chat_id;
    String anterior;
    String username;
    
    public Missatge(String text, long chat_id, String anterior, String username){
        this.text = text;
        this.chat_id = chat_id;
        this.anterior = anterior;
        this.username = username;
    }
}

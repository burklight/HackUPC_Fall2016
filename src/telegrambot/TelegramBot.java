/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package telegrambot;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pau
 */
public class TelegramBot {
    public static void main(String[] args) {
        try {
            URLreader urlreader = new URLreader();
            while (true){
                urlreader.llegeix();
                TimeUnit.SECONDS.sleep(1);
            }
        } catch (Exception ex) {
            Logger.getLogger(TelegramBot.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
}

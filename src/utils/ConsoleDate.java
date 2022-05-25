/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author gilson
 */
public class ConsoleDate {
    public ConsoleDate(){
    }
    public static String getConsoleDate(){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm:ss a");
        return sdf.format(date) + " >> ";
    }
}

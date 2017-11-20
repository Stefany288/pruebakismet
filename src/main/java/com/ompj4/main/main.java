/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ompj4.main;

import com.cloudbees.syslog.Facility;
import com.cloudbees.syslog.MessageFormat;
import com.cloudbees.syslog.Severity;
import com.ompj4.syslog.UdpSyslogMessageSender;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Logger;

/**
 *
 * @author User
 */
public class main {
    
    public static String nombre = "1132";
    public static String ip= "10.2.78.8";
    public static String severity = "3";
    
    public main(){
    }
      public static void main(String... args) throws Throwable {
       ArrayList<String> infoSyslog = new ArrayList<String>();
       infoSyslog.add("|"+nombre+"|"+ip+"|"+severity);
        // Creación paquete Syslog esto
        
        UdpSyslogMessageSender messageSender = new UdpSyslogMessageSender();
        messageSender.setDefaultMessageHostname(""); // some syslog cloud services may use this field to transmit a secret key
        messageSender.setDefaultAppName("kismet");
        messageSender.setDefaultFacility(Facility.USER);
        messageSender.setDefaultSeverity(Severity.ALERT);
        //messageSender.setSyslogServerHostname("10.2.78.8");
        messageSender.setSyslogServerHostname("192.168.0.18");
        messageSender.setSyslogServerPort(514);
        messageSender.setMessageFormat(MessageFormat.RFC_3164);
        //Definición campos del mensaje
        
        while(true){
        
        try {
        // send a Syslog message
       
                System.out.println("ENVIO ");
                messageSender.sendMessage(infoSyslog.get(0));
                System.out.println("paso "+infoSyslog.get(0));
            
        } catch (IOException ex) {
        Logger.getLogger(ex.getMessage());
        }
        }
        
}

}

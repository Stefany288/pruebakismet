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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author User
 */
public class WatchDirectory {
       public static void main(String[] args) throws Exception {
        //String fichero = "C:\\Users\\User\\Documents\\NetBeansProjects\\syslogKismet\\src\\main\\java\\prueba";
        String fic = JOptionPane.showInputDialog("Ingrese la direccion donde esta el archivo:");
        String fichero = fic;
        if (args == null || args.length == 0) {
            System.out.println("Usage (example): java -cp . WatchDirectory /var/log");
        }
        Path directoryPath = FileSystems.getDefault().getPath(fichero);
        if (!Files.exists(directoryPath)) {
            
            System.out.println(String.format("The directory %s must be a real directory !", directoryPath.toString()));
        }
        System.out.println(String.format("Watching for events happening in the directory %s", fichero));
        
        WatchDirectory wDirectory = new WatchDirectory(directoryPath);
        wDirectory.processEvents(directoryPath);
    }
 
    private WatchService wService;
    private WatchKey key;
 
    public WatchDirectory(Path directoryPath) throws Exception {
        /**
         * The object to watch must implements the interface java.nio.file.Watchable
         */
        /* 1. get a new WatchService  */
        
        wService = FileSystems.getDefault().newWatchService();
        directoryPath.register(wService, StandardWatchEventKinds.ENTRY_CREATE,StandardWatchEventKinds.ENTRY_MODIFY);
    }
 
    public void processEvents(Path directoryPath) throws Exception {
        String fichero = "C:\\Users\\User\\Documents\\NetBeansProjects\\syslogKismet\\src\\main\\java\\prueba\\archivo.txt";
        int lineas = 0;
        borrarArchivos(fichero);
        while(true) {
            /* Wait until we get some events */
            System.out.println("Waiting for key be signalled with wService.take()");
            key = wService.take();
            if (key.isValid()) {
                 List<WatchEvent<?>> events = key.pollEvents();
                 for(WatchEvent<?> event: events) {
                     /* In the case of ENTRY_CREATE, ENTRY_DELETE, and ENTRY_MODIFY events the context is a relative */
                     Path path = (Path)event.context();
                     WatchEvent.Kind<?> kindOfEvent = event.kind();
                     System.out.println(String.format("Event '%s' detected in file/direcotry '%s'", kindOfEvent.name(),path));
                    if(kindOfEvent.name() == "ENTRY_MODIFY"){
                      
                     leerArchivo(path, lineas, directoryPath);
                     //lineas++;
                     }
                     
                 }
            }
            /* once an key has been processed,  */
            boolean valid = key.reset();
            System.out.println(String.format("Return value from key.reset() : %s", valid) );
        }
    }
    
    public void leerArchivo(Path a, int posiModificada, Path directoryPath){
         String fichero = directoryPath+"\\"+a;
         List<String> lineas = new ArrayList<String>();
         // Initialise sender
 UdpSyslogMessageSender messageSender = new UdpSyslogMessageSender();
        messageSender.setDefaultMessageHostname(""); // some syslog cloud services may use this field to transmit a secret key
        messageSender.setDefaultAppName("kismet");
        messageSender.setDefaultFacility(Facility.USER);
        System.out.println("lo que tiene USER"+Facility.USER.name());
        messageSender.setDefaultSeverity(Severity.ALERT);
        //messageSender.setSyslogServerHostname("10.2.78.8");
        messageSender.setSyslogServerHostname("192.168.0.5");
        messageSender.setSyslogServerPort(514);
        messageSender.setMessageFormat(MessageFormat.RFC_3164);
         
         try {
      FileReader fr = new FileReader(fichero);
      BufferedReader br = new BufferedReader(fr);
 
      String linea;
      while((linea = br.readLine()) != null){
          lineas.add(linea);
          //System.out.println(linea);
      }
          
      for(int i= 0; i<lineas.size(); i++){
           System.out.println(lineas.get(i));
          // send a Syslog message
          
          try{ 
              messageSender.sendMessage(lineas.get(i));
                  System.out.println("el mensaje enviado fue:"+ lineas.get(i));}catch (IOException ex) {
        Logger.getLogger(ex.getMessage());
        }
         
          
      } 
      fr.close();
    }
    catch(Exception e) {
      System.out.println("Excepcion leyendo fichero "+ fichero + ": " + e);
    }
  }
    public void borrarArchivos(String archiv) throws IOException{
     String fichero = archiv;
     File archivo = new File(fichero);
     BufferedWriter bw = new BufferedWriter(new FileWriter(archivo));
      bw.write("");
      bw.close();
     
    }

}
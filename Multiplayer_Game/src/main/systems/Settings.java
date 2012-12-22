package main.systems;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Settings {
	
	public boolean useShaders = false;
	public String serverIP = "localhost";
	public boolean fpsBenchmark = false;
	public Settings() {
		Properties prop = new Properties();
		 
    	try {
               //load a properties file
    		prop.load(new FileInputStream("settings"));
    		if(prop.getProperty("useShaders").equals("true")) {
    			useShaders=true;
    		}
    		prop.load(new FileInputStream("settings"));
    		if(prop.getProperty("fpsBenchmark").equals("true")) {
    			fpsBenchmark=true;
    		}
    		if(prop.getProperty("serverIP")!=null) {
    			serverIP = prop.getProperty("serverIP");
    		}

    		

    	} catch (IOException ex) {
    		ex.printStackTrace();
        }
	}
}

package main.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import network.serialization.Register;


import objects.Bullet;
import objects.Playerlist;
import objects.characters.Player;
import objects.characters.Zombie;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Server;


public class MPServer {
	Server server;
	static int port_tcp = 54555;
	static int port_udp = 54777;

	Playerlist playerlist = new Playerlist();
	int respondTimer = 0;
	
	private int[] playerids = new int[16];
	
	public List<Bullet> bullets = new ArrayList<Bullet>();
	public List<Zombie> zombies = new ArrayList<Zombie>();
	
	public void start() {
		// Create objects

		zombies.add(new Zombie(0,0));
		
		// Start server
		
		for(int i=0;i<16;i++) {
			playerids[i] = 999;
		}
		System.out.println("[SERVER] Starting Server...");
		server = new Server(); 
		server.start(); 
		
		
		
		
		try {
			System.out.println("[SERVER] Binding ports ("+port_tcp+" and "+port_udp+")...");
			server.bind(port_tcp, port_udp);
		} catch (IOException e) {
			System.out.println("[SERVER] ERROR BINDING PORTS: ");
			e.printStackTrace();
		}
		
		// Register
		System.out.println("[SERVER] Registering classes.");
		Kryo kryo = server.getKryo();
		Register.register(kryo);
		server.addListener(new ServerListener(this));
		

		
		Runnable updaterRunnable = new Runnable() {
			public void run() {
				try {
					while(true) {
			    		long nextRefreshTime=System.currentTimeMillis()+1000;
						
			    		server.sendToAllUDP(playerlist);
						server.sendToAllUDP(zombies);		   
						for(Zombie zombie : zombies) {
							zombie.update(playerlist.players,bullets);
						}
	    				long waitTime = nextRefreshTime - System.currentTimeMillis();
	    				if(waitTime>0) {
	    					Thread.sleep(waitTime);
	    				}
						
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		Thread updater = new Thread(updaterRunnable);
		updater.start();
		
		 new Thread(){
		        public void run(){
		    		long lastUpdate=System.currentTimeMillis();
		    		long lastUpdate2=System.currentTimeMillis();
		    		int tickTime = 1000;
		    		int tickTime2 = 1000/30;
		        	while(true){ // TODO: Make a thread for this 
//		  	    	  connection.sendUDP(mpserver.playerlist);
//			    	  connection.sendUDP(mpserver.zombies);
		        		if(System.currentTimeMillis()>lastUpdate2+tickTime2) {
		    				lastUpdate2=System.currentTimeMillis();
		    				//server.sendToAllUDP(playerlist);
		    				//server.sendToAllUDP(zombies);		   
		    				for(Zombie zombie : zombies) {
		    					zombie.update(playerlist.players,bullets);
		    				}
		    				bullets.clear();
		    			}	
		    			if(System.currentTimeMillis()>lastUpdate+tickTime) {
		    				lastUpdate=System.currentTimeMillis();
		    				for(int i=0;i<bullets.size();i++) {
		    					if(bullets.get(i).timeOfCreation + bullets.get(i).LIFETIME < System.currentTimeMillis()) {
		    						bullets.remove(i);
		    					}
		    				}

		    			}			
		    		}
		        }
		      }.start();
		
	}
	
	public static void main(String[] args) {
		MPServer starter = new MPServer();
		starter.start();
	}
	
	
	public int add(int connectionID) {
		int player_id = 0;
		for(int i=0;i<16;i++) {
			if(playerids[i]==999) {
				player_id = i;
				playerids[player_id] = connectionID;
		    	playerlist.players[player_id] = new Player(0,0);
				playerlist.players[player_id].player_id = player_id;
				System.out.println("[SERVER] ADDED: #"+connectionID+" @"+player_id);
				break;
		    }				
			if(i==15) {
				player_id = 999;
			}
		}
		return player_id;
	}
	public void remove(int connectionID) {
		for(int i=0;i<16;i++) {
			if(playerids[i]==connectionID) {
				int player_id = i;
				playerids[player_id] = 999;
				playerlist.players[player_id] = null;
				System.out.println("[SERVER] REMOVED: #"+connectionID+" @"+player_id);
				break;
			}
		}
	}

}


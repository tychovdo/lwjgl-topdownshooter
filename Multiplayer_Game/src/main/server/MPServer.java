package main.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import main.map.Map;
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
	
	double lastAdd;
	
	
	private int[] playerids = new int[16];
	
	public List<Bullet> bullets = new CopyOnWriteArrayList<Bullet>();
	public List<Zombie> zombies = new ArrayList<Zombie>();
	
	boolean mustSpawn = false;
	
	private Map map = new Map();
	
	public void spawnZombies() { // TODO : remove this stuff :)

		mustSpawn=true;
	}
	public void start() {
		// Create objects
		System.out.println("[SERVER] Spawning zombies...");
		zombies.add(new Zombie(150,150));
		zombies.add(new Zombie(-150,-150));
		zombies.add(new Zombie(150,-150));
		zombies.add(new Zombie(-150,150));
		
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
		    		int tickTime = 500;
		    		int tickTime2 = 1000/30; // Proper time handling
		        	while(true){
		        		if(System.currentTimeMillis()>lastUpdate2+tickTime2) {
		    				lastUpdate2=System.currentTimeMillis();   
		    				for(Zombie zombie : zombies) {
								zombie.update(playerlist.players,map.getSolids());
		    				}
		    				for(Bullet bullet : bullets) {
		    					bullet.checkHits(playerlist.players,zombies);
		    				}
		    				for(int i=0;i<playerlist.players.length;i++) {
		    					if(playerlist.players[i]!=null) {
		    						map.update(playerlist.players[i]);
		    					}
		    				}
		    				if(mustSpawn) {
		    					
		    					if(System.currentTimeMillis() > lastAdd+1000) {
		    						lastAdd = System.currentTimeMillis();
		    						System.out.println("[SERVER] Spawning zombies...");
			    					zombies.add(new Zombie(150,150));
			    					zombies.add(new Zombie(-150,-150));
			    					zombies.add(new Zombie(150,-150));
			    					zombies.add(new Zombie(-150,150));
		    					}
		    					mustSpawn = false;
		    				}
		    				//bullets.clear();
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


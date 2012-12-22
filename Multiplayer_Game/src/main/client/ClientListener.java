package main.client;

import java.util.List;

import network.communication.Message;
import objects.Bullet;
import objects.Playerlist;
import objects.characters.Zombie;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class ClientListener extends Listener {
	Engine engine;
	public ClientListener(Engine engine) {
		this.engine = engine;
	}
	public void connected (Connection connection) {
		System.out.println("[CLIENT] Connected to server.");
	}
	public void disconnected (Connection connection) {
		System.out.println("[CLIENT] Disconnected from server.");
	}
	@SuppressWarnings("unchecked")
	public void received (Connection connection, Object object) {
		if (object instanceof Playerlist) {
			Playerlist playerlist2 = (Playerlist)object;
			engine.players = playerlist2.players;
		}
		if (object instanceof Message) {
	    	  Message message2 = (Message)object;
	    	  if (message2.msgType==Message.PLAYERID) {
	    		  engine.player.player_id = message2.msgValue;
	    	  }
		}
		if (object instanceof Bullet) {
			   Bullet bullet2 = (Bullet) object;
			   bullet2.timeOfCreation = System.currentTimeMillis();
			   engine.bh.recievedBullets.add(bullet2);
			   
		}
		if (object instanceof List) {
	    	  List<?> list2 = (List<?>) object;
	    	  if(!list2.isEmpty()) {
	    		  
//	    		  if(list2.get(0).getClass().equals(Crate.class)) {
//	    			  System.out.println("[CLIENT] Amount of crates: "+list2.size());
//			    	  engine.crates = (List<Crate>) list2;
//	    		  } else 
	    		  if (list2.get(0).getClass().equals(Bullet.class)) {
	    			  // Server doesn't send his bulletlist. (only individual bullets).
	    		  } else if (list2.get(0).getClass().equals(Zombie.class)) {
	    			  engine.zombies = (List<Zombie>) list2;
	    		  } else {
	    			  System.out.println("[CLIENT] Received unknown List from server.");
	    		  }
	    		  
	    	  } else {
	    		  System.out.println("[CLIENT] Received empty list.");
	    	  }
			  
		}	
	}
}

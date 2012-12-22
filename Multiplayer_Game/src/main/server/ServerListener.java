package main.server;

import network.communication.Message;
import objects.Bullet;
import objects.characters.Player;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class ServerListener extends Listener {
	MPServer mpserver;

	public ServerListener(MPServer mpserver) {
		this.mpserver = mpserver;
	}
	   public void connected (Connection connection) {
		   System.out.println("[SERVER] #"+connection.getID()+ " connected.");
			 
	   }
	   public void disconnected (Connection connection) {
		   mpserver.playerlist.players[connection.getID()]=null;
		   mpserver.remove(connection.getID());
		   System.out.println("[SERVER] #"+connection.getID()+ " disconnected.");
	   }
	   public void received (Connection connection, Object object) {
		   if (object instanceof Player) {
	    	  Player player2 = (Player)object;
	    	  
	    	  if(player2.player_id!=999) {
		    	  mpserver.playerlist.players[player2.player_id].x = player2.x;
		    	  mpserver.playerlist.players[player2.player_id].y = player2.y;
		    	  mpserver.playerlist.players[player2.player_id].rotation = player2.rotation;
		    	  mpserver.playerlist.players[player2.player_id].texture_num = player2.texture_num;
		    	  
	    	  }
	    	  connection.sendUDP(mpserver.playerlist);
	    	  connection.sendUDP(mpserver.zombies);
		   }
		   if (object instanceof Bullet) {
			   Bullet bullet2 = (Bullet) object;
			   bullet2.timeOfCreation = System.currentTimeMillis();
			   mpserver.bullets.add(bullet2);
			   mpserver.server.sendToAllTCP(bullet2); //TODO: maybe UDP?

		   }
		   if (object instanceof Message) {
			   Message message2 = (Message)object;
			   if(message2.msgType==Message.FIRSTCONNECT) {
				   int player_id = mpserver.add(connection.getID());
				   Message playeridmessage = new Message();
				   playeridmessage.msgType = Message.PLAYERID;
				   playeridmessage.msgValue = (byte) player_id;
				   connection.sendTCP(playeridmessage);
//				   connection.sendTCP(mpserver.crates);
				  
	    	  }
		   }
	   }
}

package network.serialization;


import java.util.*;

import network.communication.Message;

import com.esotericsoftware.kryo.Kryo;

public class Register {
	public static void register(Kryo kryo) {
		kryo.register(Message.class);
		
		kryo.register(List.class);
		kryo.register(ArrayList.class);
		
		kryo.register(objects.Playerlist.class);
		kryo.register(objects.characters.Character.class);
		kryo.register(objects.characters.Player.class);
		kryo.register(objects.characters.Player[].class);
		kryo.register(objects.characters.Zombie.class);

//		kryo.register(objects.Crate.class);
		kryo.register(objects.Bullet.class);


	}
}

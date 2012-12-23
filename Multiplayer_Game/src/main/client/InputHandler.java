package main.client;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;


public class InputHandler {
	Engine engine;
	private static final double RUNSPEED = 0.2;
	private static final double WALKSPEED = 0.025;
	private static final double COOLDOWN_FIRE = 500;
	
	
	boolean crouch = false;
	double speed = 0.35;
	
	double cooldown_shoot = System.currentTimeMillis();
	
	public InputHandler(Engine engine) {
		this.engine = engine;
	}
	
	public void getinput() {
		
		
		
		// ACTIONS
		
		if (Mouse.isButtonDown(0)) {
			if(cooldown_shoot+COOLDOWN_FIRE<System.currentTimeMillis()) {
				engine.shoot=true;
				cooldown_shoot = System.currentTimeMillis();
			}
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_Q)) {
			engine.player.texture_num=1;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_E)) {
			engine.player.texture_num=2;
		} else {
			engine.player.texture_num=0;
		}

			
		
		// MOVEMENT
		
			// Speed
		if(Mouse.isButtonDown(1)) {
			crouch = true;
		} else {
			crouch = false;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_C)) {
			crouch = true;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			crouch = false;
		}
		if(crouch) {
			speed = WALKSPEED;
		} else {
			speed = RUNSPEED;
		}
		
			// Directions
		if (Keyboard.isKeyDown(Keyboard.KEY_UP) || Keyboard.isKeyDown(Keyboard.KEY_W)) {
			engine.player.dy = speed;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_DOWN) || Keyboard.isKeyDown(Keyboard.KEY_S)) {
			engine.player.dy = -speed;
		} else {
			engine.player.dy = 0;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT) || Keyboard.isKeyDown(Keyboard.KEY_D)) {
			engine.player.dx = speed;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_LEFT) || Keyboard.isKeyDown(Keyboard.KEY_A)) {
			engine.player.dx = -speed;
		} else {
			engine.player.dx = 0;
		}

		// CLIENT / SERVER
		if (Keyboard.isKeyDown(Keyboard.KEY_U)) engine.client.stop();

		// DEBUGGING

		if (Keyboard.isKeyDown(Keyboard.KEY_O)) {
			engine.mpserver.spawnZombies();
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_F1)) {
			engine.player.teleportTo(engine.players[0]);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_F2)) {
			engine.player.teleportTo(engine.players[1]);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_F3)) {
			engine.player.teleportTo(engine.players[2]);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_F4)) {
			engine.player.teleportTo(engine.players[3]);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_F5)) {
			engine.player.teleportTo(engine.players[4]);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_F6)) {
			engine.player.teleportTo(engine.players[5]);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_F7)) {
			engine.player.teleportTo(engine.players[6]);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_F8)) {
			engine.player.teleportTo(engine.players[7]);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_F9)) {
			engine.player.teleportTo(engine.players[8]);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_F10)) {
			engine.player.teleportTo(engine.players[9]);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_F11)) {
			engine.player.teleportTo(engine.players[10]);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_F12)) {
			engine.player.teleportTo(engine.players[11]);
		} else {
			
		}
	}

}

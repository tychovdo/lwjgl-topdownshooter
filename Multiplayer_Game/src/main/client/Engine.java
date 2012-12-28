package main.client;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import main.map.Map;
import main.server.MPServer;
import main.systems.BulletHandler;
import main.systems.Overlay;
import main.systems.Settings;
import main.systems.SystemInfo;
import main.systems.TexturesLoader;
import network.communication.Message;
import network.serialization.Register;
import objects.Bullet;
import objects.characters.Player;
import objects.characters.Zombie;

import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.openal.SoundStore;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.util.ResourceLoader;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;

public class Engine {

	// Settings
	public static final int WIDTH = 960;
	public static final int HEIGHT = 512;
	private Settings settings = new Settings();
	
	// Input handling
	protected boolean shoot;
	private InputHandler inputHandler = new InputHandler(this);
	
	// Graphic timing
	private SystemInfo sysInfo = new SystemInfo();
	
	// Objects
	private Map map = new Map();
	
	protected Player player = new Player(0,0);
	public Player[] players = new Player[16];
	protected List<Zombie> zombies = new ArrayList<Zombie>();
	
	protected BulletHandler bh = new BulletHandler();
	private Overlay overlay;

	
	// Textures // TODO: load multiple sprites on 1 texture
	private Texture[] texture_characters = new Texture[4];
	
	private Texture texture_crate;
	private Texture texture_screeneffect;
	private Texture texture_background;

	// Sounds // TODO: Move in guns
	Audio bulletsound_shotgun;
	
	
	// Shaders
	private int shaderProgram;
	private int vertexShader;
	private int fragmentShader;
	
	// Networking
	Client client;
	int dataTimer = 0;

	Thread networkSender;
	public MPServer mpserver;

	
	
	public Engine() {
		

		// Set up display
		try {
			System.out.println("[CLIENT] Setting up display...");
			Display.setDisplayMode(new DisplayMode(WIDTH,HEIGHT));
			Display.create();
			Display.setInitialBackground(1f, 1f, 1f);
		} catch (LWJGLException e) {	
			e.printStackTrace();
			System.exit(0);
		}

		overlay = new Overlay();
		
		
		try {
			bulletsound_shotgun = AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("shotgun.wav"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		init_network(); // initialize networks
		init_GL();
		init_textures();
		

		// Set up timing
		sysInfo.getDelta(); 
		sysInfo.lastFPS = SystemInfo.getTime(); 
		sysInfo.startTime = SystemInfo.getTime();


		// Game loop
		while (!Display.isCloseRequested()) {
			int delta = sysInfo.getDelta();
			update(delta);
			updateFPS(); 
			renderGL();
			Display.update();
			Display.processMessages();
			
			if(!settings.fpsBenchmark) {
				Display.sync(60);
			}
		}
		
		// Exit
		System.out.println("[CLIENT] Ending game loop, disconnecting and closing game.");
		AL.destroy();
        glDeleteProgram(shaderProgram);
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);

		client.stop(); // Disconnect from server
		Display.destroy(); // Destroy display
		System.exit(0); // Kill the process
	}


	
	private void updateFPS() { // Calculate the FPS and set it in the title bar
		if (SystemInfo.getTime() - sysInfo.lastFPS > 1000) {
			Display.setTitle("FPS: " + sysInfo.fps);
			sysInfo.fps = 0;
			sysInfo.lastFPS += 1000;
		}
		sysInfo.fps++;
	}
	
	private void init_network() {
		// Set up Kryonet client

		client = new Client();
		client.addListener(new ClientListener(this));
		client.start();
		
		Runnable ns = new Runnable() {
			public void run() {
				try {
					while(true) {
						client.sendUDP(player);
						for(Bullet bullet : bh.toSendBullets) { // TODO: replace i with well programmed Iteration
							client.sendTCP(bullet);
							bh.toSendBullets.remove(bullet);
							Thread.sleep(10);
						}
						Thread.sleep(1000/30);
					}
				} catch (InterruptedException e) {
					System.out.println("[CLIENT] ERROR: Failed to create 'networkSender' thread");
					e.printStackTrace();
				}
			}
		};
		networkSender = new Thread(ns);

		
		// Connect to server
		try {
			client.connect(5000, settings.serverIP, 54555, 54777); //TODO: short time out of 5 seconds may cause problems in the future???
		} catch (IOException e) {
			System.out.println("[CLIENT] Disconnecting (could not connection to server)");
			System.out.println("[CLIENT] Starting own server...");
			overlay.enable("Playing local.");
			
			mpserver = new MPServer();
			mpserver.start();
			try {
				client.connect(5000, settings.serverIP, 54555, 54777); //TODO: short time out of 5 seconds may cause problems in the future???
			} catch (IOException e1) {
				System.out.println("[CLIENT] Disconnecting (could not connection to (local)server)");
				e1.printStackTrace();
				client.stop();
			}
		}
		Kryo kryo = client.getKryo();
		Register.register(kryo);
		
		// Send connect message (firstconnectmessage)
		Message firstconnectmessage = new Message();
		firstconnectmessage.msgType = Message.FIRSTCONNECT;
		client.sendTCP(firstconnectmessage);
		
		networkSender.start();
	}
	private void init_GL() {
		// Initiate screen
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, 960, 0, 512, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		
		// Enable transparency
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA); //TODO: not sure if this is the correct place
		GL11.glEnable(GL11.GL_BLEND);
		
		
		//GL11.glClearColor(1f, 1f, 1f,1f);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

		GL11.glEnable( GL11.GL_LINE_SMOOTH );
		GL11.glEnable( GL11.GL_POLYGON_SMOOTH );
		GL11.glHint( GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST );
		GL11.glHint( GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_NICEST );
		
		if(settings.useShaders) {
			System.out.print("[CLIENT] Loading shaders");
			init_shaders(); // TODO: use shaders (also with textures)
		}
	}
	private void init_shaders() {
	    shaderProgram = glCreateProgram();	
		
        vertexShader = glCreateShader(GL_VERTEX_SHADER);
        fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        StringBuilder vertexShaderSource = new StringBuilder();
        StringBuilder fragmentShaderSource = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("shader.vs"));
            String line;
            while ((line = reader.readLine()) != null) {
                vertexShaderSource.append(line).append('\n');
            }
        } catch (IOException e) {
            System.err.println("Vertex shader wasn't loaded properly.");
            e.printStackTrace();
            Display.destroy();
            System.exit(1);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }
        BufferedReader reader2 = null;
        try {
            reader2 = new BufferedReader(new FileReader("shader.fs"));
            String line;
            while ((line = reader2.readLine()) != null) {
                fragmentShaderSource.append(line).append('\n');
            }
        } catch (IOException e) {
            System.err.println("Fragment shader wasn't loaded properly.");
            Display.destroy();
            System.exit(1);
        } finally {
            if (reader2 != null) {
                try {
                    reader2.close();
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }
        glShaderSource(vertexShader, vertexShaderSource);
        glCompileShader(vertexShader);
        if (glGetShader(vertexShader, GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println("Vertex shader wasn't able to be compiled correctly.");
        }
        glShaderSource(fragmentShader, fragmentShaderSource);
        glCompileShader(fragmentShader);
        if (glGetShader(fragmentShader, GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println("Fragment shader wasn't able to be compiled correctly.");
        }
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);
        glValidateProgram(shaderProgram);
	}
	private void init_textures() {
		texture_characters[0] = TexturesLoader.loadTexture("/sprites/player.png");
		texture_characters[1] = TexturesLoader.loadTexture("/sprites/player_leanL.png");
		texture_characters[2] = TexturesLoader.loadTexture("/sprites/player_leanR.png");
		texture_characters[3] = TexturesLoader.loadTexture("/sprites/zombie.png");
		texture_crate = TexturesLoader.loadTexture("/sprites/crate.png");
		texture_screeneffect = TexturesLoader.loadTexture("/screeneffect.png");
		texture_background = TexturesLoader.loadTexture("/background.png");
		 
	}
	private void update(int delta) { 
		
		// Handle user input
		inputHandler.getinput();
		map.update(player);
		// Update objects
		player.update(delta,map.getSolids());
		if(shoot) { // TODO: move shoot-boolean to BulletHandler and make the bullethandler update itself.
			bh.shoot(player.x,player.y,player.texture_num,map.getSolids(),player.player_id);
			bh.shoot(player.x,player.y,player.texture_num,map.getSolids(),player.player_id);
			bh.shoot(player.x,player.y,player.texture_num,map.getSolids(),player.player_id);
			bh.shoot(player.x,player.y,player.texture_num,map.getSolids(),player.player_id);
			bh.shoot(player.x,player.y,player.texture_num,map.getSolids(),player.player_id);
			bulletsound_shotgun.playAsSoundEffect(1.0f, 0.3f, false);
			shoot=false;
		}
		// Send player data to server
		
		SoundStore.get().poll(0);
		

	}
	private void renderGL() {
		// RENDER SCREEN
		
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
//
		// Render background
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		//texture_background.bind();
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D,texture_background.getTextureID());
		for(int y1=-1;y1<2;y1++) {
			for(int x1=-1;x1<3;x1++) {
				double back_x = -player.x%512+(x1*512);
				double back_y = -player.y%512+(y1*512);
				GL11.glBegin(GL11.GL_QUADS);
					GL11.glTexCoord2f(0, 1);
					GL11.glVertex2d(back_x,back_y); // Bottom-left
					GL11.glTexCoord2f(1, 1);
					GL11.glVertex2d(back_x+512, back_y+0); // Bottom-right
					GL11.glTexCoord2f(1, 0);
					GL11. glVertex2d(back_x+512,back_y+512); // Upper-right
					GL11.glTexCoord2f(0, 0);
					GL11.glVertex2d(back_x+0, back_y+512); // Upper-left
				GL11.glEnd();
			}
		}

		
		// RENDER OBJECTS
		
		// Focus camera around player
	    double cam_x = player.x-(WIDTH/2);
	    double cam_y = player.y-(HEIGHT/2);
		GL11.glTranslated(-cam_x, -cam_y, 0);

		
		
		
		
		// Render shadows
		for(int i =0;i<16;i++) {
			if(players[i]!=null&&(i!=player.player_id)) { 
				players[i].rendershadow(texture_characters);
			}
		}
		player.rendershadow(texture_characters);
	
		// Render map
		
		map.render(map.player_chunkX, map.player_chunkY,texture_crate);
		
		
		
		// Render bullets
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		bh.renderBullets(player.player_id);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);

		
		// Render characters
		for(int i =0;i<16;i++) {
			if(players[i]!=null&&(i!=player.player_id)) { 
				players[i].render(texture_characters,shaderProgram);
			}
		}
		player.render(texture_characters,shaderProgram);
		
		for(Zombie zombie : zombies) {
			if(zombie.health > 0) { // tempfix
				zombie.rendershadow(texture_characters);
				zombie.render(texture_characters,shaderProgram);
			}
		}
		

		GL11.glTranslated(cam_x, cam_y, 0);
		
		// RENDER POST-EFFECTS
		
		if(!settings.fastMode) {
			// Render screeneffect
			
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			
			texture_screeneffect.bind();
			GL11.glBegin(GL11.GL_QUADS);
				GL11.glTexCoord2f(0, 1);
				GL11.glVertex2d(0,0); // Bottom-left
				GL11.glTexCoord2f(1, 1);
				GL11.glVertex2d(960, 0); // Bottom-right
				GL11.glTexCoord2f(1, 0);
				GL11. glVertex2d(960,512); // Upper-right
				GL11.glTexCoord2f(0, 0);
				GL11.glVertex2d(0, 512); // Upper-left
			GL11.glEnd();
			
			GL11.glDisable(GL11.GL_TEXTURE_2D);


			// Render overlay (text)
		overlay.render();
		}
	}
	public static void main(String[] argv) {
		new Engine();
	}


}

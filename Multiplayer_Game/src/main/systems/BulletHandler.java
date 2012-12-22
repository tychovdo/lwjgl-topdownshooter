package main.systems;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.input.Mouse;
import java.awt.Rectangle;

import objects.Bullet;

public class BulletHandler {

	public List<Bullet> recievedBullets = new ArrayList<Bullet>();
	public List<Bullet> clientBullets = new ArrayList<Bullet>();
	public List<Bullet> toSendBullets = new CopyOnWriteArrayList<Bullet>();
	
	public BulletHandler() {
		
	}


	public void shoot(double x, double y, int lean, List<Rectangle> solids) { // TODO: Bugfix:  mouse between center of player and gun, the bullets are going in opposite direction.
		

		// Values important for changing start position of bullet while leaning
		float extra = 0;
		if(lean==1) {
			extra = 90;
		} else if(lean==2) {
			extra = -90;
		}
		
		// Calculate bullet origin and direction
		double corner_x = (x)-(960/2); // TODO: constants file
		double corner_y = (y)-(512/2);
		double mouse_x = Mouse.getX() + corner_x; //TODO: Mouse must be called in input void
		double mouse_y = Mouse.getY() + corner_y;
		float rotation = (float) ((float) Math.atan2(mouse_y - y,mouse_x -x)* 180 / Math.PI);
		if(rotation<0) {
			rotation+=360;
		}
		double rotation_rads = Math.toRadians(rotation);
		
		double start_x = x+(27*Math.cos(rotation_rads));
		double start_y = y+(27*Math.sin(rotation_rads));
			
			// Calculating start position of bullet when leaning
		if(lean!=0) {
			double rotation_extra = Math.toRadians(rotation+extra);
			start_x = start_x+(14*Math.cos(rotation_extra));
			start_y = start_y+(14*Math.sin(rotation_extra));
		}

		shoot(start_x,start_y,mouse_x,mouse_y,solids);
		
		
	}
public void shoot(double x, double y, double mouse_x, double mouse_y, List<Rectangle> solids) {
		
		// Values for bullet
		double bullet_range = 200;
		int bullet_spread = 2;
		
		// Calculate bullet origin and direction
		Random r = new Random();
		float rotation = (float) ((float) Math.atan2(mouse_y - y,mouse_x -x)* 180 / Math.PI);
		if(rotation<0) {
			rotation+=360;
		}
		rotation += -bullet_spread;
		rotation += r.nextInt(bullet_spread*2);
		double rotation_rads = Math.toRadians(rotation);

		// Calculate bullet impact location
		double shot_x = 0;
		double shot_y = 0;
		
		outerloop:
		for(double d=0;d<bullet_range;d+=0.1) {
			shot_x = x+(d*Math.cos(rotation_rads));
			shot_y = y+(d*Math.sin(rotation_rads));
			
			for(Rectangle rectangle : solids) {
				
				if(rectangle.contains((int)shot_x, (int)shot_y)) {
					break outerloop;
				}
			}
		}

		

//		clientBullets.add(new Bullet(x,y,shot_x,shot_y));
//		toSendBullets.add(new Bullet(x,y,shot_x,shot_y));
		clientBullets.add(new Bullet(x,y,mouse_x,mouse_y)); // TODO: TEMPDEBUG
		toSendBullets.add(new Bullet(x,y,mouse_x,mouse_y));
	}
	public void renderBullets() {
		List<Bullet> renderBullets = new ArrayList<Bullet>(clientBullets);
		renderBullets.addAll(recievedBullets);

		for(Bullet bullet : renderBullets) {
			bullet.render();
		}
	}
}

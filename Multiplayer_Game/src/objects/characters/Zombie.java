package objects.characters;

import java.awt.Rectangle;
import java.util.List;


public class Zombie extends Character { // TODO: make a character class so Zombie and Player can share code.
	public int havingKnockback = 0;
	
	public Zombie() {

	}
	public Zombie(int x, int y) {
		this.x = x;
		this.y = y;
		texture_num = 3;
	}
	public void update(Character[] targets,List<Rectangle> list) { // use delta for framerate independency
		
		// Calculate rotation
		double closestPlayer_x = 0;
		double closestPlayer_y = 0;
		double closestPlayer_range = 100000000;
		for(Character target : targets) {
			if(target!=null) {	
				double dx = Math.abs(target.x - this.x);
				double dy = Math.abs(target.y - this.y);
				double range = Math.sqrt((dx*dx)+(dy*dy));
				if(closestPlayer_range>range) {
					closestPlayer_range = range;
					closestPlayer_x = target.x;
					closestPlayer_y = target.y;
				}
			}
		}
		rotateTo(closestPlayer_x, closestPlayer_y);
		
		
		// Calculate movement
		double speed = 2;
		if(havingKnockback>0) {
			havingKnockback--; //-delta
			speed = -20;
		}
		
		double rotation_rads = Math.toRadians(rotation);
		double dx = (speed*Math.cos(rotation_rads));
		double dy = (speed*Math.sin(rotation_rads));
		
		// COLLISION DETECTION
		
		boolean canMoveX = true;
		boolean canMoveY = true;
		for(Rectangle rectangle : list) {
			Rectangle r1_x = new Rectangle((int)(x-13+dx),(int)(y-13),26,26); // TODO: use delta here aswell (see Player class in which it is implemented)
			Rectangle r1_y = new Rectangle((int)(x-13),(int)(y-13+dy),26,26);
		
			if(rectangle.intersects(r1_x)) {
				canMoveX=false;
			}
			if(rectangle.intersects(r1_y)) {
				canMoveY=false;
			}

		}
		
		if(canMoveX) //delta here aswell?
			x+=dx;
		if(canMoveY)
			y+=dy;

	}

}

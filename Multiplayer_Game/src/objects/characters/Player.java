package objects.characters;


import java.awt.Rectangle;
import java.util.List;

import org.lwjgl.input.Mouse;

public class Player extends Character {
	// Input to be updated
	public double dx;
	public double dy;
	
	// Identification
	public int player_id = 999;
	
	public Player() {

	}
	public Player(int x, int y) {
		this.x = x;
		this.y = y;
		texture_num=0;
	}
	public void update(int delta, List<Rectangle> list) {
		// COLLISION DETECTION
		
		boolean canMoveX = true;
		boolean canMoveY = true;
		for(Rectangle rectangle : list) {
			Rectangle r1_x = new Rectangle((int)(x-13+dx*delta),(int)(y-13),26,26);
			Rectangle r1_y = new Rectangle((int)(x-13),(int)(y-13+dy*delta),26,26);
		
			if(rectangle.intersects(r1_x)) {
				canMoveX=false;
			}
			if(rectangle.intersects(r1_y)) {
				canMoveY=false;
			}

		}
		
		if(canMoveX)
			x+=dx*delta;
		if(canMoveY)
			y+=dy*delta;

		// CALCULATE ROTATION
		double corner_x = (x)-(960/2); // TODO: constants file
		double corner_y = (y)-(512/2);
		double mouse_x = Mouse.getX() + corner_x;
		double mouse_y = Mouse.getY() + corner_y;
		rotateTo(mouse_x, mouse_y);
		
	}



}

package objects.characters;

import java.awt.Point;
import java.util.List;

import objects.Bullet;

public class Zombie extends Character { // TODO: make a character class so Zombie and Player can share code.
	
	public Zombie() {

	}
	public Zombie(int x, int y) {
		this.x = x;
		this.y = y;
		texture_num = 3;
	}
	public void update(Player[] players, List<Bullet> bullets) {
		
		// Calculate rotation
		double closestPlayer_x = 0;
		double closestPlayer_y = 0;
		double closestPlayer_range = 100000000;
		for(Player player : players) {
			if(player!=null) {	
				double dx = Math.abs(player.x - this.x);
				double dy = Math.abs(player.y - this.y);
				double range = Math.sqrt((dx*dx)+(dy*dy));
				if(closestPlayer_range>range) {
					closestPlayer_range = range;
					closestPlayer_x = player.x;
					closestPlayer_y = player.y;
				}
			}
		}
		rotateTo(closestPlayer_x, closestPlayer_y);
		
		// Calculate movement
//		double speed = 2;
//		double rotation_rads = Math.toRadians(rotation);
//		x = x+(speed*Math.cos(rotation_rads));
//		y = y+(speed*Math.sin(rotation_rads));
		
		// Calculate bullet hits
		for(Bullet bullet : bullets) { // TODO: correct iteration
			if(!bullet.isChecked) {
				//Point point = bullet.closestpointbullet(x, y);
				Point point = new Point((int) bullet.end_x,(int) bullet.end_y);
	//			System.out.println("! + "+point.x+","+point.y);
				double distance = distanceBetweenTwoPoints(new Point((int)x,(int)y),point);
				System.out.println("d: "+distance);
				if(distance<12) {
					health-=10;
					System.out.println("Zombie hit");
					
				} else {
	
				}
				bullet.isChecked = true;
			}
		}
	}
	public double distanceBetweenTwoPoints(Point point1, Point point2) {
		double distance = 0;
		distance = Math.sqrt(((point1.x-point2.x)*(point1.x-point2.x))+((point1.y-point2.y)*(point1.y-point2.y)));
		return distance;
	}
}

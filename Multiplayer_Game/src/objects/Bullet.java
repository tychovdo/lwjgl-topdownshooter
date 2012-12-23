package objects;



import java.awt.Point;
import java.util.List;

import objects.characters.Player;
import objects.characters.Zombie;

import org.lwjgl.opengl.GL11;

public class Bullet {
	public double start_x;
	public double start_y;
	public double end_x;
	public double end_y;
	public int player_id;
	public long timeOfCreation = System.currentTimeMillis();
	private int damage=1;
	public final double LIFETIME = 300; //TODO: reset LIFETIME when client receives enemy bullet, or only send coordinates in server communication
	public boolean isChecked = false;
	//TODO log owner of the bullet. (if the user shoots there is a clientside and a serverside bullet: eliminate serverside bullets of own player)

	
	public Bullet() {

	}
	
	public Bullet(double start_x, double start_y, double end_x, double end_y, int player_id, int damage) {
		this.start_x = start_x;
		this.start_y = start_y;
		this.end_x = end_x;
		this.end_y = end_y;
		this.player_id = player_id;
		this.damage = damage;
	}
	public Point closestpointbullet(double x, double y){ 
		     double A1 = end_y - start_y; 
		     double B1 = start_x - end_x; 
		     double C1 = (end_y - start_y)*start_x + (start_x - end_y)*start_y; 
		     double C2 = -B1*x + A1*y; 
		     double det = A1*A1 - -B1*B1; 
		     double cx = 0; 
		     double cy = 0; 
		     if(det != 0){ 
		      cx = (float)((A1*C1 - B1*C2)/det); 
		      cy = (float)((A1*C2 - -B1*C1)/det); 
		     }else{ 
		     cx = x; 
		     cy = y; 
		     } 
		     return new Point((int)cx, (int)cy); 
		}
	public void checkHits(Player[] players, List<Zombie> zombies) {
		// Calculate bullet hits
		for (Zombie zombie : zombies) { // TODO: correct iteration
			if(!isChecked) {
				if(circleLineIntersect((float)start_x,(float)start_y,(float)end_x,(float)end_y,(float)zombie.x,(float)zombie.y,12)) {
					zombie.health-=damage;
					System.out.println("[SERVER] Zombie hit");
					
				}
			}
		}
		for (int i=0;i<players.length;i++) { // TODO: correct iteration
			if(!isChecked) {
				if(players[i]!=null) {
					if(circleLineIntersect((float)start_x,(float)start_y,(float)end_x,(float)end_y,(float)players[i].x,(float)players[i].y,12)) {
						players[i].health-=damage;
						System.out.println("[SERVER] Player hit");
					}
				}
			}
		}
		isChecked = true;
	}
	boolean circleLineIntersect(float x1, float y1, float x2, float y2, float cx, float cy, float cr ) {
		float dx = x2 - x1;
		float dy = y2 - y1;
		float a = dx * dx + dy * dy;
		float b = 2 * (dx * (x1 - cx) + dy * (y1 - cy));
		float c = cx * cx + cy * cy;
		c += x1 * x1 + y1 * y1;
		c -= 2 * (cx * x1 + cy * y1);
		c -= cr * cr;
		float bb4ac = b * b - 4 * a * c;

		// println(bb4ac);

		if (bb4ac < 0) { // Not intersecting
			return false;
		} else {
			float mu = (float) ((-b + Math.sqrt(b * b - 4 * a * c)) / (2 * a));
			float ix1 = x1 + mu * (dx);
			float iy1 = y1 + mu * (dy);
			mu = (float) ((-b - Math.sqrt(b * b - 4 * a * c)) / (2 * a));
			float ix2 = x1 + mu * (dx);
			float iy2 = y1 + mu * (dy);

			// The intersection points
			// ellipse(ix1, iy1, 10, 10);
			// ellipse(ix2, iy2, 10, 10);

			float testX;
			float testY;
			// Figure out which point is closer to the circle
			if (dist(x1, y1, cx, cy) < dist(x2, y2, cx, cy)) {
				testX = x2;
				testY = y2;
			} else {
				testX = x1;
				testY = y1;
			}

			if (dist(testX, testY, ix1, iy1) < dist(x1, y1, x2, y2)
					|| dist(testX, testY, ix2, iy2) < dist(x1, y1, x2, y2)) {
				return true;
			} else {
				return false;
			}
		}
	}

	public double distanceBetweenTwoPoints(Point point1, Point point2) {
		double distance = 0;
		distance = Math.sqrt(((point1.x-point2.x)*(point1.x-point2.x))+((point1.y-point2.y)*(point1.y-point2.y)));
		return distance;
	}
	public float dist(float x1, float y1, float x2, float y2) {
		float distance = 0;
		distance = (float) Math.sqrt(((x1-x2)*(x1-x2))+((y1-y2)*(y1-y2)));
		return distance;
	}
	public void render() {
		
		long c = System.currentTimeMillis()-timeOfCreation;
		
		if(c<LIFETIME) {
			
			GL11.glLineWidth(1);
			GL11.glColor4d(0.8,0.8,0.8,(double) 0.8-(c/LIFETIME));
			if(c<50) {
				GL11.glLineWidth(1);
				GL11.glColor4f(1f,1f,1f,1f);
			}
			

			GL11.glBegin(GL11.GL_LINES);
				GL11.glVertex2d(start_x, start_y);  
				GL11.glVertex2d(end_x, end_y);
			GL11.glEnd();
		}
		GL11.glColor4f(1f,1f,1f,1f);
	}

}

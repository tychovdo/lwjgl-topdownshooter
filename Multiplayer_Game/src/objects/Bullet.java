package objects;



import java.awt.Point;

import org.lwjgl.opengl.GL11;

public class Bullet {
	public double start_x;
	public double start_y;
	public double end_x;
	public double end_y;
	public long timeOfCreation = System.currentTimeMillis();
	
	public final double LIFETIME = 300; //TODO: reset LIFETIME when client receives enemy bullet, or only send coordinates in server communication
	public boolean isChecked = false;
	//TODO log owner of the bullet. (if the user shoots there is a clientside and a serverside bullet: eliminate serverside bullets of own player)
	
	public Bullet() {

	}
	
	public Bullet(double start_x, double start_y, double end_x, double end_y) {
		this.start_x = start_x;
		this.start_y = start_y;
		this.end_x = end_x;
		this.end_y = end_y;
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

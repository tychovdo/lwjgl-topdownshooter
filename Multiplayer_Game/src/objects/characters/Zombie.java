package objects.characters;


public class Zombie extends Character { // TODO: make a character class so Zombie and Player can share code.
	
	public Zombie() {

	}
	public Zombie(int x, int y) {
		this.x = x;
		this.y = y;
		texture_num = 3;
	}
	public void update(Player[] players) {
		
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
		double speed = 2;
		double rotation_rads = Math.toRadians(rotation);
		x = x+(speed*Math.cos(rotation_rads));
		y = y+(speed*Math.sin(rotation_rads));
		

	}

}

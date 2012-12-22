package main.systems;

import java.awt.Font;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;

@SuppressWarnings("deprecation")
public class Overlay {
	TrueTypeFont font;
	
	public String text;
	private boolean displayText=false;;
	
	public void enable(String string) {
		text = string;
		displayText=true;
	}
	public void disable() {
		displayText=false;
	}
	public Overlay() {
		Font awtFont = new Font("Arial", Font.BOLD, 24);
		font = new TrueTypeFont(awtFont, true);
	}

	public void render() {
		if(displayText) {
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			GL11.glOrtho(0, 960, 512, 0, 1, -1);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			
			font.drawString(100, 50, text, Color.white);
			
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			GL11.glOrtho(0, 960, 0, 512, 1, -1);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
		}
		
		
	}
}

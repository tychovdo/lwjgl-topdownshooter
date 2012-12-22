package objects.characters;


import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.*;
import org.newdawn.slick.opengl.Texture;

public class Character {
	public double x;
	public double y;
	public float rotation = 10f;
	
	public int width = 64;
	public int height = 32;
	public int texture_num;
	
	public int health = 100;
	
	public void teleportTo(Character goal) { //TODO: temp
		if(goal!=null) {
			x = goal.x;
			y = goal.y;
		}
	}
	public void rotateTo(double rotateDest_x, double rotateDest_y) {

		rotation = (float) ((float) Math.atan2(rotateDest_y - y,rotateDest_x -x)* 180 / Math.PI);
		if(rotation<0) {
			rotation+=360;
		}
	}
	public void render(Texture[] texture_player, int shaderProgram) {

		// Draw player with rotation


		//glUseProgram(shaderProgram);
	    int loc = glGetUniformLocation(shaderProgram, "texture1");
	    glUniform1i(loc, 0);
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D,texture_player[texture_num].getTextureID());
		
		glPushMatrix();
			glTranslated((x), (y), 0);
			glRotatef(rotation, 0f, 0f, 1f);
			glTranslated(-(x), -(y), 0);
			glBegin(GL_QUADS);
				glTexCoord2f(0, 1);
				glVertex2d(x-14,y-16); // Bottom-left
				glTexCoord2f(1, 1);
				glVertex2d((x+width)-14, y-16); // Bottom-right
				glTexCoord2f(1, 0);
				 glVertex2d((x+width)-14,(y+height)-16); // Upper-right
				glTexCoord2f(0, 0);
				glVertex2d(x-14, (y+height)-16); // Upper-left
			glEnd();
		glPopMatrix();
		
		//glUseProgram(0);
		
		glActiveTexture(GL_TEXTURE0);
		glDisable(GL_TEXTURE_2D);
		glColor3f(1f,0f,0f);
		glBegin(GL_QUADS);
			glVertex2d(x-16,y+25); // Bottom-left
			glVertex2d(x+(health/3)-16, y+25); // Bottom-right
			glVertex2d(x+(health/3)-16,y+5+25); // Upper-right
			glVertex2d(x-16, y+5+25); // Upper-left
		glEnd();
		glEnable(GL_TEXTURE_2D);
		glColor3f(1f,1f,1f);
	}

	public void rendershadow(Texture[] texture_player) {
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D,texture_player[texture_num].getTextureID());
		
		glPushMatrix();
			glColor4f(0f,0f,0f,0.6f);
			glTranslated(x+5, y-5, 0);
			glRotatef(rotation, 0f, 0f, 1f);
			glTranslated(-x, -y, 0);
			
			glBegin(GL_QUADS);
				glTexCoord2f(0, 1);
				glVertex2d(x-14,y-16); // Bottom-left
				glTexCoord2f(1, 1);
				glVertex2d((x+width)-14, y-16); // Bottom-right
				glTexCoord2f(1, 0);
				 glVertex2d((x+width)-14,(y+height)-16); // Upper-right
				glTexCoord2f(0, 0);
				glVertex2d(x-14, (y+height)-16); // Upper-left
			glEnd();
		glPopMatrix();
		glColor3f(1f,1f,1f);
	}
}

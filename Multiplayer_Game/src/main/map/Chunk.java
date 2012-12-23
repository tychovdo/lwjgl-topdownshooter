package main.map;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;

public class Chunk {
	
	public int chunkX;
	public int chunkY;
	public int tileTypes[][] = new int[32][32];
	
	public List<Rectangle> solids = new ArrayList<Rectangle>();
	
	
	
	public Chunk(int chunkX, int chunkY) {
		this.chunkX = chunkX;
		this.chunkY = chunkY;
		
		double baseX = chunkX*(64*8); // TODO: deprecated
		double baseY = chunkY*(64*8);
		for(int y1=2;y1<8;y1++) {
			for(int x1=2;x1<8;x1++) {
				if(x1==3||y1==3) {
					tileTypes[x1][y1] = 1;

					double x = baseX+(x1*64);
					double y = baseY+(y1*64);
					solids.add(new Rectangle((int)x,(int)y,64,64));
				}
			}
		}
	}
	public int getTileType(int tileX, int tileY) {
		return tileTypes[tileX][tileY];
	}
	public void setTileType(int tileX, int tileY, int tileType) {
		tileTypes[tileX][tileY] = tileType;
	}
	public void render(Texture texture) {
		double baseX = chunkX*(64*8);
		double baseY = chunkY*(64*8);
		texture.bind();

		for(int y1=0;y1<8;y1++) {
			for(int x1=0;x1<8;x1++) {
				if(tileTypes[x1][y1]!=0) {
					double x = baseX+(x1*64);
					double y = baseY+(y1*64);
					GL11.glBegin(GL11.GL_QUADS);
						GL11.glTexCoord2f(0, 1);
						GL11.glVertex2d(x-32,y-32); // Bottom-left
						GL11.glTexCoord2f(1, 1);
						GL11.glVertex2d(x+96, y-32); // Bottom-right
						GL11.glTexCoord2f(1, 0);
						GL11. glVertex2d(x+96,y+96); // Upper-right
						GL11.glTexCoord2f(0, 0);
						GL11.glVertex2d(x-32, y+96); // Upper-left
					GL11.glEnd();
				}
			}
		}
	}

}

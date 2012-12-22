package main.map;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import objects.characters.Player;

import org.newdawn.slick.opengl.Texture;

public class Map {
	public List<Chunk> chunks = new ArrayList<Chunk>();
	public int player_chunkX;
	public int player_chunkY;
	
	public Map() {
		// load chunks array;
	}
	public void update(Player player) {
		player_chunkX = getChunkFromTile((int) player.x);
		player_chunkX = getChunkFromTile((int) player.y);
		
	}
	public int getTileType(int x, int y) {
		int tileType = 0;
		
		int chunkX = getChunkFromTile(x);
		int chunkY = getChunkFromTile(y);
		int tileX=0;
		int tileY=0;
		if(x>0) {
			tileX=x%8;
		}
		if(x<0) {
			x=8-x;
			tileX=x%8;
		}
		if(y>0) {
			tileY=y%8;
		}
		if(y<0) {
			y=8-y;
			tileY=y%8;
		}
		
		Chunk chunk = getChunk(chunkX,chunkY);
		tileType = chunk.getTileType(tileX,tileY);

		return tileType;
		
	
	}
	public Chunk getChunk(int chunkX, int chunkY) {
		Chunk output = null;
		boolean foundChunk = false;
		for(Chunk chunk : chunks) {
			if((chunk.chunkX==chunkX) && (chunk.chunkY==chunkY)) {
				output = chunk;
				foundChunk = true;
				break;
			}		}
		if(!foundChunk) {
			output = createChunk(chunkX, chunkY);
		}
		return output;
	}
	private Chunk createChunk(int chunkX, int chunkY) {
		Chunk chunk = new Chunk(chunkX, chunkY);
		chunks.add(chunk);
		return chunk;
	}
	private static int getChunkFromTile(int coordinate) { // coordinate is x or y;
		int output = coordinate;
		if(output>=0) {
			output = output / (64*8);
		} else {
			output = output / (64*8);
			output = output -1;
		}
		return output;
	}
	public List<Rectangle> getSolids() {
		List<Rectangle> list = new ArrayList<Rectangle>();
		for(int y1=-1;y1<2;y1++) {
			for(int x1=-1;x1<2;x1++) {
				list.addAll(getChunk(player_chunkX+x1,player_chunkY+y1).solids);
			}
		}
		return list;
	}
	public void render(int chunkX, int chunkY, Texture texture) {
		for(int y1=-1;y1<2;y1++) {
			for(int x1=-1;x1<2;x1++) {
				getChunk(chunkX+x1,chunkY+y1).render(texture);
			}
		}
	}
}

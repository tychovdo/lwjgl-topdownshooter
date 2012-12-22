package main.systems;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

public class TexturesLoader {
	public static Texture loadTexture(String filename) {
    	//return texture, in res/ folder, from filename
        try {
        	//return TextureLoader.getTexture("PNG", new FileInputStream(new File("res/" + filename)));
        	//texture = InternalTextureLoader.get().getTexture(data, filter == FILTER_LINEAR ? SGL.GL_LINEAR : SGL.GL_NEAREST);
        	return TextureLoader.getTexture("PNG", new FileInputStream(new File("res/" + filename)), GL11.GL_NEAREST);
        } catch (FileNotFoundException e) {
                e.printStackTrace();
                System.exit(0);
        } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
        }
        return null;
    }
}

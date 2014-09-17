import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;


public class Image {
	Texture texture;
	
	Image (String format, String path){
		try {
			// load texture from PNG file
			texture=TextureLoader.getTexture(format, ResourceLoader.getResourceAsStream(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void changeImage (String format, String path){
		try {
			// load texture from PNG file
			texture=TextureLoader.getTexture(format, ResourceLoader.getResourceAsStream(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void render(int posX,int posY){
		Color.white.bind();
		texture.bind(); // or GL11.glBind(texture.getTextureID());
		
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(0,0);
			GL11.glVertex2f(posX,posY);
			
			GL11.glTexCoord2f(1,0);
			GL11.glVertex2f(posX+texture.getTextureWidth(),posY);
			
			GL11.glTexCoord2f(1,1);
			GL11.glVertex2f(posX+texture.getTextureWidth(),posY+texture.getTextureHeight());
			
			GL11.glTexCoord2f(0,1);
			GL11.glVertex2f(posX,posY+texture.getTextureHeight());
		GL11.glEnd();
	}
}

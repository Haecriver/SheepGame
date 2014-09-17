import java.io.IOException;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.util.ResourceLoader;


public class Game {
	
	final int width=800;
	final int height = 600;
	private int nbMoutons=5;
	private Mouton mouton[];
	private Audio music;
	//private boolean paused;
	
	private void initMouton(){
		mouton = new Mouton[nbMoutons];
		for(int i=0;i<nbMoutons;i++){
			mouton[i]=new Mouton(width,height);
		}
	}
	
	private void initMusic(){
		try {
			music =AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("res/music.wav"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void playMusic(){
		music.playAsSoundEffect(1.0f, 0.5f, true);
	}
	
	public void start() {
		initGL(width,height);
		initMusic();
		Fond fond = new Fond();
		initMouton();
		
		playMusic();
		
		while (!Display.isCloseRequested()) {
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			
			fond.render();
			for(int i=0;i<nbMoutons;i++){
				mouton[i].render();
			}
			Display.update();
		}
		
		Display.destroy();
	}
	
	private void initGL(int width, int height) {
		try {
			Display.setDisplayMode(new DisplayMode(width,height));
			Display.create();
			Display.setVSyncEnabled(true);
			Display.setTitle("Mouton");
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}

		GL11.glEnable(GL11.GL_TEXTURE_2D);               
        
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);          
        
    	// enable alpha blending
    	GL11.glEnable(GL11.GL_BLEND);
    	GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    
    	GL11.glViewport(0,0,width,height);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, width, height, 0, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
	}
	

	public static void main(String[] argv) {
		Game displayExample = new Game();
		displayExample.start();
	}
}

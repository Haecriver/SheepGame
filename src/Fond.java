
public class Fond extends Image {
	Fond (){
		super("JPG","res/fond.jpg");
	}
	
	public int getImageWidth(){
		System.out.println(texture.getImageWidth());
		return texture.getImageWidth();
	}
	
	public int getImageHeight(){
		System.out.println(texture.getImageHeight());
		return texture.getImageHeight();
	}
	
	public void render(){
		super.render(0,0);
	}
}

import java.io.IOException;
import java.util.Random;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.openal.SoundStore;
import org.newdawn.slick.util.ResourceLoader;


public class Mouton extends Image {
	//états
	private boolean debug=false;
	
	private enum Etat {BELE,BROUTE,DEPLACE,MORT,ATTEND};
	private Etat etat;
	
	private enum PhaseMort {P0,P1,P2,P3,P4,P5,DEAD};
	private PhaseMort phaseMort;
	
	private int vitesse;
	
	private int posX;
	private int posY;
	private int futurPosX;
	private int futurPosY;
	private double bond;
	
	private long brouteTime;
	private long brouteTimeDeb;
	private long brouteCpt;
	
	private Random random;
	private int randomInt;
	
	private int fondWidth;
	private int fondHeight;
	
	private Audio belement;
	private long beleTimeDeb;
	private int beleDuree;
	
	private Audio mort;
	private long debMort;
	private int pointDeVie;
	
	private Audio coup;
	
	private boolean moutonClicked;
	
	Mouton (int fondWidth,int fondHeight){
		super("PNG","res/mouton_droite_fermee.png");
		
		random=new Random();
		tirageRandom();
		
		try {
			belement=AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("res/belement.wav"));
			coup=AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("res/death/coup.wav"));
			if(Math.abs(random.nextInt())%10==1){
				mort=AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("res/death/wilhelm.wav"));
			}else{
				mort=AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("res/death/mort.wav"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		pointDeVie=15;
		moutonClicked=false;
		
		this.fondWidth=fondWidth;
		this.fondHeight=fondHeight;
		
		vitesse=Math.abs(randomInt)%5;
		posX=(Math.abs(random.nextInt()))%(fondWidth-texture.getImageWidth());
		posY= (Math.abs(random.nextInt()))%(fondHeight-texture.getImageHeight());
		bond=0;
		
		etat=Etat.ATTEND;
		phaseMort=PhaseMort.P0;
	}
	
	private void tirageRandom(){
		randomInt=(Math.abs(random.nextInt()))%100;
	}
	
	private long getTime() {
	    return System.nanoTime() / 1000000;
	}
	
	//Gestion du mouton
	public void render(){
		
		if (testMouse()&&etat!=Etat.MORT){
			ajoutPDV(-1);
			coup.playAsSoundEffect(1.0f, 0.5f, false);
		}
		
		switch (etat){
			case DEPLACE :
				if (debug){
					System.out.println("Deplacement");
					System.out.println("Src = "+posX+" "+posY+" Dest = "+futurPosX+" "+futurPosY);
				}
				bond=(Math.sin(Math.abs((posX)/(2*vitesse)))+1)*8;
				deplacer();
			break;
			
			case BROUTE :
				if (debug){
					System.out.println("Broute");
					System.out.println("Temps Restant =");
					System.out.println(getTime()-brouteTimeDeb-brouteTime);
				}
				if (brouteCpt%10==0){
					if (futurPosX>posX){
						changeImage("PNG","res/mouton_droite_broute1.png");
					}
					else{
						changeImage("PNG","res/mouton_gauche_broute1.png");
					}
				}else if (brouteCpt%10==5){
					if (futurPosX>posX){
						changeImage("PNG","res/mouton_droite_broute2.png");
					}
					else{
						changeImage("PNG","res/mouton_gauche_broute2.png");
					}
				}
				
				brouteCpt++;
				if (getTime()-brouteTimeDeb>brouteTime){
					etat=Etat.ATTEND;
				}
			break;
			
			case BELE :
				if (debug){
					System.out.println("Bele");
					System.out.println("Temps Restant =");
					System.out.println(getTime()-beleTimeDeb-beleDuree);
				}
				
				if (getTime()-beleTimeDeb>beleDuree){
					etat=Etat.ATTEND;
				}
			break;
			
			
			case MORT:
				mourir();
			break;
			
			default :
				tirageRandom();
				if (randomInt<33){//deplacement
					futurPosX= (Math.abs(random.nextInt()))%(fondWidth-texture.getImageWidth());
					futurPosY= (Math.abs(random.nextInt()))%(fondHeight-texture.getImageHeight());
					vitesse=(Math.abs(randomInt)%5)+1;
					
					if (futurPosX>posX){
						changeImage("PNG","res/mouton_droite_fermee.png");
					}
					else{
						changeImage("PNG","res/mouton_gauche_fermee.png");
					}
					
					etat=Etat.DEPLACE;
				}
				else if (randomInt<66){//broutage
					brouteTime=Math.abs(random.nextInt())%10000;
					brouteTimeDeb=getTime();
					brouteCpt=0;
					
					etat=Etat.BROUTE;
				}
				else {//belage
					beleDuree=1000;
					beler();
					etat=Etat.BELE;
				}
			break;
			
		}
		super.render(posX,posY+(int)bond);
	}
	
	private boolean testMouse(){
		//Le mouton fait 128x128 donc
		//mouseX doit se trouver entre posX et posX+128
		//mouseY doit se trouver entre posY+bond et posY+128+bond
		float mouseX=Mouse.getX();
		float mouseY=fondHeight-Mouse.getY();
		boolean leftButtonDown = Mouse.isButtonDown(0);
		boolean rightButtonDown = Mouse.isButtonDown(1);
		
		boolean mousePressedOnMouton=((leftButtonDown||rightButtonDown)&&(mouseX>posX&&mouseX<(posX+128))&&(mouseY>(posY+(int)bond)&&mouseY<(posY+128+(int)bond)));
		boolean res=!moutonClicked&&mousePressedOnMouton;
		moutonClicked=mousePressedOnMouton;
		if (!debug){
			System.out.println("Pos Mouse "+mouseX+" "+mouseY);
			System.out.println("Entre "+posX+" "+(posX+128)+" et "+(posY+(int)bond)+" "+(posY+128+(int)bond));
		}
		return res;
	}

	private void ajoutPDV(int valPV){
		pointDeVie+=valPV;
		//domage
		if (etat!=Etat.MORT&&valPV<0){
			//Belement avec une autre image
			if (futurPosX>posX){
				changeImage("PNG","res/death/mouton_droite_hurt.png");
			}
			else{
				changeImage("PNG","res/death/mouton_gauche_hurt.png");
			}
			beleTimeDeb=getTime();
			belement.playAsSoundEffect(1.0f, 1.0f, false);
			SoundStore.get().poll(0);
			etat=Etat.BELE;
			beleDuree=400;
		}
		if (pointDeVie<=0){
			etat=Etat.MORT;
		}
	}
	
	public void deplacer(){
		if ((vitesse>=Math.abs(futurPosX-posX))&&((vitesse>=Math.abs(futurPosY-posY)))){
			etat=Etat.ATTEND;
		}
		if (Math.abs(futurPosX-posX)>Math.abs(futurPosY-posY)){
			if (futurPosX>posX){
				posX+=vitesse;
			}
			else{
				posX-=vitesse;
			}
		}else{
			if (futurPosY>posY){
				posY+=vitesse;
			}
			else{
				posY-=vitesse;
			}
		}
		
	}
	
	public void beler(){
		if (futurPosX>posX){
			changeImage("PNG","res/mouton_droite_ouverte.png");
		}
		else{
			changeImage("PNG","res/mouton_gauche_ouverte.png");
		}
		beleTimeDeb=getTime();
		belement.playAsSoundEffect(1.0f, 1.0f, false);
		SoundStore.get().poll(0);
	}
	
	public void mourir(){
		switch (phaseMort){
		case P0 :
			debMort=getTime();
			if (futurPosX>posX){
				changeImage("PNG","res/death/mouton_droite_hurt.png");
			}
			else{
				changeImage("PNG","res/death/mouton_gauche_hurt.png");
			}
			belement.stop();
			mort.playAsSoundEffect(1.0f, 1.0f, false);
			phaseMort = PhaseMort.P1;
		break;
		
		case P1 :
			if((getTime()-debMort)>200){
				if (futurPosX>posX){
					changeImage("PNG","res/death/mouton_droite_mort1.png");
				}
				else{
					changeImage("PNG","res/death/mouton_gauche_mort1.png");
				}
				phaseMort = PhaseMort.P2;
			}
		break;
		
		case P2 :
			if((getTime()-debMort)>400){
				if (futurPosX>posX){
					changeImage("PNG","res/death/mouton_droite_mort2.png");
				}
				else{
					changeImage("PNG","res/death/mouton_gauche_mort2.png");
				}
				phaseMort = PhaseMort.P3;
			}
		break;
		
		case P3 :
			if((getTime()-debMort)>600){
				if (futurPosX>posX){
					changeImage("PNG","res/death/mouton_droite_mort3.png");
				}
				else{
					changeImage("PNG","res/death/mouton_gauche_mort3.png");
				}
				phaseMort = PhaseMort.P4;
			}
		break;
		
		case P4 :
			if((getTime()-debMort)>800){
				if (futurPosX>posX){
					changeImage("PNG","res/death/mouton_droite_mort4.png");
				}
				else{
					changeImage("PNG","res/death/mouton_gauche_mort4.png");
				}
				phaseMort = PhaseMort.P5;
			}
		break;
		
		case P5 :
			if((getTime()-debMort)>1000){
				if (futurPosX>posX){
					changeImage("PNG","res/death/mouton_droite_mort5.png");
				}
				else{
					changeImage("PNG","res/death/mouton_gauche_mort5.png");
				}
				phaseMort = PhaseMort.DEAD;
			}
		break;
		
		case DEAD:
		break;
		
		default:
		break;
	}
	}
	
	public String toString(){
		return etat.toString();
	}
}



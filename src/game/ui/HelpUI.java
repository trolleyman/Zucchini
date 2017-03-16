package game.ui;

import java.util.ArrayList;
import game.InputHandler;
import game.InputPipeMulti;
import game.audio.AudioManager;
import game.net.client.IClientConnection;
import game.render.Align;
import game.render.Font;
import game.render.FontBank;
import game.render.IRenderer;
import game.render.TextureBank;
import game.ui.component.ButtonComponent;
import game.ui.component.ImageComponent;
import game.world.ClientWorld;

public class HelpUI extends UI implements InputPipeMulti{
	private UI nextUI;
	private ArrayList<InputHandler> inputHandlers = new ArrayList<>();
	private TextureBank bank;
	private ButtonComponent backBtn, nextBtn;

	private float winWidth;
	private float winHeight;
	private ImageComponent backgroundImage;
	private ImageComponent logInScreen, lobbyScreen, lobbyWaitScreen;
	private Boolean boolLIS, boolLS, boolLWS;
	private String[] strLIS, strLS, strLWS;
	private int imageCount;
	private Font font;
	
	public HelpUI(UI ui){
		super(ui);
		nextUI = this;
		font = fontBank.getFont("emulogic.ttf");
		imageCount = 0;
		boolLIS = true;
		boolLS = false;
		boolLWS = false;
		start();
	}
	
	private void start(){
		backBtn = new ButtonComponent(
				() -> System.out.println("Clicked back button"),
				Align.TL, 0, 0,
				textureBank.getTexture("temparrow.png"),
				textureBank.getTexture("temparrow.png"),
				textureBank.getTexture("temparrow.png")
		);
		
		nextBtn = new ButtonComponent(
				() -> this.nextPicture(),
				Align.BL, 100, 100,
				textureBank.getTexture("nextDefault.png"),
				textureBank.getTexture("nextHover.png"),
				textureBank.getTexture("nextPressed.png")
			);
			
		
		backgroundImage = new ImageComponent(
				Align.BL, 0, 0, textureBank.getTexture("Start_BG.png"), 0.0f
		);
		
		logInScreen = new ImageComponent(
				Align.BL, 100, 100, textureBank.getTexture("LogInScreen.png"), 0.0f
		);
		
		lobbyScreen = new ImageComponent(
				Align.BL, 100, 100, textureBank.getTexture("LobbyScreen.png"), 0.0f
		);
		
		lobbyWaitScreen = new ImageComponent(
				Align.BL, 100, 100, textureBank.getTexture("LobbyWaitScreen.png"), 0.0f
		);
		
		setUpText();
		
		
		this.inputHandlers.add(backBtn);
		this.inputHandlers.add(nextBtn);
	}
	
	private void setUpText(){
		strLIS = new String[10];
		strLIS[1] = "When logging into the game,";
		strLIS[2] = "enter your username into";
		strLIS[3] = "the 'NAME' text entry box.";
		strLIS[4] = "";
		strLIS[5] = "To connect automatically,";
		strLIS[6] = "click 'AUTO'. To connect manually,";
		strLIS[7] = "enter the ip adress into the ";
		strLIS[8] = "'ADDRESS' entry box and click"; 
		strLIS[9] = "'CONNECT'.";
				
		strLS = new String[9];
		strLS[1] = "At the Lobby screen, click";
		strLS[2] = "on the lobby that you wish";
		strLS[3] = "to join and click 'JOIN'.";
		strLS[4] = "";
		strLS[5] = "The refresh button will refresh"; 
		strLS[6] = "which lobbies are available. The";
		strLS[7] = "'CREATE' button will allow you";
		strLS[8] = "to create a new lobby.";
				
		strLWS = new String[9];
		strLWS[1] = "Here, you have joined a lobby and";
		strLWS[2] = "can wait for the other three players";
		strLWS[3] = "to join before clicking 'READY'.";
		strLWS[4] = "";
		strLWS[5] = "When all players are ready the";
		strLWS[6] = "countdown will start and the game will";
		strLWS[7] = "begin. You can leave the lobby before";
		strLWS[8] = "the game starts by clicking on 'LEAVE'."; 
	}
	
	public ArrayList<InputHandler> getHandlers() {
		return this.inputHandlers;
	}

	@Override
	public void handleResize(int w, int h) {
		this.winWidth = w;
		this.winHeight = h;
		InputPipeMulti.super.handleResize(w, h);
	}
	
	@Override
	public void update(double dt) {
		backBtn.update(dt);
		nextBtn.update(dt);
		
	}

	
	public void nextPicture(){
		imageCount++;
		if(imageCount == 1){
			boolLIS = false;
			boolLS = true;
		}else if(imageCount == 2){
			boolLS = false;
			boolLWS = true;
		}else if(imageCount == 3){
			boolLWS = false;
		}
	}
	
	
	@Override
	public void render(IRenderer r) {
		backgroundImage.render(r);
		
		nextBtn.setX((int) winWidth - 100);
		nextBtn.setY((int) 100);
		
		r.drawText(font, "HOW TO SET UP A GAME", Align.TL, false, winWidth/3, winHeight, 1.0f);
		
		if(boolLIS){
			logInScreen.setX(winWidth/9);
			logInScreen.setY(winHeight/3);
			logInScreen.render2(r, 1.8f);
			
			for(int i = 1; i < strLIS.length; i++){
				r.drawText(font, strLIS[i], Align.BL, false, winWidth - 700, winHeight - 300 - (50*i), 0.4f);
			}
		}else if(boolLS){
			lobbyScreen.setX(winWidth/9);
			lobbyScreen.setY(winHeight/3);
			lobbyScreen.render2(r, 1.8f);
			
			for(int i = 1; i < strLS.length; i++){
				r.drawText(font, strLS[i], Align.BL, false, winWidth - 700, winHeight - 300 - (50*i), 0.4f);
			}
		}else if(boolLWS){
			lobbyWaitScreen.setX(winWidth/9);
			lobbyWaitScreen.setY(winHeight/3);
			lobbyWaitScreen.render2(r, 1.8f);
			
			for(int i = 1; i < strLWS.length; i++){
				r.drawText(font, strLWS[i], Align.BL, false, winWidth - 700, winHeight - 300 - (50*i), 0.4f);
			}
		}
		
		nextBtn.render(r);
	//	backBtn.setX(0);
	//	backBtn.setY((int) winHeight - 200);
	//	backBtn.render(r);
	}

	@Override
	public UI next() {
		return nextUI;
	}

	@Override
	public void destroy() {
		//Nothing to destroy
	}

	@Override
	public String toString() {
		return "HelpUI";
	}
}

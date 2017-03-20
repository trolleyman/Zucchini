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
	private final UI afterUI;
	private ArrayList<InputHandler> inputHandlers = new ArrayList<>();
	private ButtonComponent backBtn, nextBtn, exitBtn;

	private float winWidth;
	private float winHeight;
	private ImageComponent backgroundImage;
	private ImageComponent logInScreen, lobbyScreen, lobbyWaitScreen, gameScreen, escapeScreen;
	private String[] strLIS, strLS, strLWS, strGS, strES, strWS;
	private int imageCount;
	private Font font;
	
	/**
	 * Constructs a new HelpUI
	 * @param afterUI The UI that will be the next UI after this one
	 */
	public HelpUI(UI ui, UI afterUI){
		super(ui);
		nextUI = this;
		this.afterUI = afterUI;
		font = fontBank.getFont("emulogic.ttf");
		imageCount = 0;
	
		start();
	}
	
	private void start(){
		backBtn = new ButtonComponent(
				() -> imageCount--,
				Align.TL, 0, 0,
				textureBank.getTexture("backDefault.png"),
				textureBank.getTexture("backHover.png"),
				textureBank.getTexture("backPressed.png")
		);
		
		nextBtn = new ButtonComponent(
				() -> this.imageCount++,
				Align.BL, 100, 100,
				textureBank.getTexture("nextDefault.png"),
				textureBank.getTexture("nextHover.png"),
				textureBank.getTexture("nextPressed.png")
		);
			
		exitBtn = new ButtonComponent(
				() -> this.nextUI = afterUI,
				Align.BL, 100, 100,
				textureBank.getTexture("exitButtonDefault.png"),
				textureBank.getTexture("exitButtonHover.png"),
				textureBank.getTexture("exitButtonPressed.png")
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
		
		gameScreen = new ImageComponent(
				Align.BL, 100, 100, textureBank.getTexture("GameScreen.png"), 0.0f
		);
		
		escapeScreen = new ImageComponent(
				Align.BL, 100, 100, textureBank.getTexture("EscapeScreen.png"), 0.0f
		);
		setUpText();
		
		
		this.inputHandlers.add(backBtn);
		this.inputHandlers.add(nextBtn);
		this.inputHandlers.add(exitBtn);
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
		
		strGS = new String[30];
		strGS[1] = "This is the screen during game play.";
		strGS[2] = "You are the icon at the centre";
		strGS[3] = "of the screen.In the top right of the";
		strGS[4] = "screen is your health bar. Green represents";
		strGS[5] = "how much life you have left. Red shows";
		strGS[6] = "what you have lost.";
		strGS[7] = "";
		strGS[8] = "In the bottom right of the screen shows the";
		strGS[9] = "item you are curently holding. If you are";
		strGS[10] = "holding a gun it will show how much";
		strGS[11] = "ammo you have.";
		strGS[12] = "";
		strGS[13] = "In the top left is your minimap. This shows";
		strGS[14] = "the layout of the maze in your area.";
		strGS[15] = "To move your player around the area, use:";
		strGS[16] = "The W key to move North,";
		strGS[17] = "The A key to move West,";
		strGS[18] = "The S key to move South,";
		strGS[19] = "The D key to move East.";
		strGS[20] = "";
		strGS[21] = "Use your mouse or touchpad to control the";
		strGS[22] = "direction and aim of your player.";
		strGS[23] = "Click your mouse to fire or use your weapon.";
		strGS[24] = "";
		strGS[25] = "To pick up other items, press the E key.";
		strGS[26] = "This will also cause you to drop the item";
		strGS[27] = "you are currently holding.";
		strGS[28] = "";
		strGS[29] = "Press the ESCAPE key to see the menu.";

		strES = new String[14];
		strES[1] = "This is the escape menu, which you can";
		strES[2] = "access at any time during the game.";
		strES[3] = "The game is still live during your time";
		strES[4] = "viewing the menu. By pressing CONTINUE,";
		strES[5] = "you will go back into the same game.";
		strES[6] = "";
		strES[7] = "From here, you can QUIT the game back to";
		strES[8] = "the start screen. If you need to look at";
		strES[9] = "this HELP menu again by pressing the";
		strES[10] = "button, you will automatically quit";
		strES[11] = "the game.";
		strES[12] = "";
		strES[13] = "Click AUDIO to change any sound features.";

		strWS = new String[13];
		strWS[1] = "The aim of the game is to be the";
		strWS[2] = "last one alive!";
		strWS[3] = "The arena is a maze with a central area";
		strWS[4] = "throughout which are weapons for you to";
		strWS[5] = "pick up.";
		strWS[6] = "";
		strWS[7] = "As time in the game goes on, the maze";
		strWS[8] = "will fill with flesh-eating zombies!";
		strWS[9] = "Nowhere is safe...except the centre.";
		strWS[10] = "Do what you can to keep yourself alive";
		strWS[11] = "from the zombies and other players who";
		strWS[12] = "are out to get you. Good luck!";
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
		exitBtn.update(dt);
	}
	
	@Override
	public void render(IRenderer r) {
		backgroundImage.render(r);
		backBtn.setX(50);
		backBtn.setY(200);
		nextBtn.setX(winWidth - 150);
		nextBtn.setY(100);
		exitBtn.setX(winWidth - 150);
		exitBtn.setY(winHeight - 150);
		
		if(imageCount<0){
			nextUI = afterUI;
		}
		
		if(imageCount<3){
			r.drawText(font, "HOW TO SET UP A GAME", Align.TL, false, winWidth/3, winHeight, 1.0f);
		}
		
		if(imageCount==0){
			logInScreen.setX(winWidth/15);
			logInScreen.setY(winHeight/3);
			logInScreen.render2(r, 1.8f);
			
			for(int i = 1; i < strLIS.length; i++){
				r.drawText(font, strLIS[i], Align.BL, false, winWidth - 900, winHeight - 200 - (50*i), 0.4f);
			}
		}else if(imageCount==1){
			lobbyScreen.setX(winWidth/15);
			lobbyScreen.setY(winHeight/3);
			lobbyScreen.render2(r, 1.8f);
			
			for(int i = 1; i < strLS.length; i++){
				r.drawText(font, strLS[i], Align.BL, false, winWidth - 900, winHeight - 200 - (50*i), 0.4f);
			}
		}else if(imageCount==2){
			lobbyWaitScreen.setX(winWidth/15);
			lobbyWaitScreen.setY(winHeight/3);
			lobbyWaitScreen.render2(r, 1.8f);
			
			for(int i = 1; i < strLWS.length; i++){
				r.drawText(font, strLWS[i], Align.BL, false, winWidth - 900, winHeight - 200 - (50*i), 0.4f);
			}
		}
		
		if(imageCount>2 && imageCount<6){
			r.drawText(font, "HOW TO PLAY THE GAME", Align.TL, false, winWidth/3, winHeight, 1.0f);
		}
		
		if(imageCount==3){
			gameScreen.setX(winWidth/15);
			gameScreen.setY(winHeight/3);
			gameScreen.render2(r, 1.8f);
			
			for(int i = 1; i < 15; i++){
				r.drawText(font, strGS[i], Align.BL, false, winWidth - 900, winHeight - 200 - (50*(i)), 0.4f);
			}
		}else if(imageCount==4){
			gameScreen.setX(winWidth/15);
			gameScreen.setY(winHeight/3);
			gameScreen.render2(r, 1.8f);
			
			for(int i = 15; i < strGS.length; i++){
				r.drawText(font, strGS[i], Align.BL, false, winWidth - 900, winHeight - 200 - (50*(i-15)), 0.4f);
			}
		}else if(imageCount==5){
			escapeScreen.setX(winWidth/15);
			escapeScreen.setY(winHeight/3);
			escapeScreen.render2(r, 1.8f);
			
			for(int i = 1; i < strES.length; i++){
				r.drawText(font, strES[i], Align.BL, false, winWidth - 900, winHeight - 200 - (50*i), 0.4f);
			}
		}
		
		if(imageCount>5){
			r.drawText(font, "HOW TO WIN THE GAME", Align.TL, false, winWidth/3, winHeight, 1.0f);
		}
		
		if(imageCount==6){
			for(int i = 1; i < strWS.length; i++){
				r.drawText(font, strWS[i], Align.BL, false, 300, winHeight - 150 - (50*i), 0.8f);
			}
		}
		
		if(imageCount==7){
			this.nextUI = afterUI;
		}
		
		exitBtn.render(r);
		nextBtn.render(r);
		backBtn.render(r, 350.0f, 100.0f);
	}
	
	@Override
	public UI next() {
		return nextUI;
	}

	@Override
	public void destroy() {
		//Nothing to destroy
	}
}

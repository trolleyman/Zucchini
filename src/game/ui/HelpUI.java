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
	private String strLIS, strLS, strLWS;
	private int imageCount;
	private Font font;
	
	public HelpUI(UI ui){
		super(ui);
		start();
	}
	
	public HelpUI(IClientConnection _conn, AudioManager audio, TextureBank _bank, FontBank _fb) {
		super(_conn, audio, _bank, _fb);
	//	this.bank = _bank;
		nextUI = this;
		font = fontBank.getFont("emulogic.ttf");
		imageCount = 0;
		boolLIS = false;
		boolLS = false;
		boolLWS = false;
		start();
	}
	
	private void start(){
		backBtn = new ButtonComponent(null, Align.TL, 0, 0,
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
				Align.BL, 100, 100, textureBank.getTexture("LobbyWait.png"), 0.0f
		);
		
		setUpText();
		
		
		this.inputHandlers.add(backBtn);
	}
	
	private void setUpText(){
		strLIS = "When logging into the game, enter your username into the 'NAME' text entry box. To connect automatically, click 'AUTO'. To connect manually, enter the ip adress into the 'ADDRESS' entry box and click 'CONNECT'."; 
				
		strLS = "At the Lobby screen, click on the lobby that you wish to join and click 'JOIN'. The refresh button will refresh which lobbies are available. The 'CREATE' button will allow you to create a new lobby.";
		
		strLWS = "Here, you have joined a lobby and can wait for the other three players to join before clicking 'READY'. When all players are ready the countdown will start and the game will begin. You can leave the lobby before the game starts by clicking on 'LEAVE'."; 
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
	}

	
	public void nextPicture(){
		imageCount++;
		
	}
	
	
	@Override
	public void render(IRenderer r) {
		backgroundImage.render(r);
		
		nextBtn.setX((int) winWidth - 100);
		nextBtn.setY((int) 100);
		
		r.drawText(font, "HOW TO SET UP A GAME", Align.TL, false, winWidth/2, winHeight, 1.0f);
		
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

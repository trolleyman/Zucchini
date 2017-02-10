package game.ui;

import game.ColorUtil;
import game.InputHandler;
import java.lang.Math;
import game.InputPipeMulti;
import game.audio.AudioManager;
import game.render.Align;
import game.render.IRenderer;
import game.render.TextureBank;
import game.world.ClientWorld;
import game.world.World;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL11.*;
import java.util.ArrayList;
import static org.lwjgl.glfw.GLFW.*;


/**
 * The GameUI is the UI responsible for rendering, updating the game and handling input
 * 
 * @author Abbygayle Wiggins
 */
public class GameUI extends UI implements InputPipeMulti {
	
	/** The world of the game */
	private ClientWorld world;
	private float winWidth; //window width
	private float winHeight; //window height
	private float barWidth;
	private float barHeight;
	private float mapSize;
	private ArrayList<InputHandler> inputHandlers = new ArrayList<>();
   private UI nextUI;
   private TextureBank bank;
	/**
	 * Constructs a new GameUI
	 * @param _world The world
	 */
	public GameUI(AudioManager audio, TextureBank _bank, ClientWorld _world) {
		super(audio);
		this.world = _world;
		this.bank = _bank;
		this.inputHandlers.add(world);
		
		nextUI = this;

		
	/*	this.addKeyListener(new KeyListener(){
			public void keyTyped(KeyEvent e){
				if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
					System.exit(0);
				}
			}
		});*/
	}
	
	@Override
	public ArrayList<InputHandler> getHandlers() {
		// TODO Auto-generated method stub
		return this.inputHandlers;
		
	}
	
	@Override
	public void handleKey(int key, int scancode, int action, int mods) {
		
		InputPipeMulti.super.handleKey(key, scancode, action, mods);
		if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS){
			System.out.println("escape pressed");
			this.nextUI = new EscapeUI(audio, bank, world);//change null to renderer?
		}
	}

	
	@Override
	public void handleResize(int w, int h) {
		this.winWidth = w;
		this.winHeight = h;
		InputPipeMulti.super.handleResize(w, h);
	}
	
	@Override
	public void update(double dt) {
	//	stencil();
		
		barWidth = (winWidth/3);
		barHeight = (winHeight/10);
		mapSize = (winHeight/5);
		this.world.update(dt);
	}
	
	public void stencil(IRenderer r){
		
		
		glClear(GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
		glEnable(GL_STENCIL_TEST);
		glEnable(GL_STENCIL_FUNC);
		
	//	glClear(GL_STENCIL_BUFFER_BIT);
		
		glEnable(GL_STENCIL_TEST);
		glStencilFunc(GL_NEVER, 1, 0xFF);
		glStencilOp(GL_REPLACE, GL_KEEP, GL_KEEP);
		
	// draw stencil pattern
		glStencilMask(0xFF);
		glClear(GL_STENCIL_BUFFER_BIT);
	//	drawCircle();
		Vector4f colour = ColorUtil.WHITE;
		r.drawBox(Align.MM, winWidth/2, winHeight/2, 100, 100, colour);
		
		glStencilMask(0x00);
		
		// draw where stencil's value is 0
		glStencilFunc(GL_EQUAL, 0, 0xFF);
		 
		/* (nothing to draw) */
		// draw only where stencil's value is 1
		//glStencilFunc(GL_EQUAL, 1, 0xFF);
		glDisable(GL_STENCIL_TEST);
  
		//glStencilFunc(GL_GEQUAL, 2, 0xFF);
	//	glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
		//glStencilMask(0xFF);
		
	/* glClear(GL_STENCIL_BUFFER_BIT);
		glEnable(GL_STENCIL_TEST);
		glStencilFunc(GL_LESS,1,1);
		glStencilOp(GL_REPLACE, GL_KEEP, GL_KEEP);
		glPushMatrix();
		//glTranslatef(0.5,0,0);
	//	glColor3f(0,0,1);
	//	glutSolidSphere(0.6,16,16); // DRAWING METHOD SPHERE
		glPopMatrix();		*/
		
	}
	

	
	@Override
	public void render(IRenderer r) {
		stencil(r);
		this.world.render(r);
		r.drawTexture(r.getImageBank().getTexture("healthbar.png"), Align.BL, winWidth-barWidth, winHeight-barHeight, barWidth, barHeight);
	   r.drawTexture(r.getImageBank().getTexture("minimap.png"), Align.BL, 10, 10, mapSize, mapSize); //this will get changed with hiddenmap() later on
	}
	
	
	@Override
	public UI next() {
		// TODO: Handle exiting
		return nextUI;
	}
	
	@Override
	public String toString() {
		return "GameUI";
	}

	@Override
	public void destroy() {
		this.world.destroy();
	}


}

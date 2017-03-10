package game.ui;

import game.ColorUtil;
import game.InputHandler;
import game.InputPipeMulti;
import game.Util;
import game.audio.AudioManager;
import game.net.client.IClientConnection;
import game.render.Align;
import game.render.FontBank;
import game.render.IRenderer;
import game.render.TextureBank;
import game.world.ClientWorld;

import game.world.World;
import game.world.entity.Player;
import org.joml.Vector4f;

import game.world.entity.Item;
import game.world.entity.Player;
import game.world.entity.weapon.Weapon;
import game.world.map.Map;


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
   private Player player;
   
   public GameUI(UI ui){
   	super(ui);
   	
   }
   
	/**
	 * Constructs a new GameUI
	 * @param _world The world
	 */
	public GameUI(IClientConnection _conn, AudioManager audio, TextureBank _bank, FontBank _fb, ClientWorld _world) {
		super(_conn, audio, _bank, _fb);
		this.world = _world;
		this.bank = _bank;
		this.inputHandlers.add(world);
		this.player = world.getPlayer();
		nextUI = this;
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
			this.nextUI = new EscapeUI(connection, audio, bank, fontBank, world);
		} else if (key == GLFW_KEY_UP && action == GLFW_PRESS){ //up arrow for now - may change to "M" later 
			System.out.println("M pressed");
			this.nextUI = new MiniMap(connection, audio, bank, fontBank, world);
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
	@Override
	public void render(IRenderer r) {
		this.world.render(r);
		createMiniMap(r);
		createHealthBar(r);
		//GL11.glEnable(GL11.GL_SCISSOR_TEST);
	   //GL11.glScissor((int) (winWidth/2) - 200, (int) (winHeight/2) - 200, 400, 400);
	}
	
	public void createHealthBar(IRenderer r){
		float maxHealth;
		//if(player.getMaxHealth() == 0.0f){
		maxHealth = 10.0f;
		//}else{
		//	maxHealth = player.getMaxHealth();
		//}
		float currentHealth = 6.0f; //player.getCurrentHealth(); ??
		r.drawBox(Align.TR, (float) winWidth - 20, (float) winHeight - 20, (float) 30 * maxHealth, (float) 20, ColorUtil.GREEN);//max health
		r.drawBox(Align.TR, winWidth - 20, winHeight - 20, 30 * (maxHealth - currentHealth), 20, ColorUtil.RED);
	}
	
	public void stencil(IRenderer r){ //CURRENTLY ABSOLUTELY NOT WORKING
	
		/*// Clear Screen, Depth Buffer & Stencil Buffer
	   glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
	 
	   // Clip Plane Equations
	   double eqr[] = {0.0f,-1.0f, 0.0f, 0.0f};                // Plane Equation To Use For The Reflected Objects
	   
	   glColorMask(false, false, false, false);                           // Set Color Mask
	   glEnable(GL_STENCIL_TEST);                  // Enable Stencil Buffer For "marking" The Floor
	   glStencilFunc(GL_ALWAYS, 1, 1);                // Always Passes, 1 Bit Plane, 1 As Mask
	   glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE);              // We Set The Stencil Buffer To 1 Where We Draw Any Polygon
	                                       // Keep If Test Fails, Keep If Test Passes But Buffer Test Fails
	                                       // Replace If Test Passes
	   glDisable(GL_DEPTH_TEST);                       // Disable Depth Testing
	 //  DrawFloor(); 
		this.world.render(r);
		
		glEnable(GL_DEPTH_TEST);                        // Enable Depth Testing
		glColorMask(true, true, true, true);                           // Set Color Mask to TRUE, TRUE, TRUE, TRUE
		glStencilFunc(GL_EQUAL, 1, 1);                      // We Draw Only Where The Stencil Is 1
		                                    // (I.E. Where The Floor Was Drawn)
		glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);   
		glEnable(GL_CLIP_PLANE0);                       // Enable Clip Plane For Removing Artifacts
      // (When The Object Crosses The Floor)
		glClipPlane(GL_CLIP_PLANE0, eqr);                   // Equation For Reflected Objects
		glPushMatrix();                             // Push The Matrix Onto The Stack
		glScalef(1.0f, -1.0f, 1.0f);                    // Mirror Y Axis
	    
		/*glLightfv(GL_LIGHT0, GL_POSITION, LightPos);            // Set Up Light0
	   glTranslatef(0.0f, height, 0.0f);               // Position The Object
	   glRotatef(xrot, 1.0f, 0.0f, 0.0f);              // Rotate Local Coordinate System On X Axis
	   glRotatef(yrot, 0.0f, 1.0f, 0.0f);              // Rotate Local Coordinate System On Y Axis
		
		
	   Vector4f colour = ColorUtil.WHITE;
		r.drawBox(Align.MM, winWidth/2, winHeight/2, 100, 100, colour);
	   
	   glPopMatrix();                              // Pop The Matrix Off The Stack
   	glDisable(GL_CLIP_PLANE0);                      // Disable Clip Plane For Drawing The Floor
   	glDisable(GL_STENCIL_TEST);                     // We Don't Need The Stencil Buffer Any More (Disable)
	
   	//glLightfv(GL_LIGHT0, GL_POSITION, LightPos);                // Set Up Light0 Position
   	glEnable(GL_BLEND);                         // Enable Blending (Otherwise The Reflected Object Wont Show)
   	glDisable(GL_LIGHTING);                         // Since We Use Blending, We Disable Lighting
   	glColor4f(1.0f, 1.0f, 1.0f, 0.8f);                  // Set Color To White With 80% Alpha
   	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);          // Blending Based On Source Alpha And 1 Minus Dest Alpha
   	this.world.render(r);                                // Draw The Floor To The Screen
   	*/
	}
	
	public void createMiniMap(IRenderer r){ //ALSO ABSOLUTELY NOT WORKING
		
		//r.drawBox(Align.BL, (float) 100, (float) 100, (float) 300, (float) 300, ColorUtil.WHITE);	
	
	/*	 Canvas openglSurface = new Canvas();
       JFrame frame = new JFrame();
       frame.setSize(800, 800);
       frame.add(openglSurface);
       frame.setVisible(true);
       frame.add(new JTextField("Hello World!"));
       openglSurface.setSize(500, 500);
       Display.setParent(openglSurface);
       Display.create();
       GL11.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
       GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
       Display.update();
       Thread.sleep(2000);
       Display.destroy();
	*/
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

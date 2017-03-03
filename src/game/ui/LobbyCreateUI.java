package game.ui;

import game.InputHandler;
import game.InputPipeMulti;
import game.render.Align;
import game.render.IRenderer;
import game.ui.component.TextEntryComponent;
import game.ui.component.UIComponent;

import java.util.ArrayList;

public class LobbyCreateUI extends UI implements InputPipeMulti {
	
	private static final float PADDING = 15.0f;
	
	private UI nextUI = this;
	
	private ArrayList<UIComponent> components;
	
	public LobbyCreateUI(UI _ui) {
		super(_ui);
		
		TextEntryComponent entry = new TextEntryComponent(
				fontBank.getFont("emulogic.ttf"),
				Character::isLetterOrDigit,
				this::submit,
				Align.TM,
				PADDING, PADDING, 100, 80
		);
		
		components = new ArrayList<>();
		components.add(entry);
	}
	
	private void submit() {
		connection.sendLobbyCreateRequest();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<InputHandler> getHandlers() {
		// This cast is fine since all UIComponent's are InputHandlers
		return (ArrayList) components;
	}
	
	@Override
	public void render(IRenderer r) {
		
	}
	
	@Override
	public void update(double dt) {
		
	}
	
	@Override
	public UI next() {
		return nextUI;
	}
}

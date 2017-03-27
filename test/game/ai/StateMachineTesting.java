package game.ai;

import static org.junit.Assert.*;

import org.joml.Vector2f;
import org.junit.Test;

import game.ai.state.EvadeState;
import game.ai.state.MoveTowardsCentreState;
import game.ai.state.ShootEnemyState;
import game.ai.state.WanderState;

public class StateMachineTesting {

	@Test
	public void testChangeState() {
		@SuppressWarnings("unchecked")
		IStateMachine<AIPlayer, State<AIPlayer>> stateMachine = new StateMachine(new AIPlayer(3,new Vector2f(5,5), "A", Difficulty.MEDIUM),new MoveTowardsCentreState()) ;
		
		stateMachine.changeState(new EvadeState());
		assertEquals(stateMachine.getCurrentState().getClass(),(new EvadeState().getClass()));
		stateMachine.changeState(new MoveTowardsCentreState());
		assertEquals(stateMachine.getCurrentState().getClass(),(new MoveTowardsCentreState().getClass()));
		stateMachine.changeState(new ShootEnemyState());
		assertEquals(stateMachine.getCurrentState().getClass(),(new ShootEnemyState().getClass()));
		stateMachine.changeState(new WanderState());
		assertEquals(stateMachine.getCurrentState().getClass(),(new WanderState().getClass()));
	}

	@Test
	public void testGetCurrentState() {
		@SuppressWarnings("unchecked")
		IStateMachine<AIPlayer, State<AIPlayer>> stateMachine = new StateMachine(new AIPlayer(3,new Vector2f(5,5), "A", Difficulty.MEDIUM),new MoveTowardsCentreState()) ;
		assertEquals(new MoveTowardsCentreState().getClass(), stateMachine.getCurrentState().getClass());
		IStateMachine<AIPlayer, State<AIPlayer>> stateMachine2 = new StateMachine(new AIPlayer(3,new Vector2f(5,5), "A", Difficulty.MEDIUM),new EvadeState()) ;
		assertEquals(new EvadeState().getClass(), stateMachine2.getCurrentState().getClass());
	}


	@Test
	public void testGetPreviousState() {
		@SuppressWarnings("unchecked")
		IStateMachine<AIPlayer, State<AIPlayer>> stateMachine = new StateMachine(new AIPlayer(3,new Vector2f(5,5), "A", Difficulty.MEDIUM),new MoveTowardsCentreState()) ;
		stateMachine.changeState(new WanderState());
		assertEquals(new MoveTowardsCentreState().getClass(), stateMachine.getPreviousState().getClass());
		
		
		IStateMachine<AIPlayer, State<AIPlayer>> stateMachine2 = new StateMachine(new AIPlayer(3,new Vector2f(5,5), "A", Difficulty.MEDIUM),new MoveTowardsCentreState()) ;
		//if no previous state return null
		assertEquals(null, stateMachine2.getPreviousState());
	}
	
	@Test
	public void testInitialState() {
		@SuppressWarnings("unchecked")
		IStateMachine<AIPlayer, State<AIPlayer>> stateMachine = new StateMachine(new AIPlayer(3,new Vector2f(5,5), "A", Difficulty.MEDIUM),new MoveTowardsCentreState()) ;
		assertEquals(new MoveTowardsCentreState().getClass(), stateMachine.getCurrentState().getClass());
	
	}

}

package game.ai;

public enum Difficulty {
	EASY  (0.2f, (float)Math.toRadians(5.0)),
	MEDIUM(0.2f, (float)Math.toRadians(5.0)),
	HARD  (0.2f, (float)Math.toRadians(5.0));
	
	/**
	 * Constructs a new difficulty setting.
	 * @param turningRate See {@link Difficulty#getTurningRate()}
	 * @param deviation See {@link Difficulty#getDeviation()}
	 */
	Difficulty(float turningRate, float deviation) {
		this.turningRate = turningRate;
		this.deviation = deviation;
	}
	
	private float turningRate;
	private float deviation;
	
	/**
	 * Returns the turning rate, which determines how fast the AI can spin around. The higher, the more difficult the AI.
	 */
	public float getTurningRate() {
		return turningRate;
	}
	
	/**
	 * Returns the deviation of the firing angle in radians. The lower, the more difficult the AI.
	 */
	public float getDeviation() {
		return deviation;
	}
}

package fishingGame;

public abstract class Fish extends Sprite {
	protected double speed;

	protected final static int FISH_INIT_DIAMETER = 40;
	protected final static double FISH_INIT_SPEED = 120 / FISH_INIT_DIAMETER;

	public Fish(int xPos, int yPos) {
		super(xPos, yPos, Fish.FISH_INIT_DIAMETER, Fish.FISH_INIT_DIAMETER);
		this.speed = Fish.FISH_INIT_SPEED;
	}

	// Player and Enemy movement are different
	// Player: controlled by user; Enemy: random movements
	abstract void move();



	// fish diameter should increase by 10
	abstract void eatFood();


	// fish diameter should increase by the sie of the Fish eaten
	abstract void eatEnemy(Enemy enemy);


	// getters ----
	public double getSpeed() {
		return this.speed;
	}
	// ------------

	// setters ----
	public void setSpeed(double value) {
		this.speed = value;
	}
	// ------------
}

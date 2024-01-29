package fishingGame;

import java.util.Random;

import javafx.scene.image.Image;

public class Enemy extends Fish {
	private int moveDir; // dictates where the Enemy should move to

	private long startSpawn; // time when the enemy got spawned
	private int moveTime; // dictates how long it should move in a certain direction (in seconds)

	public final static Image ENEMY_IMG = new Image("images/enemy.png", Fish.FISH_INIT_DIAMETER, Fish.FISH_INIT_DIAMETER, false, false);


	public Enemy(int xPos, int yPos) {
		super(xPos, yPos);
		this.loadImage(ENEMY_IMG);
		this.startSpawn = System.nanoTime();

		// initially give the enemy a direction to move to
		changeDir();
		// and how long it should move to that direction
		changeTime();
	}




	void changeDir() {
		Random r = new Random();
		//returns a random number from 0-3
		this.moveDir = r.nextInt(4);
	}




	void changeTime() {
		Random r = new Random();
		this.moveTime = r.nextInt(5) + 3; // add 1 to make sure that zero will not occur
	}


	// enemy can move in a certain random direction
	@Override
	void move() {
		// UP
		if (this.moveDir == 0) this.setDY(-this.getSpeed());
		// LEFT
		if (this.moveDir == 1) this.setDX(-this.getSpeed());
		// DOWN
		if (this.moveDir == 2) this.setDY(this.getSpeed());
		// RIGHT
		if (this.moveDir == 3) this.setDX(this.getSpeed());

		this.xPos += this.dx;
		this.yPos += this.dy;
	}




	// eat fellow Enemy
	@Override
	void eatEnemy(Enemy enemy) {
		double newDiam = this.getWidth() + enemy.getWidth();
		Image img = new Image("images/enemy.png", newDiam, newDiam, false, false);
		// increase size of Enemy
		this.loadImage(img);
		this.speed = 120 / this.getWidth();
	}

	// eat Player
	void eatPlayer(Player player) {
		double newDiam = this.getWidth() + player.getWidth();
		Image img = new Image("images/enemy.png", newDiam, newDiam, false, false);
		// increase size of Enemy
		this.loadImage(img);
		this.speed = 120 / this.getWidth();
	}



	@Override
	void eatFood() {
		double newDiam = this.getWidth() + Food.FOOD_GIVES;

		Image img = new Image("images/enemy.png", newDiam, newDiam, false, false);
		this.loadImage(img);
		this.speed = 120 / this.getWidth();

//		System.out.println("== Food eaten! ==");
//		System.out.println("Size = " + this.getWidth() + "; (Size increased by " + newDiam + ".)");
//		System.out.println("");
	}





	// make the enemy disappear when it collides with the bigger player
	void checkCollision_Player(Player player) {
		if (this.collidesWith(player)) {
			// check their size, the bigger will eat the smaller
			if (player.getWidth() >= this.getWidth()) {
				// increase size of player by the size of the enemy eaten
				player.eatEnemy(this);
				this.vanish();
			} else {
				// enemy cannot eat player that is immune
				if (player.isImmune() == false) {
					this.eatPlayer(player);
					player.die();
				}
			}
		}
	}

	// make the smaller enemy disappear when there's a collision between enemies
	void checkCollision_Enemy(Enemy enemy) {
		if (this.collidesWith(enemy)) {
			// make this disappear when the other enemy is bigger
			if (enemy.getWidth() > this.getWidth()) {
				System.out.println("== Enemy ate an enemy! ==");
				enemy.eatEnemy(this);
				this.vanish();
			}
		}
	}




	// getters ---------
	public long getStartSpawn() {
		return this.startSpawn;
	}
	public int getMoveTime() {
		return this.moveTime;
	}
	// -----------------

	// setters ---------
	public void setStartSpawn(long nanoTime) {
		this.startSpawn = nanoTime;
	}
	// -----------------
}

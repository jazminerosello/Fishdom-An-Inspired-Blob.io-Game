package fishingGame;

import java.io.File;

import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

// main character of the game

public class Player extends Fish {
	private String name;
	private boolean alive;
	// for stats at the end of the game ---
	private int foodEaten;
	private int fishEaten;
	// ------------------------------------
	private boolean immune;
	private long startImmunity; // time when the player got the immunity power-up (will be used to make sure that it lasts only 5 secs)
	private boolean boosted;
	private long startBoosted; // time when the player got the speed-up power-up (will be used to make sure that it lasts only 5 secs)

	//for the sound effect when player eats food/enemy
	private Media eatSound = new Media(new File("src/images/fishEat.mp3").toURI().toString());
	private MediaPlayer mediaPlayer = new MediaPlayer(eatSound);

	public final static String PLAYER_IMG = "images/fish.png";
	public final static String PLAYER_IMG_SHIELDED = "images/fish_immunity.png";
	public final static int POWERUP_TIME = 5;

	public Player(String name, int xPos, int yPos) {
		super(xPos, yPos);
		this.name = name;
		this.alive = true;

		this.foodEaten = 0;
		this.fishEaten = 0;

		this.immune = false;
		this.boosted = false;

		Image plyrImg = new Image(Player.PLAYER_IMG, Fish.FISH_INIT_DIAMETER, Fish.FISH_INIT_DIAMETER, false, false);
		this.loadImage(plyrImg);
	}





	@Override
	// change xPos and yPos to make it look like the Player is moving
	// NOTE: Player should not go out of the window when moving
	// NOTE: technically not used since key press moves the bg and other sprites instead of the player
	void move() {
		double newXPos = this.xPos + this.dx;
		double newYPos = this.yPos + this.dy;

		//if 'yung x posiition ni player eh greater than or equal to 0 pa o nasa loob pa ng screen at kapag ang position nito
		//ay hindi pa lumampas sa difference ng window_width at ng width ng player, this.xPos lang tapos add doon 'yung dx meanign, kaya pa galawin si fish
		//kaya sinubtractt 'yung width ng fish object ni player sa window width ay dahil 'yung width nung fish sinasakop niya yung width na meron siya
		// sa window_width
		if (newXPos >= 0 && newXPos < Game.WINDOW_WIDTH - this.getWidth())
			this.xPos += this.dx;

		//kapag naman si fish ay 'yung y position ay greater than or equal to 0, at yung y position ay hindi pa lagpas sa sinubtract na window_height
		//at height ng fish then magagalaw pa si fish
		if (newYPos >= 0 && newYPos < Game.WINDOW_HEIGHT - this.getHeight())
			this.yPos += this.dy;
	}





	void eatEnemy(Enemy enemy) {

		double newDiam = this.getWidth() + enemy.getWidth();

		Image img;
		// img displayed can be the PLAYER_IMG or SHIELDED
		if (this.immune == false) {
			img = new Image(Player.PLAYER_IMG, newDiam, newDiam, false, false);
		} else {
			img = new Image(Player.PLAYER_IMG_SHIELDED, newDiam, newDiam, false, false);
		}
		// increase size of player
		this.loadImage(img);
		this.speed = 120 / this.getWidth();

		//to play the sound effect
		mediaPlayer.seek(Duration.ONE);
		mediaPlayer.setRate(1);
		mediaPlayer.play();

		// increase count of enemy eaten
		this.fishEaten += 1;

		System.out.println("== Enemy eaten! ==");
		System.out.println("Size = " + this.getWidth() + "; (Size increased by " + newDiam + ".)");
		System.out.println("");
	}




	@Override
	void eatFood() {


		double newDiam = this.getWidth() + Food.FOOD_GIVES;

		Image img;
		// img displayed can be the PLAYER_IMG or SHIELDED
		if (this.immune == false) {
			img = new Image(Player.PLAYER_IMG, newDiam, newDiam, false, false);
		} else {
			img = new Image(Player.PLAYER_IMG_SHIELDED, newDiam, newDiam, false, false);
		}
		this.loadImage(img);
		this.speed = 120 / this.getWidth();

		//to play the sound effect
		mediaPlayer.seek(Duration.ONE);
		mediaPlayer.setRate(1);
		mediaPlayer.play();

		this.foodEaten += 1;

		System.out.println("== Food eaten! ==");
		System.out.println("Size = " + this.getWidth() + "; (Size increased by " + newDiam + ".)");
		System.out.println("");
	}





	// RECEIVE POWER-UPS -----
	// double the player's speed
	void speedUp() {
		this.speed = this.speed * 2;

		this.boosted = true;
		this.startBoosted = System.nanoTime();

		System.out.println("== Speed increased! ==");
		System.out.println("");

	}



	void normalizeSpeed() {
		this.speed = this.speed / 2;

		this.boosted = false;

		System.out.println("== Speed increase wore off! ==");
	}



	// make player immune and change player img to signify immunity
	void shieldUp() {
		this.immune = true;
		this.startImmunity = System.nanoTime();

		Image img = new Image(Player.PLAYER_IMG_SHIELDED, this.getWidth(), this.getHeight(), false, false);
		this.loadImage(img);

		System.out.println("== Immunity gained! ==");
		System.out.println("");
	}



	void removeShield() {
		this.immune = false;

		Image img = new Image(Player.PLAYER_IMG, this.getWidth(), this.getHeight(), false, false);
		this.loadImage(img);

		System.out.println("== Immunity wore off! ==");
	}
	// -----------------------






	void die() {
		this.alive = false;
	}





	// getters -----
	public boolean isAlive() {
		return this.alive;
	}

	public boolean isImmune() {
		return this.immune;
	}

	public boolean isBoosted() {
		return this.boosted;
	}

	public long getStartImmunity() {
		return this.startImmunity;
	}

	public long getStartBoosted() {
		return this.startBoosted;
	}

	public String getFishEaten() {
		return Integer.toString(this.fishEaten);
	}

	public String getFoodEaten() {
		return Integer.toString(this.foodEaten);
	}

	public String getSize() {
		return Integer.toString((int) this.getWidth());
	}
	// -------------

	// setters -----
	public void setImmune(boolean value) {
		this.immune = value;
	}

	public void setBoosted(boolean value) {
		this.boosted = value;
	}

	// -------------
}

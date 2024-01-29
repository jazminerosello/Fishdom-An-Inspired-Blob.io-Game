package fishingGame;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

// GameTimer is responsible for making the game move

public class GameTimer extends AnimationTimer {

	private GraphicsContext gc;
	private Scene gameScene;
	private long startTime; // for game stats (to know the time alive)
	private long item_spawnTime; // will be used to spawn a power_up every 10 secs
	private long timeAlive;

	private Player player;
	private ArrayList<Enemy> enemies;
	private ArrayList<Food> foods;
	private ArrayList<Item> items;


	// for the background ---
	private double backgroundX;
	private double backgroundY;
	private Image background = new Image("images/gameBg.png", Game.MAP_WIDTH, Game.MAP_HEIGHT, false, false);
	private boolean withinBounds;
	private boolean moveBG; // to allow movement of BG even when player might be at the bounds

	//for the sound effect that will be played when game over
	private Media gameOverSound = new Media(new File("src/images/gameOver.mp3").toURI().toString());
	private MediaPlayer mediaPlayer = new MediaPlayer(gameOverSound);
	// ----------------------

	// constants ----
	public final static int INIT_ENEMY_SPAWNS = 10;
	public final static int TOTAL_FOOD = 50;
	// --------------

	public GameTimer(Scene gameScene, GraphicsContext gc) {
		this.gc = gc;
		this.gameScene = gameScene;
		this.startTime = System.nanoTime(); // get current nanotime
		this.item_spawnTime = System.nanoTime();
		this.player = new Player("Player 1", Game.WINDOW_CENTER, Game.WINDOW_CENTER);
		this.enemies = new ArrayList<Enemy>();
		this.foods = new ArrayList<Food>();
		this.items = new ArrayList<Item>();
		this.withinBounds = true;
		this.moveBG = false;

		// methods ran at the start of GameTimer ---
		this.handleKeyPressEvent();

		this.spawnEnemies();

		this.spawnFoods();
		// -----------------------------------------
	}




	@Override
	public void handle(long currentNanoTime) {
		long currentSec = TimeUnit.NANOSECONDS.toSeconds(currentNanoTime);
		this.redrawBackgroundImage();
		this.checkBounds();

		// MOVE SPRITES ---------
		this.moveSprites();

		// change direction of each Enemy movement after certain random seconds
		for (Enemy enemy: this.enemies) {
			long startSec = TimeUnit.NANOSECONDS.toSeconds(enemy.getStartSpawn());
			// change direction of specific enemy when indicated time has passed
			if ((currentSec - startSec)%enemy.getMoveTime() == 0) {
//				System.out.println(">> curr: "+ currentSec + " >>start: "+ startSec);
				enemy.changeDir();

				// get a new random move time once the specified time has elapsed
				enemy.changeTime();
				enemy.setStartSpawn(currentNanoTime);
			}

		}
		// ----------------------

		// RENDER SPRITES ---------
		this.renderSprites();
		this.respawnFood();
		// ------------------------

		// ALL ABOUT POWER-UPS ----
		// spawn random power-up every 10 seconds interval
		long item_startSec = TimeUnit.NANOSECONDS.toSeconds(this.item_spawnTime);
		if ((currentSec - item_startSec + 1)%( Item.SPAWN_INTERVAL+1) == 0) { // add 1 since 0 divide anything would result to zero
			System.out.println("== Power-Up spawned! == time: " + (currentSec-item_startSec));
//			System.out.println(">> curr: "+ currentSec + " >>start: "+ item_startSec);
			this.spawnPowerUp();
			this.item_spawnTime = currentNanoTime;
		}

		// remove the specific power-up when it has been present on the screen for 5 seconds
		for (Item item: this.items) {
			long startSec = TimeUnit.NANOSECONDS.toSeconds(item.getStartSpawn());
			if ((currentSec-startSec + 1)% (Item.VISIBLE_TIME+1) == 0) { // add 1 since 0 divide anything would result to zero
				System.out.println("== Power-up disappeared! == time: " + (currentSec-startSec));
//				System.out.println(">> curr: "+ currentSec + " >>start: "+ startSec);
				item.vanish();
			}
		}


		// wear off power-up received after 5 seconds
		if (player.isBoosted()) {
			long startSec = TimeUnit.NANOSECONDS.toSeconds(player.getStartBoosted());
			if ((currentSec - startSec + 1) % (Player.POWERUP_TIME+1) == 0) {
				System.out.println(">> curr: "+ currentSec + " >>start: "+ startSec +"; time: " + (currentSec-startSec));
				player.normalizeSpeed();
			}
		}
		if (player.isImmune()) {
			long startSec = TimeUnit.NANOSECONDS.toSeconds(player.getStartImmunity());
			if ((currentSec-startSec + 1) % (Player.POWERUP_TIME+1) == 0) {
				System.out.println(">> curr: "+ currentSec + " >>start: "+ startSec +"; time: " + (currentSec-startSec));
				player.removeShield();
			}
		}
		// ------------------------

		long startSec = TimeUnit.NANOSECONDS.toSeconds(this.startTime);
		this.timeAlive = currentSec - startSec;
		this.drawScore();

		//if the player is not alive then the game will be over so execute proper music effect
		//and show the game over statistics
		if (!this.player.isAlive()) {
			this.stop();
			//this is to play the respective sound effect when game is over with respective duration adn rate of time
			mediaPlayer.seek(Duration.ONE);
			mediaPlayer.setRate(1);
			mediaPlayer.play();
			this.drawGameOver();
		}
	}

	//draw the background
	private void redrawBackgroundImage() {
		this.gc.clearRect(0, 0, Game.WINDOW_WIDTH, Game.WINDOW_HEIGHT);
		this.gc.drawImage(background, this.backgroundX,this.backgroundY);
	}





	// checks if window is showing the edge of the map already
	private void checkBounds() {
		if (this.backgroundX > Game.BOUNDS1 || this.backgroundX < Game.BOUNDS2) {
			this.withinBounds = false;
		}
		else if (this.backgroundY > Game.BOUNDS1 || this.backgroundY < Game.BOUNDS2) {
			this.withinBounds = false;
		}
		else {
			this.withinBounds = true;
		}
	}





	// spawn ten initial enemy fishes in random areas on the map
	private void spawnEnemies() {
		Random r = new Random();

		for (int i=0; i<GameTimer.INIT_ENEMY_SPAWNS; i++) {
			int xPos = r.nextInt(Game.MAP_WIDTH+1);
			int yPos = r.nextInt(Game.MAP_HEIGHT+1);

			// to avoid spawn near player --
			while(true) {
				if(xPos <= player.getXPos()-10 || xPos >= player.getXPos()+10) {
					break;
				}

				if(yPos <= player.getYPos()+10 || yPos <= player.getYPos()-10) {
					break;
				}
				xPos = r.nextInt(Game.MAP_WIDTH);
				yPos = r.nextInt(Game.MAP_HEIGHT);
			}
			// ----------------------------

			this.enemies.add(new Enemy(xPos, yPos));
		}

	}






	// spawn 50 foods in random areas on the map
	void spawnFoods() {
		Random r = new Random();

		for (int i=0; i<GameTimer.TOTAL_FOOD; i++) {
			int xPos = r.nextInt(Game.MAP_WIDTH);
			int yPos = r.nextInt(Game.MAP_HEIGHT);

			//to avoid spawning foods near the player xpos and ypos
			while(true) {
				if(xPos <= player.getXPos()-10 || xPos >= player.getXPos()+10) {
					break;
				}

				if(yPos <= player.getYPos()+10 || yPos <= player.getYPos()-10) {
					break;
				}
				xPos = r.nextInt(Game.MAP_WIDTH);
				yPos = r.nextInt(Game.MAP_HEIGHT);
			}

			this.foods.add(new Food(xPos, yPos));
		}
	}






	// respawn food (need to maintain 50 foods on the game)
	void respawnFood() {
		Random r = new Random();

		for (int i=0; i<foods.size(); i++) {
			Food f = this.foods.get(i);
			f.checkCollision(player);
			for (Enemy enemy: this.enemies) {
				f.checkCollision(enemy);
			}

			// if a player collides with a food, remove that
			// then add a new food
			if (f.isVisible() == false) {
				foods.remove(i);
				int xPos = r.nextInt(Game.MAP_WIDTH);
				int yPos = r.nextInt(Game.MAP_HEIGHT);

				//to avoid respawning foods near the player xpos and ypos
				while(true) {
					if(xPos <= player.getXPos()-10 || xPos >= player.getXPos()+10) {
						break;
					}

					if(yPos <= player.getYPos()+10 || yPos <= player.getYPos()-10) {
						break;
					}
					xPos = r.nextInt(Game.MAP_WIDTH);
					yPos = r.nextInt(Game.MAP_HEIGHT);
				}

				this.foods.add(new Food(xPos, yPos));
			}
		}
	}





	// spawn a random power-up
	void spawnPowerUp() {
		Random r = new Random();
		int choice = r.nextInt(2); // 0 - boost; 1 - shield
		int xPos = r.nextInt(Game.WINDOW_WIDTH+1);
		int yPos = r.nextInt(Game.WINDOW_HEIGHT+1);

		if (choice == 0) {
			this.items.add(new Boost(xPos, yPos));
			System.out.println("BOOST>>>>");

		} else {
			this.items.add(new Shield(xPos, yPos));
			System.out.println("[[SHIELD]]");
		}
	}





	// render needed Sprites on the screen
	private void renderSprites() {
		// show player on the screen
		this.player.render(this.gc); // displays the change of xPos and yPos to the canvas

		// show enemies on the screen
		for (Enemy enemy: this.enemies) {
			enemy.render(this.gc);
		}

		// show foods on the screen
		for (Food food: this.foods) {
			food.render(this.gc);
		}

		// show power-ups on screen
		for (Item item: this.items) {
			item.render(this.gc);
		}
	}




	private void moveSprites() {
		// move the player
		this.player.move();

		// move the enemies
		for (int i=0; i<this.enemies.size(); i++) {
			Enemy e = this.enemies.get(i);
			if(e.isVisible()) {
				e.move();

				// checking if there is a collision ----
				e.checkCollision_Player(player);
				for (Enemy enemy: this.enemies) {
					e.checkCollision_Enemy(enemy);
				}

				// -------------------------------------

			} else {
				this.enemies.remove(i);
			}
		}

		// trigger power-up when player collides with the Item
		for (int i=0; i<this.items.size(); i++) {
			Item p = this.items.get(i);
			if (p.isVisible()) { //NOTE: item will turn invisible when (1) player collides with it; (2) 5 secs has passed
				p.checkCollision(player);
			} else {
				this.items.remove(i);
			}
		}

	}



	// listens to any key press and does corresponding actions
	private void handleKeyPressEvent() {
		this.gameScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			public void handle(KeyEvent e) {
				// code is a value that represents the pressed key
				KeyCode code = e.getCode();
				moveMyFish(code);
			}
		});

		this.gameScene.setOnKeyReleased(new EventHandler<KeyEvent>() {
			public void handle(KeyEvent e) {
				KeyCode code = e.getCode();
				stopMyFish(code);
			}
		});
	}





	private boolean cornerBound() {
		if (this.backgroundX > Game.BOUNDS1 && this.backgroundY > Game.BOUNDS1) return true;
		if (this.backgroundX > Game.BOUNDS1 && this.backgroundY < Game.BOUNDS2) return true;
		if (this.backgroundX < Game.BOUNDS2 && this.backgroundY > Game.BOUNDS1) return true;
		if (this.backgroundX < Game.BOUNDS2 && this.backgroundY < Game.BOUNDS2) return true;
		if (this.backgroundX > 0 || this.backgroundX < Game.BOUNDS2 || this.backgroundY < Game.BOUNDS2 || this.backgroundY > 0) return true;
		return false;
	}





	// method to move player fish depending on the key pressed (used in handleKeyPressEvent)
	private void moveMyFish(KeyCode key) {
		if (key == KeyCode.W || key == KeyCode.UP) {
//			this.player.setDY(-this.player.getSpeed());
			if (this.withinBounds || this.moveBG) {
				// --
				if (cornerBound()) this.moveBG = false;
				// --
				this.backgroundY += this.player.getSpeed();
				for (Food food: this.foods) {
					food.setYPos(food.getYPos() + this.player.getSpeed());
				}
				for (Item item: this.items) {
					item.setYPos(item.getYPos() + this.player.getSpeed());
				}
				for (Enemy enemy: this.enemies) {
					enemy.setYPos(enemy.getYPos() + this.player.getSpeed());
				}

			// WINDOW is on the edge of bounds of MAP
			} else {
				// on upper left bound
				if (this.backgroundX > Game.BOUNDS1 && this.backgroundY > Game.BOUNDS1) {
					System.out.println(">> upper left bound");
					this.moveBG = false;
					this.player.setDY(-this.player.getSpeed());
				}
				// on lower left bound
				else if (this.backgroundX > Game.BOUNDS1 && this.backgroundY < Game.BOUNDS2) {
					System.out.println(">> lower left bound");
					if (this.player.getYPos() > Game.WINDOW_CENTER) {
						this.moveBG = false;
						this.player.setDY(-this.player.getSpeed());
					} else {
						this.moveBG = true;
						this.player.setYPos(Game.WINDOW_CENTER);
						this.backgroundY = Game.BOUNDS2;
					}
				}
				// on upper right bound
				else if (this.backgroundX < Game.BOUNDS2 && this.backgroundY > Game.BOUNDS1) {
					System.out.println(">> upper right bound");
					this.moveBG = false;
					this.player.setDY(-this.player.getSpeed());
				}
				// on lower right bound
				else if (this.backgroundX < Game.BOUNDS2 && this.backgroundY < Game.BOUNDS2) {
					System.out.println(">> lower right bound");
					if (this.player.getYPos() > Game.WINDOW_CENTER) {
						this.moveBG = false;
						this.player.setDY(-this.player.getSpeed());
					} else {
						this.moveBG = true;
						this.player.setYPos(Game.WINDOW_CENTER);
						this.backgroundY = Game.BOUNDS2;
					}
				}
				// on left bound
				else if (this.backgroundX > Game.BOUNDS1) {
					System.out.println(">> left bound");
					this.moveBG = true;
					if (this.backgroundX > Game.BOUNDS1 && this.backgroundY > Game.BOUNDS1) {
						this.moveBG = false;
					}
				}
				// on right bound
				else if (this.backgroundX < Game.BOUNDS2) {
					System.out.println(">> right bound");
					this.moveBG = true;
					if (this.backgroundX < Game.BOUNDS2 && this.backgroundY > Game.BOUNDS1) {
						this.moveBG = false;
					}
				}
				// on lower bound
				else if (this.backgroundY < Game.BOUNDS2) {
					System.out.println(">> lower bound");
					if (this.player.getYPos() > Game.WINDOW_CENTER) {
						this.moveBG = false;
						this.player.setDY(-this.player.getSpeed());
					} else {
						this.moveBG = true;
						this.player.setYPos(Game.WINDOW_CENTER);
						this.backgroundY = Game.BOUNDS2;
					}
				}
				// on upper bound
				else if (this.backgroundY > Game.BOUNDS1) {
					System.out.println(">> upper bound");
					this.moveBG = false;
					this.player.setDY(-this.player.getSpeed());
				}
			}
		}




		if (key == KeyCode.A || key == KeyCode.LEFT) {
//			this.player.setDX(-this.player.getSpeed());
			if (this.withinBounds || this.moveBG) {
				// --
				if (cornerBound()) this.moveBG = false;
				// --
				this.backgroundX += this.player.getSpeed();
				for (Food food: this.foods) {
					food.setXPos(food.getXPos() + this.player.getSpeed());
				}
				for (Item item: this.items) {
					item.setXPos(item.getXPos() + this.player.getSpeed());
				}
				for (Enemy enemy: this.enemies) {
					enemy.setXPos(enemy.getXPos() + this.player.getSpeed());
				}

			// WINDOW is on the edge of bounds of MAP
			} else {
				// on upper left bound
				if (this.backgroundX > Game.BOUNDS1 && this.backgroundY > Game.BOUNDS1) {
					System.out.println(">> upper left bound");
					this.moveBG = false;
					this.player.setDX(-this.player.getSpeed());
				}
				// on lower left bound
				else if (this.backgroundX > Game.BOUNDS1 && this.backgroundY < Game.BOUNDS2) {
					System.out.println(">> lower left bound");
					this.moveBG = false;
					this.player.setDX(-this.player.getSpeed());
				}
				// on upper right bound
				else if (this.backgroundX < Game.BOUNDS2 && this.backgroundY > Game.BOUNDS1) {
					System.out.println(">> upper right bound");
					if (this.player.getXPos() > Game.WINDOW_CENTER) {
						this.moveBG = false;
						this.player.setDX(-this.player.getSpeed());
					} else {
						this.moveBG = true;
						this.player.setXPos(Game.WINDOW_CENTER);
						this.backgroundX = Game.BOUNDS2;
					}
				}
				// on lower right bound
				else if (this.backgroundX < Game.BOUNDS2 && this.backgroundY < Game.BOUNDS2) {
					System.out.println(">> lower right bound");
					if (this.player.getXPos() > Game.WINDOW_CENTER) {
						this.moveBG = false;
						this.player.setDX(-this.player.getSpeed());
					} else {
						this.moveBG = true;
						this.player.setXPos(Game.WINDOW_CENTER);
						this.backgroundX = Game.BOUNDS2;
					}
				}
				// on left bound
				else if (this.backgroundX > Game.BOUNDS1) {
					System.out.println(">> left bound");
					this.moveBG = false;
					this.player.setDX(-this.player.getSpeed());
				}
				// on right bound
				else if (this.backgroundX < Game.BOUNDS2) {
					System.out.println(">> right bound");
					if (this.player.getXPos() > Game.WINDOW_CENTER) {
						this.moveBG = false;
						this.player.setDX(-this.player.getSpeed());
					} else {
						this.moveBG = true;
						this.player.setXPos(Game.WINDOW_CENTER);
						this.backgroundX = Game.BOUNDS2;
					}
				}
				// on lower bound
				else if (this.backgroundY < Game.BOUNDS2) {
					System.out.println(">> lower bound");
					this.moveBG = true;
					if (this.backgroundX > Game.BOUNDS1 && this.backgroundY < Game.BOUNDS2) {
						this.moveBG = false;
					}
				}
				// on upper bound
				else if (this.backgroundY > Game.BOUNDS1) {
					System.out.println(">> upper bound");
					this.moveBG = true;
					if (this.backgroundX > Game.BOUNDS1 && this.backgroundY > Game.BOUNDS1) {
						this.moveBG = false;
					}
				}
			}
		}




		if (key == KeyCode.S || key == KeyCode.DOWN) {
//			this.player.setDY(this.player.getSpeed());
			if (this.withinBounds || this.moveBG) {
				// --
				if (cornerBound()) this.moveBG = false;
				// --
				if (this.withinBounds) {
					this.player.setXPos(Game.WINDOW_CENTER);
					this.player.setYPos(Game.WINDOW_CENTER);
				}

				this.backgroundY += -this.player.getSpeed();
				for (Food food: this.foods) {
					food.setYPos(food.getYPos() - this.player.getSpeed());
				}
				for (Item item: this.items) {
					item.setYPos(item.getYPos() - this.player.getSpeed());
				}
				for (Enemy enemy: this.enemies) {
					enemy.setYPos(enemy.getYPos() - this.player.getSpeed());
				}

			// WINDOW is on the edge of bounds of MAP
			} else {
				// on upper left bound
				if (this.backgroundX > Game.BOUNDS1 && this.backgroundY > Game.BOUNDS1) {
					System.out.println(">> upper left bound");
					if (this.player.getYPos() < Game.WINDOW_CENTER) {
						this.moveBG = false;
						this.player.setDY(this.player.getSpeed());
					}
					else {
						this.moveBG = true;
						this.player.setYPos(Game.WINDOW_CENTER);
						this.backgroundY = Game.BOUNDS1;
					}
				}
				// on lower left bound
				else if (this.backgroundX > Game.BOUNDS1 && this.backgroundY < Game.BOUNDS2) {
					System.out.println(">> lower left bound");
					this.moveBG = false;
					this.player.setDY(this.player.getSpeed());
				}
				// on upper right bound
				else if (this.backgroundX < Game.BOUNDS2 && this.backgroundY > Game.BOUNDS1) {
					System.out.println(">> upper right bound");
					if (this.player.getYPos() < Game.WINDOW_CENTER) {
						this.moveBG = false;
						this.player.setDY(this.player.getSpeed());
					} else {
						this.moveBG = true;
						this.player.setYPos(Game.WINDOW_CENTER);
						this.backgroundY = Game.BOUNDS1;
					}
				}
				// on lower right bound
				else if (this.backgroundX < Game.BOUNDS2 && this.backgroundY < Game.BOUNDS2) {
					System.out.println(">> lower right bound");
					this.moveBG = false;
					this.player.setDY(this.player.getSpeed());
				}
				// on left bound
				else if (this.backgroundX > Game.BOUNDS1) {
					System.out.println(">> left bound");
					this.moveBG = true;
					// lower left corner reached
					if (this.backgroundX > Game.BOUNDS1 && this.backgroundY < Game.BOUNDS2) {
						this.moveBG = false;
					}
				}
				// on right bound
				else if (this.backgroundX < Game.BOUNDS2) {
					System.out.println(">> right bound");
					this.moveBG = true;
					// lower right corner reached
					if (this.backgroundX < Game.BOUNDS2 && this.backgroundY < Game.BOUNDS2) {
						this.moveBG = false;
					}
				}
				// on lower bound
				else if (this.backgroundY < Game.BOUNDS2) {
					System.out.println(">> lower bound");
					this.moveBG = false;
					this.player.setDY(this.player.getSpeed());
				}
				// on upper bound
				else if (this.backgroundY > Game.BOUNDS1) {
					System.out.println(">> upper bound");
					if (this.player.getYPos() < Game.WINDOW_CENTER) {
						this.moveBG = false;
						this.player.setDY(this.player.getSpeed());
					} else {
						this.moveBG = true;
						this.player.setYPos(Game.WINDOW_CENTER);
						this.backgroundY = Game.BOUNDS1;
					}
				}
			}
		}




		if (key == KeyCode.D || key == KeyCode.RIGHT) {
//			this.player.setDX(this.player.getSpeed());
			if (this.withinBounds || this.moveBG) {
				// --
				if (cornerBound()) this.moveBG = false;
				// --
				this.backgroundX += -this.player.getSpeed();
				for (Food food: this.foods) {
					food.setXPos(food.getXPos() - this.player.getSpeed());
				}
				for (Item item: this.items) {
					item.setXPos(item.getXPos() - this.player.getSpeed());
				}
				for (Enemy enemy: this.enemies) {
					enemy.setXPos(enemy.getXPos() - this.player.getSpeed());
				}

			// WINDOW is on the edge of bounds of MAP
			} else {
				// on upper left bound
				if (this.backgroundX > Game.BOUNDS1 && this.backgroundY > Game.BOUNDS1) {
					System.out.println(">> upper left bound");
					if (this.player.getXPos() < Game.WINDOW_CENTER) {
						this.moveBG = false;
						this.player.setDX(this.player.getSpeed());
					} else {
						this.moveBG = true;
						this.backgroundX = Game.BOUNDS1;
					}
				}
				// on lower left bound
				else if (this.backgroundX > Game.BOUNDS1 && this.backgroundY < Game.BOUNDS2) {
					System.out.println(">> lower left bound");
					if (this.player.getXPos() < Game.WINDOW_CENTER) {
						this.moveBG = false;
						this.player.setDX(this.player.getSpeed());
					} else {
						this.moveBG = true;
						this.player.setXPos(Game.WINDOW_CENTER);
						this.backgroundX = Game.BOUNDS1;
					}
				}
				// on upper right bound
				else if (this.backgroundX < Game.BOUNDS2 && this.backgroundY > Game.BOUNDS1) {
					System.out.println(">> upper right bound");
					this.moveBG = false;
					this.player.setDX(this.player.getSpeed());
				}
				// on lower right bound
				else if (this.backgroundX < Game.BOUNDS2 && this.backgroundY < Game.BOUNDS2) {
					System.out.println(">> lower right bound");
					this.moveBG = false;
					this.player.setDX(this.player.getSpeed());
				}
				// on left bound
				else if (this.backgroundX > Game.BOUNDS1) {
					System.out.println(">> left bound");
					if (this.player.getXPos() < Game.WINDOW_CENTER) {
						this.moveBG = false;
						this.player.setDX(this.player.getSpeed());
					} else {
						this.moveBG = true;
						this.player.setXPos(Game.WINDOW_CENTER);
						this.backgroundX = Game.BOUNDS1;
					}
				}
				// on right bound
				else if (this.backgroundX < Game.BOUNDS2) {
					System.out.println(">> right bound");
					this.moveBG = false;
					this.player.setDX(this.player.getSpeed());
				}
				// on lower bound
				else if (this.backgroundY < Game.BOUNDS2) {
					System.out.println(">> lower bound");
					this.moveBG = true;
					// stop moving bg when corner bounds reached
					if (this.backgroundX < Game.BOUNDS2 && this.backgroundY < Game.BOUNDS2) {
						this.moveBG = false;
					}
				}
				// on upper bound
				else if (this.backgroundY > Game.BOUNDS1) {
					System.out.println(">> upper bound");
					this.moveBG = true;
					if (this.backgroundX < Game.BOUNDS2 && this.backgroundY > Game.BOUNDS1) {
						this.moveBG = false;
					}
				}
			}
		}

		System.out.println(key + " key pressed. ");
		System.out.println("xPos: " + this.player.getXPos() + "; yPos: " + this.player.getYPos());
		System.out.println("bg xPos: " + this.backgroundX + "; bg yPos: " + this.backgroundY);
		System.out.println("moveBG: " + this.moveBG + "; withinBounds: " + this.withinBounds);
		System.out.println("");
	}




	// method that will stop the fish's movement
	private void stopMyFish(KeyCode code) {
		this.player.setDX(0);
		this.player.setDY(0);
	}





	// FOR STATUS BAR 
	private void drawScore() {
		this.gc.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
		this.gc.setFill(Color.YELLOW);
		this.gc.fillText("Current Size: ", 20, 35);

		this.gc.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
		this.gc.setFill(Color.WHITE);
		this.gc.fillText(player.getSize(), 170, 35);

		this.gc.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
		this.gc.setFill(Color.YELLOW);
		this.gc.fillText("Enemy eaten: ", 230, 35);

		this.gc.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
		this.gc.setFill(Color.WHITE);
		this.gc.fillText(player.getFishEaten(), 390, 35);

		this.gc.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
		this.gc.setFill(Color.YELLOW);
		this.gc.fillText("Food eaten: ", 450, 35);

		this.gc.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
		this.gc.setFill(Color.WHITE);
		this.gc.fillText(player.getFoodEaten(), 590, 35);

		this.gc.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
		this.gc.setFill(Color.YELLOW);
		this.gc.fillText("Time: ", 650, 35);

		this.gc.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        this.gc.setFill(Color.WHITE);
        this.gc.fillText(String.valueOf(this.timeAlive), 740, 35);

	}


	// FOR GAME OVER SCREEN ------
	private void drawGameOver() {
		this.gc.setFont(Font.font("Verdana", FontWeight.BOLD, 70));
		this.gc.setFill(Color.WHITE);
		this.gc.fillText("GAME OVER!", 150, 300);

		this.gc.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
		this.gc.setFill(Color.WHITE);
		this.gc.fillText("Time Alive: ", 200,  390);

		this.gc.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
		this.gc.setFill(Color.WHITE);
		this.gc.fillText(String.valueOf(this.timeAlive) + " seconds", 470,  390);

		this.gc.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
		this.gc.setFill(Color.WHITE);
		this.gc.fillText("Current Size: ", 200,  420);

		this.gc.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
		this.gc.setFill(Color.WHITE);
		this.gc.fillText(player.getSize(), 470,  420);

		this.gc.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
		this.gc.setFill(Color.WHITE);
		this.gc.fillText("Enemy eaten: ", 200,  450);

		this.gc.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
		this.gc.setFill(Color.WHITE);
		this.gc.fillText(player.getFishEaten(), 470,  450);

		this.gc.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
		this.gc.setFill(Color.WHITE);
		this.gc.fillText("Food eaten: ", 200,  480);

		this.gc.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
		this.gc.setFill(Color.WHITE);
		this.gc.fillText(player.getFoodEaten(), 470,  480);

	}
	// --------------------

}

package fishingGame;

import javafx.scene.image.Image;

// doubles the speed of the player for 5 seconds

public class Boost extends Item {
	public final static Image BOOST_IMG = new Image("images/speedboost.png", Item.ITEM_DIAM, Item.ITEM_DIAM, false, false);
	public final static int BOOST_TIME = 5;

	public Boost(int xPos, int yPos) {
		super(xPos, yPos);
		this.loadImage(BOOST_IMG);
	}




	// speed up player when it collides with the boost power-up
	@Override
	void checkCollision(Player player) {
		if (this.collidesWith(player)) {
			player.speedUp();
			this.vanish();
		}
	}

}

package fishingGame;

import javafx.scene.image.Image;

// provides immunity for the blob for 5 seconds

public class Shield extends Item {
	public final static Image SHIELD_IMG = new Image("images/shield.png", Item.ITEM_DIAM, Item.ITEM_DIAM, false, false);
	public final static int SHIELD_TIME = 5;

	public Shield(int xPos, int yPos) {
		super(xPos, yPos);
		this.loadImage(SHIELD_IMG);
	}




	// provides immunity to player when it collides with the shield power-up
	@Override
	void checkCollision(Player player) {
		if (this.collidesWith(player)) {
			player.shieldUp();
			this.vanish();
		}
	}

}

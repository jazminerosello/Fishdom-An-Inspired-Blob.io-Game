/***************
 * This class food contains the static integers that are needed. In its constructors the xPos and yPos being passed in the parameter are
 * stored or being passed too to super since this class food extends the class Sprite then loadImage will be called to load the image of the food i
 * food to the stage. There is also a method called checkCollision which checks if fish is being collided with the food and if it so then prompt message
 * will be printed out to console then we call the method of the fish which is the eatFood() then call the vanish() method of the food which
 * simply sets the visibility of the food as false
 *
 * @author Jazmine Rosello and Arawela Delmo
* @created_date 2022-26-12 18:34
 *********/

package fishingGame;

import javafx.scene.image.Image;

public class Food extends Sprite {

	public final static int FOOD_DIAM = 20;
	public final static int FOOD_GIVES = 10; // will help Fish that eats the food increase by the specified number
	public final static Image FOOD_IMG = new Image("images/pearl.png", Food.FOOD_DIAM, Food.FOOD_DIAM, false, false);


	public Food(int xPos, int yPos) {
		super(xPos, yPos, Food.FOOD_DIAM, Food.FOOD_DIAM);
		this.loadImage(FOOD_IMG); // load image of the food to be displayed to the scene or stage
	}


	// check collision to fish
	void checkCollision(Fish fish) {
		if (this.collidesWith(fish)) {
			System.out.println("== Food was eaten ==");

			fish.eatFood(); // to notify and also let the fish class knows that it eats food
			this.vanish();
		}
	}
}

package fishingGame;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

// represents all objects in the game (i.e. Player, Enemy, Power-ups)

public class Sprite {
	private Image img;
	// NOTE: xPos and yPos indicates the pos of the Sprite
	// while dx and dy is what you add to xPos and yPos to change its pos
	protected double xPos, yPos, dx, dy;
	private double width, height; // used in checking for collision (comes from the width and height of the img)
	private boolean visible;


	public Sprite(int xPos, int yPos, int width, int height) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.visible = true; // used later to indicate whether something has been eaten or not
	}



	private Rectangle2D getBounds() {
		return new Rectangle2D(this.xPos, this.yPos, this.width, this.height);
	}



	// method that checks collision between two Sprites
	protected boolean collidesWith(Sprite rect2) {
		Rectangle2D rectangle1 = this.getBounds();
		Rectangle2D rectangle2 = rect2.getBounds();

		return rectangle1.intersects(rectangle2);
	}



	// set the Sprite's width and height based on the object's size
	private void setSize() {
		this.width = this.img.getWidth();
		this.height = this.img.getHeight();
	}



	// method to see the object's image
	// NOTE: use try catch to catch the possibility that an invalid img is passed
	protected void loadImage(Image img) {
		try {
			this.img = img;
			this.setSize();
		} catch(Exception e){}
	}



	// method to set the image at a specific place
	void render(GraphicsContext gc) {
		gc.drawImage(this.img, this.xPos, this.yPos);
	}



	// getters ---------------
	public Image getImage() {
		return this.img;
	}

	public double getXPos() {
		return this.xPos;
	}

	public double getYPos() {
		return this.yPos;
	}

	public double getWidth() {
		return this.width;
	}

	public double getHeight() {
		return this.height;
	}

	public boolean isVisible() {
		return this.visible;
	}
	// -----------------------

	// setters ---------------
	public void setDX(double val) {
		this.dx = val;
	}

	public void setDY(double val) {
		this.dy = val;
	}

	public void setXPos(double val) {
		this.xPos = val;
	}

	public void setYPos(double val) {
		this.yPos = val;
	}

	public void vanish() {
		this.visible = false;
	}
	// -----------------------
}

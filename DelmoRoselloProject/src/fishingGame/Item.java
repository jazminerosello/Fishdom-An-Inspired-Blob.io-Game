package fishingGame;

public abstract class Item extends Sprite {
	private long startSpawn;

	// Power-ups appear every 10-second intervals;
	public final static int SPAWN_INTERVAL = 10;
	// disappear after 5 seconds if uncollected
	public final static int VISIBLE_TIME = 5;

	public final static int ITEM_DIAM = 60;

	public Item(int xPos, int yPos) {
		super(xPos, yPos, Item.ITEM_DIAM, Item.ITEM_DIAM);
		this.startSpawn = System.nanoTime();
	}


	abstract void checkCollision(Player player);


	// getters -----
	public long getStartSpawn() {
		return this.startSpawn;
	}
	// -------------

	// setters -----
	public void setStartSpawn(long nanoTime) {
		this.startSpawn = nanoTime;
	}
	// -------------
}

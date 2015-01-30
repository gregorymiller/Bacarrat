package baccarat.wager;

public class Wager {
	private int wager;
	private int x;
	private int y;

	public Wager() {
		this.wager = 0;
		this.x = 0;
		this.y = 0;
	}

	public void increaseWager(int amount) {
		wager += amount;
	}

	public int getWager() {
		return wager;
	}

	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getXLocation() {
		return x;
	}

	public int getYLocation() {
		return y;
	}

	public void clearWager() {
		wager = 0;
	}
}

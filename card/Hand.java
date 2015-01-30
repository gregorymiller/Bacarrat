package baccarat.card;

import java.util.ArrayList;

import android.graphics.Point;

public class Hand {
	private ArrayList<Card> hand;
	private Point location;

	public Hand() {
		hand = new ArrayList<Card>();
	}

	public void addCard(Card card) {
		hand.add(card);
	}

	public void clearHand() {
		hand.clear();
	}

	public int getHandValue() {
		int value = 0;

		for (Card card : hand) {
			value += card.getScoreValue();
		}

		// If hand value is larger than 10 mod 10 from it to get the true score
		if (value >= 10)
			value = value % 10;

		return value;
	}

	public int getLastCardValue() {
		return hand.get(hand.size() - 1).getScoreValue();
	}

	public void setLocation(Point point) {
		location = point;
	}

	public Point getLocation() {
		return location;
	}

	public int size() {
		return hand.size();
	}

	public Card get(int index) {
		return hand.get(index);
	}
}

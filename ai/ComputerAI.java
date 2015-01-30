package baccarat.ai;

import java.util.Random;

import baccarat.wager.Wager;

public class ComputerAI {
	private Wager wager;
	private int balance;
	private String hand;
	private int hardBetCounter;
	private int screenW;
	private int screenH;

	public ComputerAI() {
		this.balance = 50;
		this.wager = new Wager();
		this.hand = "None";
		this.hardBetCounter = 0;
		this.screenW = 0;
		this.screenH = 0;
	}

	public void makeBet(boolean easy) {
		if (easy) {
			easyBet();
		} else {
			hardBet();
		}
	}

	private void easyBet() {
		Random rand = new Random();

		// Pick a random hand to bet on then pick a random
		// amount of units to bet
		int randomHand = rand.nextInt((3 - 1) + 1) + 1;
		int randomBet = rand.nextInt((5 - 1) + 1) + 1;
		int bet = 5 * randomBet;

		// If the bet is more than the computer's balance
		// bet everything the computer has
		if (bet >= balance) {
			bet = balance;
		}

		wager.clearWager();

		// Depending on the hand set the information for the user
		// and displaying the chips correctly
		if (randomHand == 1) {
			hand = "Tie";
			wager.setLocation((int) (screenW * 0.63), (int) (screenH * 0.51));
			wager.increaseWager(bet);
		} else if (randomHand == 2) {
			hand = "Banker";
			wager.setLocation((int) (screenW * 0.63), (int) (screenH * 0.59));
			wager.increaseWager(bet);
		} else if (randomHand == 3) {
			hand = "Player";
			wager.setLocation((int) (screenW * 0.63), (int) (screenH * 0.68));
			wager.increaseWager(bet);
		}
	}

	private void hardBet() {
		hand = "Banker";
		wager.setLocation((int) (screenW * 0.63), (int) (screenH * 0.59));

		// Bet based on the 1, 3, 2, 6 system
		// If they do not have enough bet everything they have
		if (hardBetCounter == 0) {
			wager.clearWager();

			if (5 <= balance) {
				wager.increaseWager(5);
			}
		} else if (hardBetCounter == 1) {
			if (15 <= balance) {
				wager.increaseWager(10);
			} else {
				wager.clearWager();
				wager.increaseWager(balance);
			}
		} else if (hardBetCounter == 2) {
			wager.clearWager();

			if (10 <= balance) {
				wager.increaseWager(10);
			} else {
				wager.clearWager();
				wager.increaseWager(balance);
			}
		} else if (hardBetCounter == 3) {
			if (30 <= balance) {
				wager.increaseWager(20);
			} else {
				wager.clearWager();
				wager.increaseWager(balance);
			}
		}

		hardBetCounter++;

		if (hardBetCounter >= 4) {
			hardBetCounter = 0;
		}
	}

	public void checkOutcome(int player, int banker) {
		int playerDiff = 9 - player;
		int dealerDiff = 9 - banker;
		int payout = 0;
		boolean win = true;

		// Tie
		if ((playerDiff == dealerDiff) && hand.equals("Tie")) {
			payout = (wager.getWager() * 9);

		}
		// Player
		else if ((playerDiff < dealerDiff) && hand.equals("Player")) {
			payout = (wager.getWager() * 2);
		}
		// Dealer
		else if ((dealerDiff < playerDiff) && hand.equals("Banker")) {
			payout = (wager.getWager() * 2);
		}
		// Lose
		else {
			win = false;
			payout = wager.getWager();
		}

		if (win) {
			balance += payout;
		} else {
			balance -= payout;
		}
	}

	public void setScreenWidth(int width) {
		screenW = width;
	}

	public void setScreenHeight(int height) {
		screenH = height;
	}

	public int getBalance() {
		return balance;
	}

	public String getHand() {
		return hand;
	}

	public int getWager() {
		return wager.getWager();
	}

	public int getXLocation() {
		return wager.getXLocation();
	}

	public int getYLocation() {
		return wager.getYLocation();
	}
}

package baccarat.ai;

import java.util.HashMap;

import baccarat.utilities.BankerMove;

public class BankerAI {
	private HashMap<String, BankerMove[]> bankerRules;

	public BankerAI() {
		this.bankerRules = new HashMap<String, BankerMove[]>();

		populateRules();
	}

	public void populateRules() {
		// Populate play rules for dealer hand values of 4-6
		
		/* 
		 * Player third card
		 * 	0  1  2  3  4  5  6  7  8  9
		 * 6	S, S, S, S, S, S, H, H, S, S
		 * 5	S, S, S, S, H, H, H, H, S, S
		 * 4	S, S, H, H, H, H, H, H, S, S
		 */
		for (int i = 0; i < 3; i++) {
			BankerMove[] tempArray = new BankerMove[10];

			for (int j = 0; j < (i * 2) + 2; j++) {
				tempArray[j] = BankerMove.STAY;
			}

			for (int k = (i * 2) + 2; k < 8; k++) {
				tempArray[k] = BankerMove.HIT;
			}

			tempArray[8] = BankerMove.STAY;
			tempArray[9] = BankerMove.STAY;

			bankerRules.put("" + (i + 4), tempArray.clone());
		}
	}
	
	public HashMap<String, BankerMove[]> getRules() {
		return bankerRules;
	}

	public BankerMove getDealerMove(int playerLastCard, int dealerValue) {
		// Gets the dealer move depending on the player third card and the
		// dealer hand value

		if (dealerValue < 3) {
			return BankerMove.HIT;
		} else if (dealerValue == 7) {
			return BankerMove.STAY;
		} else if (dealerValue == 3) {
			if (playerLastCard == 8)
				return BankerMove.STAY;
			else
				return BankerMove.HIT;
		} else {
			return bankerRules.get("" + dealerValue)[playerLastCard];
		}
	}
}

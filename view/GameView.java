package baccarat.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import baccarat.ai.BankerAI;
import baccarat.ai.ComputerAI;
import baccarat.card.ACard;
import baccarat.card.Card;
import baccarat.card.Hand;
import baccarat.utilities.BankerMove;
import baccarat.utilities.SoundManager;
import baccarat.wager.Wager;

import com.example.baccarat.R;

public class GameView extends View {
	private Bitmap gameBackground;
	private Bitmap chip5;
	private Bitmap chip10;
	private Bitmap chip25;
	private Bitmap scaledChip5;
	private Bitmap scaledChip10;
	private Bitmap scaledChip25;
	private Bitmap cardBack;
	private Bitmap dealUp;
	private Bitmap dealDown;
	private Bitmap chooseHandUp;
	private Bitmap chooseHandDown;

	private int screenW;
	private int screenH;
	private int scaledCardW;
	private int scaledCardH;
	private int balance;
	private int winAmount;

	private boolean thirdHit;
	private boolean handOver;
	public boolean soundOn;
	public boolean easy;
	private boolean dealPressed;
	private boolean chooseHandPressed;

	private Hand player;
	private Hand banker;

	private List<ACard> inFlight = new ArrayList<ACard>();
	private List<Card> shoe = new ArrayList<Card>();

	private Paint whitePaint;
	private Paint smallWhitePaint;
	private Context myContext;
	private Rect screen;
	private float scale;
	private BankerAI bankerAI;
	private String handText;
	private Wager wager;
	private SoundManager soundManager;
	private ComputerAI computerAI;

	public GameView(Context context) {
		super(context);
		myContext = context;
		scale = myContext.getResources().getDisplayMetrics().density;

		gameBackground = BitmapFactory.decodeResource(getResources(),
				R.drawable.game);

		whitePaint = new Paint();
		whitePaint.setAntiAlias(true);
		whitePaint.setColor(Color.WHITE);
		whitePaint.setStyle(Paint.Style.FILL_AND_STROKE);
		whitePaint.setTextAlign(Paint.Align.LEFT);
		whitePaint.setTextSize(scale * 40);

		smallWhitePaint = new Paint();
		smallWhitePaint.setAntiAlias(true);
		smallWhitePaint.setColor(Color.WHITE);
		smallWhitePaint.setStyle(Paint.Style.FILL_AND_STROKE);
		smallWhitePaint.setTextAlign(Paint.Align.LEFT);
		smallWhitePaint.setTextSize(18);

		balance = 50;
		winAmount = 0;

		thirdHit = false;
		handOver = false;
		dealPressed = false;
		chooseHandPressed = false;

		handText = "None";

		bankerAI = new BankerAI();
		player = new Hand();
		banker = new Hand();
		wager = new Wager();
		soundManager = new SoundManager(myContext);
		computerAI = new ComputerAI();
	}

	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		screenW = w;
		screenH = h;

		// Sets a rectangle for the screen height and width
		// This is for placing the background
		screen = new Rect(0, 0, screenW, screenH);

		// Scales the card for the screen height and width
		Bitmap tempBitmap = BitmapFactory.decodeResource(
				myContext.getResources(), R.drawable.card_back);
		scaledCardW = (int) (screenW / 10);
		scaledCardH = (int) (scaledCardW * 1.28);
		cardBack = Bitmap.createScaledBitmap(tempBitmap, scaledCardW,
				scaledCardH, false);

		// Defines the player and dealer location based on the screen size
		player.setLocation(new Point(60, (int) (screenH * 0.71) - scaledCardH));
		banker.setLocation(new Point((int) (screenW * 0.75),
				(int) (screenH * 0.75) - scaledCardH));

		// Create all the cards in the deck, all the chips, and then the buttons
		initCards();
		initChips();
		initButtons();

		computerAI.setScreenWidth(screenW);
		computerAI.setScreenHeight(screenH);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// Draws the background
		canvas.drawBitmap(gameBackground, null, screen, null);

		// Draw the computer player information
		canvas.drawText("Player2", 0, 15, smallWhitePaint);
		canvas.drawText("$" + Integer.toString(computerAI.getBalance()), 0, 35,
				smallWhitePaint);
		canvas.drawText("Betting on: " + computerAI.getHand(), 0, 55,
				smallWhitePaint);
		if (easy) {
			canvas.drawText("Difficulty: Easy", 0, 75, smallWhitePaint);
		} else {
			canvas.drawText("Difficulty: Hard", 0, 75, smallWhitePaint);
		}

		// Draws the balance, the wager, and the win on the screen
		canvas.drawText("$" + Integer.toString(balance), 10,
				(int) (screenH * 0.94), whitePaint);
		canvas.drawText("$" + Integer.toString(wager.getWager()),
				(int) (screenW * 0.5) - (whitePaint.getTextSize() / 2),
				(int) (screenH * 0.94), whitePaint);
		canvas.drawText("$" + Integer.toString(winAmount),
				(int) (screenW * 0.93) - (whitePaint.getTextSize() / 2),
				(int) (screenH * 0.94), whitePaint);

		// Draw player and dealer scores
		canvas.drawText(Integer.toString(player.getHandValue()),
				(int) (screenW * 0.1), (int) (screenH * 0.80), whitePaint);
		canvas.drawText(Integer.toString(banker.getHandValue()),
				(int) (screenW * 0.83), (int) (screenH * 0.80), whitePaint);

		// Draw the card face down where the shoe is
		canvas.drawBitmap(cardBack,
				(float) (screenW * 0.98) - cardBack.getWidth() - 10,
				(float) (screenH * 0.15) - (cardBack.getHeight() / 2), null);

		// Draw the chips on the screen
		canvas.drawBitmap(chip5, (int) (screenW * 0.17),
				(int) (screenH * 0.84), null);
		canvas.drawBitmap(chip10, (int) (screenW * 0.17) + 120,
				(int) (screenH * 0.84), null);
		canvas.drawBitmap(chip25, (int) (screenW * 0.17) + 235,
				(int) (screenH * 0.84), null);

		// Draw the deal and choose the hand buttons
		if (!dealPressed) {
			canvas.drawBitmap(dealUp, (int) (screenW * 0.58),
					(int) (screenH * 0.88), null);
		} else {
			canvas.drawBitmap(dealDown, (int) (screenW * 0.58),
					(int) (screenH * 0.88), null);
		}

		if (!chooseHandPressed) {
			canvas.drawBitmap(chooseHandUp, (int) (screenW * 0.66),
					(int) (screenH * 0.88), null);
		} else {
			canvas.drawBitmap(chooseHandDown, (int) (screenW * 0.66),
					(int) (screenH * 0.88), null);
		}

		// Draw text to the show which hand the user is betting on
		canvas.drawText("Betting on: " + handText, (int) (screenW * 0.35)
				- (whitePaint.getTextSize() / 2), (int) (screenH * 0.1),
				whitePaint);

		renderChips(canvas);

		// Draws the cards in the player's hand
		for (int i = 0; i < player.size(); i++) {
			canvas.drawBitmap(player.get(i).getBitmap(), (i * 40) + 60,
					(float) (screenH * 0.71) - scaledCardH, null);
		}

		// Draws the cards in the dealer's hand
		for (int i = 0; i < banker.size(); i++) {
			canvas.drawBitmap(banker.get(i).getBitmap(), (float) ((i * 40)
					+ (screenW * 0.75) + 30), (float) (screenH * 0.71)
					- scaledCardH, null);
		}

		// If a card is in flight then draw it and when it arrives
		// at the location it sets the value as null
		for (int i = 0; i < inFlight.size(); i++) {
			if (inFlight.get(i) != null && !inFlight.get(i).hasArrived()) {
				inFlight.get(i).draw(canvas);
			} else if (inFlight.get(i) != null && inFlight.get(i).hasArrived()) {
				inFlight.set(i, null);
			}
		}

		// If there has not been a third card hit then check for one
		if (!thirdHit && !handOver) {
			checkForThirdCard();
		}
		// If the hand is done and all cards are done moving check the outcome
		else if (handOver && isAllNulls(inFlight)) {
			checkOutcome();
			computerAI.checkOutcome(player.getHandValue(),
					banker.getHandValue());
			handOver = false;

			if (computerAI.getBalance() <= 0) {
				showWinGameDialog();
			}
		}

		invalidate();
	}

	public boolean isAllNulls(Iterable<?> array) {
		// Go through the array if something is not null return false
		for (Object element : array) {
			if (element != null) {
				return false;
			}
		}
		return true;
	}

	public boolean onTouchEvent(MotionEvent event) {
		int eventAction = event.getAction();
		int X = (int) event.getX();
		int Y = (int) event.getY();

		switch (eventAction) {
		case MotionEvent.ACTION_DOWN:
			// Touching the deal button
			if ((X > (int) (screenW * 0.58) && X < (int) (screenW * 0.58)
					+ dealUp.getWidth())
					&& (Y > (int) (screenH * 0.88) && Y < (int) (screenH * 0.88)
							+ dealUp.getHeight())) {
				// If there is no bet amount do not allow the player to play
				if (wager.getWager() != 0) {
					computerAI.makeBet(easy);

					player.clearHand();
					banker.clearHand();

					winAmount = 0;

					thirdHit = false;
					handOver = false;

					dealCards();

					dealPressed = true;
				}
			}
			// Touching the choose hand button
			else if ((X > (int) (screenW * 0.66) && X < (int) (screenW * 0.66)
					+ chooseHandUp.getWidth())
					&& (Y > (int) (screenH * 0.88) && Y < (int) (screenH * 0.88)
							+ chooseHandUp.getHeight())) {
				showChooseHandDialog();

				chooseHandPressed = true;
			}
			// Touch the 5 chip
			else if ((X > (int) (screenW * 0.17) && X < (int) (screenW * 0.17)
					+ chip5.getWidth())
					&& (Y > (int) (screenH * 0.84) && Y < (int) (screenH * 0.84)
							+ chip5.getHeight())) {
				// If there is no hand to bet on do not increase the wager
				if (!handText.equals("None")
						&& (balance - wager.getWager()) >= 5) {
					wager.increaseWager(5);

					if (soundOn) {
						soundManager.playPlaceChip();
					}
				}
			}
			// Touch the 10 chip
			else if ((X > ((int) (screenW * 0.17) + 120) && X < ((int) (screenW * 0.17) + 120)
					+ chip5.getWidth())
					&& (Y > (int) (screenH * 0.84) && Y < (int) (screenH * 0.84)
							+ chip5.getHeight())) {
				// If there is no hand to bet on do not increase the wager
				if (!handText.equals("None")
						&& (balance - wager.getWager()) >= 10) {
					wager.increaseWager(10);

					if (soundOn) {
						soundManager.playPlaceChip();
					}
				}
			}
			// Touch the 25 chip
			else if ((X > ((int) (screenW * 0.17) + 235) && X < ((int) (screenW * 0.17) + 235)
					+ chip5.getWidth())
					&& (Y > (int) (screenH * 0.84) && Y < (int) (screenH * 0.84)
							+ chip5.getHeight())) {
				// If there is no hand to bet on do not increase the wager
				if (!handText.equals("None")
						&& (balance - wager.getWager()) >= 25) {
					wager.increaseWager(25);

					if (soundOn) {
						soundManager.playPlaceChip();
					}
				}
			}
			// Touch chip area to clear bet
			else if ((X > ((int) (screenW * 0.29)) && X < ((int) (screenW * 0.75)))
					&& (Y > ((int) (screenH * 0.5)) && Y < ((int) (screenH * 0.75)))) {
				wager.clearWager();

				if (soundOn) {
					soundManager.playPlaceChip();
				}
			}
			break;

		case MotionEvent.ACTION_MOVE:
			break;

		case MotionEvent.ACTION_UP:
			dealPressed = false;
			chooseHandPressed = false;
			break;
		}

		invalidate();
		return true;
	}

	private void dealCards() {
		// Deals two cards to the player and the dealer
		// Wait a second between each card dealt
		for (int i = 0; i < 4; i++) {
			if (i < 2) {
				new CountDownTimer(i * 1000, 1000) {

					@Override
					public void onTick(long miliseconds) {
					}

					@Override
					public void onFinish() {
						dealCard(player.getLocation(), player);
					}
				}.start();
			} else {
				new CountDownTimer(i * 1000, 1000) {

					@Override
					public void onTick(long miliseconds) {
					}

					@Override
					public void onFinish() {
						dealCard(banker.getLocation(), banker);
					}
				}.start();
			}
		}
	}

	private void dealCard(Point location, Hand hand) {
		if (soundOn) {
			soundManager.playCardDeal();
		}

		// Resets the position of the shoe and draws a card otherwise the point
		// where it starts to draw is the ending location of the last card
		Point tempShoe = new Point((int) (screenW * 0.98) - cardBack.getWidth()
				- 10, (int) (screenH * 0.15) - (cardBack.getHeight() / 2));
		Card tempCard = shoe.remove(0);

		// If the shoe is empty shuffle it
		if (shoe.size() <= 10) {
			initCards();

			if (soundOn) {
				soundManager.playCardShuffle();
			}
		}

		// Puts the card in flight
		inFlight.add(new ACard(tempCard, tempShoe, location, hand));
	}

	private void checkForThirdCard() {
		// If the players do not have all their cards yet just return
		// Else mark that a third has been checked
		if (player.size() != 2 || banker.size() != 2) {
			return;
		} else {
			thirdHit = true;
			handOver = true;
		}

		int playerValue = player.getHandValue();
		int bankerValue = banker.getHandValue();

		// If either the player or the dealer has 8 or 9 just return because no
		// third card will be dealt
		if ((playerValue == 9 || playerValue == 8)
				|| (bankerValue == 9 || bankerValue == 8)) {
			return;
		}

		// If the player value is 5 or less the player must hit
		// Then depending on the dealer's hand and the third card dealt the
		// dealer either has to stay or hit
		if (playerValue <= 5) {
			int playerLastCard = shoe.get(0).getScoreValue();

			dealCard(player.getLocation(), player);

			BankerMove dealerAction = bankerAI.getDealerMove(playerLastCard,
					bankerValue);

			if (dealerAction == BankerMove.HIT) {
				dealCard(banker.getLocation(), banker);
			}

			return;
		}

		// If the player does not hit and the dealer has a hand value of 5 or
		// less it must hit
		if (bankerValue <= 5) {
			dealCard(banker.getLocation(), banker);

			return;
		}
	}

	private void checkOutcome() {
		int playerDiff = 9 - player.getHandValue();
		int dealerDiff = 9 - banker.getHandValue();
		int payout = 0;
		boolean win = true;

		// Tie
		if ((playerDiff == dealerDiff) && handText.equals("Tie")) {
			payout = (wager.getWager() * 9);

		}
		// Player
		else if ((playerDiff < dealerDiff) && handText.equals("Player")) {
			payout = (wager.getWager() * 2);
		}
		// Dealer
		else if ((dealerDiff < playerDiff) && handText.equals("Banker")) {
			payout = (wager.getWager() * 2);
		}
		// Lose
		else {
			win = false;
			payout = wager.getWager();
		}

		if (win) {
			winAmount = payout;
			balance += payout;

			if (soundOn) {
				soundManager.playWinHand();
			}
		} else {
			winAmount = payout * -1;
			balance -= payout;
		}

		if (balance <= 0) {
			if (soundOn) {
				soundManager.playLoseGame();
			}

			showLoseGameDialog();
		}
	}

	private void initChips() {
		// Initialize chips and their scaled versions
		chip5 = BitmapFactory.decodeResource(myContext.getResources(),
				R.drawable.chip5);
		chip10 = BitmapFactory.decodeResource(myContext.getResources(),
				R.drawable.chip10);
		chip25 = BitmapFactory.decodeResource(myContext.getResources(),
				R.drawable.chip25);

		Bitmap tempBitmap = BitmapFactory.decodeResource(
				myContext.getResources(), R.drawable.chip5);
		scaledChip5 = Bitmap.createScaledBitmap(tempBitmap, (screenW / 15),
				(screenH / 15), false);

		tempBitmap = BitmapFactory.decodeResource(myContext.getResources(),
				R.drawable.chip10);
		scaledChip10 = Bitmap.createScaledBitmap(tempBitmap, (screenW / 15),
				(screenH / 15), false);

		tempBitmap = BitmapFactory.decodeResource(myContext.getResources(),
				R.drawable.chip25);
		scaledChip25 = Bitmap.createScaledBitmap(tempBitmap, (screenW / 15),
				(screenH / 15), false);
	}

	private void showChooseHandDialog() {
		// Create a spinner for choosing which hand to bet on
		final Dialog chooseHandDialog = new Dialog(myContext);
		chooseHandDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		chooseHandDialog.setContentView(R.layout.choose_hand_dialog);

		final Spinner handSpinner = (Spinner) chooseHandDialog
				.findViewById(R.id.handSpinner);

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				myContext, R.array.hands, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		handSpinner.setAdapter(adapter);

		Button okButton = (Button) chooseHandDialog.findViewById(R.id.okButton);
		okButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				int tempPosition = (handSpinner.getSelectedItemPosition());

				// Depending which hand the user wants to bet on set the
				// position where the chips will be rendered
				if (tempPosition == 0) {
					handText = "Player";
					wager.setLocation((int) (screenW * 0.29),
							(int) (screenH * 0.68));
				} else if (tempPosition == 1) {
					handText = "Banker";
					wager.setLocation((int) (screenW * 0.29),
							(int) (screenH * 0.59));
				} else if (tempPosition == 2) {
					handText = "Tie";
					wager.setLocation((int) (screenW * 0.29),
							(int) (screenH * 0.51));
				}
				chooseHandDialog.dismiss();
			}
		});
		chooseHandDialog.show();
	}

	private void showLoseGameDialog() {
		final Dialog loseGameDialog = new Dialog(myContext);
		loseGameDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		loseGameDialog.setContentView(R.layout.lose_game_layout);

		Button okButton = (Button) loseGameDialog.findViewById(R.id.okButton);
		okButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				player.clearHand();
				banker.clearHand();

				winAmount = 0;
				balance = 1000;

				thirdHit = false;
				handOver = false;

				wager.clearWager();
				computerAI = new ComputerAI();

				handText = "None";

				loseGameDialog.dismiss();
			}
		});
		loseGameDialog.show();
	}

	private void showWinGameDialog() {
		final Dialog winGameDialog = new Dialog(myContext);
		winGameDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		winGameDialog.setContentView(R.layout.win_game_layout);

		Button okButton = (Button) winGameDialog.findViewById(R.id.okButton);
		okButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				player.clearHand();
				banker.clearHand();

				winAmount = 0;
				balance = 1000;

				thirdHit = false;
				handOver = false;

				wager.clearWager();
				computerAI = new ComputerAI();

				handText = "None";

				winGameDialog.dismiss();
			}
		});
		winGameDialog.show();
	}

	private void renderChips(Canvas canvas) {
		int playerAmount = wager.getWager();
		int computerAmount = computerAI.getWager();
		int xOffset = 0;

		// Place chips on the board with the least amount of chips possible
		while (playerAmount != 0) {
			if (playerAmount - 25 >= 0) {
				canvas.drawBitmap(scaledChip25, wager.getXLocation() + xOffset,
						wager.getYLocation(), null);
				playerAmount -= 25;
			} else if (playerAmount - 10 >= 0) {
				canvas.drawBitmap(scaledChip10, wager.getXLocation() + xOffset,
						wager.getYLocation(), null);
				playerAmount -= 10;
			} else if (playerAmount - 5 >= 0) {
				canvas.drawBitmap(scaledChip5, wager.getXLocation() + xOffset,
						wager.getYLocation(), null);
				playerAmount -= 5;
			}

			// Create an offset so the chips are not stacked on top of each other
			xOffset += 20;
		}

		xOffset = 0;

		while (computerAmount != 0) {
			if (computerAmount - 25 >= 0) {
				canvas.drawBitmap(scaledChip25, computerAI.getXLocation()
						+ xOffset, computerAI.getYLocation(), null);
				computerAmount -= 25;
			} else if (computerAmount - 10 >= 0) {
				canvas.drawBitmap(scaledChip10, computerAI.getXLocation()
						+ xOffset, computerAI.getYLocation(), null);
				computerAmount -= 10;
			} else if (computerAmount - 5 >= 0) {
				canvas.drawBitmap(scaledChip5, computerAI.getXLocation()
						+ xOffset, computerAI.getYLocation(), null);
				computerAmount -= 5;
			}

			xOffset += 20;
		}
	}

	private void initButtons() {
		Bitmap tempBitmap = BitmapFactory.decodeResource(
				myContext.getResources(), R.drawable.deal_up);
		dealUp = Bitmap.createScaledBitmap(tempBitmap, (screenW / 15),
				(screenH / 15), false);

		tempBitmap = BitmapFactory.decodeResource(myContext.getResources(),
				R.drawable.deal_down);
		dealDown = Bitmap.createScaledBitmap(tempBitmap, (screenW / 15),
				(screenH / 15), false);

		tempBitmap = BitmapFactory.decodeResource(myContext.getResources(),
				R.drawable.choose_bet_up);
		chooseHandUp = Bitmap.createScaledBitmap(tempBitmap, (screenW / 5),
				(screenH / 15), false);

		tempBitmap = BitmapFactory.decodeResource(myContext.getResources(),
				R.drawable.choose_bet_down);
		chooseHandDown = Bitmap.createScaledBitmap(tempBitmap, (screenW / 5),
				(screenH / 15), false);
	}

	private void initCards() {
		// Create the shoe with four decks
		for (int h = 0; h < 5; h++) {
			// Prof. Coleman's code for initializing the cards
			for (int i = 0; i < 4; i++) {
				for (int j = 102; j < 115; j++) {
					int tempId = j + (i * 100);
					Card tempCard = new Card(tempId);

					// pkgName is the java class package name
					String pkgName = myContext.getPackageName();

					// 1) getResources() or myContext.getResources() doesn't
					// matter
					// 2) nowhere is ".png" mentioned
					int resourceId = myContext.getResources().getIdentifier(
							"card" + tempId, "drawable", pkgName);

					// decodeResource apparently interprets resourceId
					Bitmap tempBitmap = BitmapFactory.decodeResource(
							myContext.getResources(), resourceId);
					scaledCardW = (int) (screenW / 10);
					scaledCardH = (int) (scaledCardW * 1.28);
					Bitmap scaledBitmap = Bitmap.createScaledBitmap(tempBitmap,
							scaledCardW, scaledCardH, false);
					tempCard.setBitmap(scaledBitmap);
					shoe.add(tempCard);
				}
			}
		}

		Collections.shuffle(shoe, new Random());
	}
}

package baccarat.utilities;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.example.baccarat.R;

public class SoundManager {
	private static SoundPool sounds;
	private static int cardDeal;
	private static int cardShuffle;
	private static int placeChip;
	private static int loseGame;
	private static int winGame;
	private static int winHand;
	private float volume;

	public SoundManager(Context context) {
		sounds = new SoundPool(8, AudioManager.STREAM_MUSIC, 0);
		cardDeal = sounds.load(context, R.raw.card_deal, 1);
		cardShuffle = sounds.load(context, R.raw.card_shuffle, 1);
		placeChip = sounds.load(context, R.raw.place_chip, 1);
		loseGame = sounds.load(context, R.raw.lose_game, 1);
		winGame = sounds.load(context, R.raw.win_game, 1);
		winHand = sounds.load(context, R.raw.win_hand, 1);
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		volume = (float) audioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);
	}

	public void playCardDeal() {
		sounds.play(cardDeal, volume, volume, 1, 0, 1);
	}

	public void playCardShuffle() {
		sounds.play(cardShuffle, volume, volume, 1, 0, 1);
	}

	public void playPlaceChip() {
		sounds.play(placeChip, volume, volume, 1, 0, 1);
	}

	public void playLoseGame() {
		sounds.play(loseGame, volume, volume, 1, 0, 1);
	}

	public void playWinGame() {
		sounds.play(winGame, volume, volume, 1, 0, 1);
	}

	public void playWinHand() {
		sounds.play(winHand, volume, volume, 1, 0, 1);
	}

}

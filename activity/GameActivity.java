package baccarat.activity;

import baccarat.view.GameView;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class GameActivity extends Activity {
	private boolean soundEnabled;
	private boolean easy;
	private SharedPreferences settings;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GameView gView = new GameView(this);
		gView.setKeepScreenOn(true);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(gView);

		// Load the settings so that the game behaves properly
		settings = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
		soundEnabled = settings.getBoolean("soundSetting", true);
		easy = settings.getBoolean("difficultySetting", true);
		gView.soundOn = soundEnabled;
		gView.easy = easy;
	}
}

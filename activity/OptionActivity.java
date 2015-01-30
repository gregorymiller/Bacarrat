package baccarat.activity;

import baccarat.view.OptionView;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class OptionActivity extends Activity {
	private SharedPreferences settings;
	private boolean soundEnabled;
	private boolean difficultyEnabled;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		OptionView gView = new OptionView(this);
		gView.setKeepScreenOn(true);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(gView);

		// Load the saved settings for the options screen to show the correct
		// information
		settings = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
		soundEnabled = settings.getBoolean("soundSetting", true);
		difficultyEnabled = settings.getBoolean("difficultySetting", true);
		gView.soundEnabled = soundEnabled;
		gView.difficultyEnabled = difficultyEnabled;
		gView.settings = settings;
	}
}

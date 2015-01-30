package baccarat.view;

import com.example.baccarat.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

public class OptionView extends View {
	private int screenW;
	private int screenH;
	private Bitmap soundOn;
	private Bitmap soundOff;
	private Bitmap easy;
	private Bitmap hard;
	public boolean soundEnabled;
	public boolean difficultyEnabled;
	private Paint blackPaint;
	public SharedPreferences settings;

	public OptionView(Context context) {
		super(context);

		soundOn = BitmapFactory.decodeResource(getResources(),
				R.drawable.sound_on);
		soundOff = BitmapFactory.decodeResource(getResources(),
				R.drawable.sound_off);
		easy = BitmapFactory.decodeResource(getResources(), R.drawable.easy);
		hard = BitmapFactory.decodeResource(getResources(), R.drawable.hard);

		blackPaint = new Paint();
		blackPaint.setAntiAlias(true);
		blackPaint.setColor(Color.BLACK);
		blackPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		blackPaint.setTextAlign(Paint.Align.LEFT);
		blackPaint.setTextSize(24);
	}

	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		screenW = w;
		screenH = h;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// Toggle the image based on if sound is enabled or not
		if (soundEnabled) {
			canvas.drawBitmap(soundOn, (screenW - soundOn.getWidth()) / 2,
					(int) (screenH * 0.3), null);
		} else {
			canvas.drawBitmap(soundOff, (screenW - soundOff.getWidth()) / 2,
					(int) (screenH * 0.3), null);
		}

		// Toggle the image based on what the difficulty is
		if (difficultyEnabled) {
			canvas.drawBitmap(easy, (screenW - easy.getWidth()) / 2,
					(int) (screenH * 0.5), null);
		} else {
			canvas.drawBitmap(hard, (screenW - hard.getWidth()) / 2,
					(int) (screenH * 0.5), null);
		}

		// Tell the user what difficulty does
		canvas.drawText(
				"Easy and hard difficulties are related to how the computer player will make its decisions",
				(int) (screenW * 0.15) - (blackPaint.getTextSize() / 2),
				(int) (screenH * 0.7), blackPaint);

		// Give the user basic instructions
		canvas.drawText(
				"Directions: Pick a hand to bet on, then place a bet, and hit the deal button",
				(int) (screenW * 0.15) - (blackPaint.getTextSize() / 2),
				(int) (screenH * 0.75), blackPaint);
	}

	public boolean onTouchEvent(MotionEvent event) {
		int eventaction = event.getAction();
		int X = (int) event.getX();
		int Y = (int) event.getY();

		switch (eventaction) {

		case MotionEvent.ACTION_DOWN:
			// Hitting the sound button and update the settings
			if ((X > (screenW - soundOn.getWidth()) / 2)
					&& (X < ((screenW - soundOn.getWidth()) / 2)
							+ soundOn.getWidth())
					&& (Y > (int) (screenH * 0.3))
					&& (Y < (int) (screenH * 0.3) + soundOn.getHeight())) {
				soundEnabled = !soundEnabled;
				saveSetting("soundSetting", soundEnabled);
			}
			// Hitting the difficulty button and update the settings
			else if ((X > (screenW - easy.getWidth()) / 2)
					&& (X < ((screenW - easy.getWidth()) / 2) + easy.getWidth())
					&& (Y > (int) (screenH * 0.5))
					&& (Y < (int) (screenH * 0.5) + easy.getHeight())) {
				difficultyEnabled = !difficultyEnabled;
				saveSetting("difficultySetting", difficultyEnabled);
			}
			break;

		case MotionEvent.ACTION_MOVE:
			break;

		case MotionEvent.ACTION_UP:
			break;
		}

		invalidate();

		return true;
	}

	public void saveSetting(String key, boolean value) {
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
}

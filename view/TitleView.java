package baccarat.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;
import baccarat.activity.GameActivity;
import baccarat.activity.OptionActivity;

import com.example.baccarat.R;

public class TitleView extends View {
	private Bitmap titleGraphic;
	private int screenW;
	private int screenH;
	private Bitmap playButtonUp;
	private Bitmap playButtonDown;
	private Bitmap optionButtonUp;
	private Bitmap optionButtonDown;
	private boolean playButtonPressed;
	private boolean optionButtonPressed;
	private Context myContext;

	public TitleView(Context context) {
		super(context);
		myContext = context;
		titleGraphic = BitmapFactory.decodeResource(getResources(),
				R.drawable.title_graphic);
		playButtonUp = BitmapFactory.decodeResource(getResources(),
				R.drawable.play_button_up);
		playButtonDown = BitmapFactory.decodeResource(getResources(),
				R.drawable.play_button_down);
		optionButtonUp = BitmapFactory.decodeResource(getResources(),
				R.drawable.option_button_up);
		optionButtonDown = BitmapFactory.decodeResource(getResources(),
				R.drawable.option_button_down);
	}

	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		screenW = w;
		screenH = h;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(titleGraphic,
				(screenW - titleGraphic.getWidth()) / 2, (int) (screenH * 0.2),
				null);

		// Toggles the image displayed when the play and the options buttons are
		// clicked
		if (!playButtonPressed) {
			canvas.drawBitmap(playButtonUp,
					(screenW - playButtonUp.getWidth()) / 2,
					(int) (screenH * 0.65), null);
		} else {
			canvas.drawBitmap(playButtonDown,
					(screenW - playButtonDown.getWidth()) / 2,
					(int) (screenH * 0.65), null);
		}

		if (!optionButtonPressed) {
			canvas.drawBitmap(optionButtonUp,
					(screenW - optionButtonUp.getWidth()) / 2,
					(int) (screenH * 0.8), null);
		} else {
			canvas.drawBitmap(optionButtonDown,
					(screenW - optionButtonDown.getWidth()) / 2,
					(int) (screenH * 0.8), null);
		}
	}

	public boolean onTouchEvent(MotionEvent event) {
		int eventaction = event.getAction();
		int X = (int) event.getX();
		int Y = (int) event.getY();

		switch (eventaction) {

		case MotionEvent.ACTION_DOWN:
			// Hitting the play button
			if ((X > (screenW - playButtonUp.getWidth()) / 2)
					&& (X < ((screenW - playButtonUp.getWidth()) / 2)
							+ playButtonUp.getWidth())
					&& (Y > (int) (screenH * 0.65))
					&& (Y < (int) (screenH * 0.65) + playButtonUp.getHeight())) {
				playButtonPressed = true;
			}
			// Hitting the option button
			else if ((X > (screenW - optionButtonUp.getWidth()) / 2)
					&& (X < ((screenW - optionButtonUp.getWidth()) / 2)
							+ optionButtonUp.getWidth())
					&& (Y > (int) (screenH * 0.8))
					&& (Y < (int) (screenH * 0.8) + optionButtonUp.getHeight())) {
				optionButtonPressed = true;
			}
			break;

		case MotionEvent.ACTION_MOVE:
			break;

		case MotionEvent.ACTION_UP:
			// Launch either the game screen or the options screen
			if (playButtonPressed) {
				Intent gameIntent = new Intent(myContext, GameActivity.class);
				myContext.startActivity(gameIntent);
			} else if (optionButtonPressed) {
				Intent optionIntent = new Intent(myContext,
						OptionActivity.class);
				myContext.startActivity(optionIntent);
			}

			playButtonPressed = false;
			optionButtonPressed = false;
			break;
		}

		invalidate();

		return true;
	}
}

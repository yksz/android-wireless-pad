package jp.android.wirelesspad.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import groovy.transform.CompileStatic
import jp.android.wirelesspad.R
import jp.android.wirelesspad.ui.util.SystemUiHider

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
@CompileStatic
public class FullscreenActivity extends Activity {
    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider

    private GestureDetector mGestureDetector

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        contentView = R.layout.activity_fullscreen

        def controlsView = findViewById(R.id.fullscreen_content_controls)
        def contentView = findViewById(R.id.fullscreen_content)

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS)
        mSystemUiHider.setup()
        mSystemUiHider.setOnVisibilityChangeListener { boolean visible ->
            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE)
        }

        mGestureDetector = createGestureDetector()
    }

    private GestureDetector createGestureDetector() {
        return new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Log.d("Gesture", "onSingleTapUp")
                return super.onSingleTapUp(e)
            }

            @Override
            public void onLongPress(MotionEvent e) {
                mSystemUiHider.toggle()
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                Log.d("Gesture", "onScroll: distanceX=" + distanceX + ", distanceY=" + distanceY)
                return super.onScroll(e1, e2, distanceX, distanceY)
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Log.d("Gesture", "onDoubleTap")
                return super.onDoubleTap(e)
            }
        })
    }

    @Override
    public void onResume() {
        super.onResume()
        if (mSystemUiHider.isVisible()) {
            mSystemUiHider.hide()
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                mSystemUiHider.show()
                return true
            default:
                return super.onKeyUp(keyCode, event)
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event)
        return false
    }

    public void onClickBluetoothSettingsButton(View view) {
        def intent = new Intent(this, BluetoothSettingsActivity.class)
        startActivity(intent)
    }

    public void onClickWiFiSettingsButton(View view) {
        def intent = new Intent(this, WiFiSettingsActivity.class)
        startActivity(intent)
    }

    public void onClickCloseButton(View view) {
        mSystemUiHider.hide()
    }
}

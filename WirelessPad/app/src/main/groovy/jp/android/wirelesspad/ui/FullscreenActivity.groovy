package jp.android.wirelesspad.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import groovy.transform.CompileStatic
import jp.android.wirelesspad.R
import jp.android.wirelesspad.client.WirelessPadClient
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

    private static final int REQUEST_TEXT = 0;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider
    private MultiTouchGestureDetector mGestureDetector;

    private WirelessPadClient mClient

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

    private MultiTouchGestureDetector createGestureDetector() {
        return new MultiTouchGestureDetector(this, new MultiTouchGestureDetector.OnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Log.d("Gesture", "onSingleTapUp: count=" + e.pointerCount)
                if (mClient != null) {
                    try {
                        mClient.send("leftClick")
                    } catch (ex) {
                    }
                }
                return false
            }

            @Override
            public boolean onMultiTapUp(MotionEvent e) {
                Log.d("Gesture", "onMultiTapUp: count=" + e.pointerCount)
                if (mClient != null) {
                    try {
                        mClient.send("rightClick")
                    } catch (ex) {
                    }
                }
                return false
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (mClient != null) {
                    if (e2.pointerCount == 1) {
                        int moveX = (distanceX + 0.5) as int
                        int moveY = (distanceY + 0.5) as int
                        try {
                            mClient.send("move $moveX $moveY")
                        } catch (ex) {
                        }
                    } else if (e2.pointerCount == 2) {
                        int amount = (distanceY * 2 + 0.5) as int
                        try {
                            mClient.send("scroll $amount")
                        } catch (ex) {
                        }
                    }
                }
                return false
            }
        })
    }

    @Override
    public void onResume() {
        super.onResume()
        if (mSystemUiHider.isVisible()) {
            mSystemUiHider.hide()
        }
        if (mClient == null) {
            Toast.makeText(this, "Not connected", Toast.LENGTH_SHORT).show()
        } else if (mClient.connection.isOpen()) {
            Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show()
        } else if (mClient.connection.isClosed()) {
            mClient.connect()
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy()
        if (mClient != null) {
            mClient.close()
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
        startActivityForResult(intent, REQUEST_TEXT);
    }

    public void onClickCloseButton(View view) {
        mSystemUiHider.hide()
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_TEXT:
                def uri = (URI) data.getSerializableExtra("uri")
                if (uri != null) {
                    mClient = new WirelessPadClient(uri)
                    mClient.connect()
                }
                break;
        }
    }
}

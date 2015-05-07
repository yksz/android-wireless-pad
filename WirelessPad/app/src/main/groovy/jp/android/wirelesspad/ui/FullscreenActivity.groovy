package jp.android.wirelesspad.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen)

        def controlsView = findViewById(R.id.fullscreen_content_controls)
        def contentView = findViewById(R.id.fullscreen_content)

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS)
        mSystemUiHider.setup()
        mSystemUiHider.setOnVisibilityChangeListener { visible ->
            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE)
        }

        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnLongClickListener { view ->
            mSystemUiHider.toggle()
            return false
        }
    }

    @Override
    public void onResume() {
        super.onResume()
        if (mSystemUiHider.isVisible()) {
            mSystemUiHider.hide()
        }
    }

    public void onClickSettingsButton(View view) {
        def intent = new Intent(this, SettingsActivity.class)
        startActivity(intent)
    }

    public void onClickCloseButton(View view) {
        mSystemUiHider.hide()
    }
}

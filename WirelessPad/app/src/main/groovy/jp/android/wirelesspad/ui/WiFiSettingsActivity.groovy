package jp.android.wirelesspad.ui

import android.os.Bundle
import android.support.v7.app.ActionBarActivity
import groovy.transform.CompileStatic
import jp.android.wirelesspad.R

@CompileStatic
public class WiFiSettingsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        contentView = R.layout.activity_wifisettings
    }
}

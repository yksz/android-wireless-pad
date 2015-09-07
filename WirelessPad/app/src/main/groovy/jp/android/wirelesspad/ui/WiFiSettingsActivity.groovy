package jp.android.wirelesspad.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.ActionBarActivity
import android.view.View
import android.widget.EditText
import android.widget.Toast
import groovy.transform.CompileStatic
import jp.android.wirelesspad.R
import jp.android.wirelesspad.remote.mouse.WebSocketMouse

@CompileStatic
public class WiFiSettingsActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        contentView = R.layout.activity_wifisettings
    }

    public void onClickConnectButton(View view) {
        def editText = (EditText) findViewById(R.id.wifiSettings_host_editText)
        def host = editText.getText().toString()

        if (!testConnection(host)) {
            Toast.makeText(this, "Connection failed", Toast.LENGTH_LONG).show()
            return
        }

        def data = new Intent()
        data.putExtra("host", host)
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    private boolean testConnection(String host) {
        def mouse = new WebSocketMouse(host)
        mouse.connect()
        try {
            return mouse.move(0, 0)
        } finally {
            mouse.disconnect()
        }
    }
}

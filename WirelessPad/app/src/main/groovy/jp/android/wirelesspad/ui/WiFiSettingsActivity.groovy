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
import jp.android.wirelesspad.remote.mouse.Mouse
import jp.android.wirelesspad.remote.mouse.MouseFactory
import jp.android.wirelesspad.remote.mouse.UDPMouse

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

        Mouse mouse = createMouse()
        if (!mouse.checkConnection(host)) {
            Toast.makeText(this, "Connection failed", Toast.LENGTH_LONG).show()
            return
        }

        def data = new Intent()
        data.putExtra("host", host)
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    private Mouse createMouse() {
        MouseFactory.mouse = new UDPMouse()
        return MouseFactory.mouse
    }
}

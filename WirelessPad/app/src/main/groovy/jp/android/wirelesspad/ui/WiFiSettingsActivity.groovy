package jp.android.wirelesspad.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.ActionBarActivity
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import groovy.transform.CompileStatic
import jp.android.wirelesspad.R
import jp.android.wirelesspad.remote.mouse.Mouse
import jp.android.wirelesspad.remote.mouse.MouseFactory
import jp.android.wirelesspad.remote.mouse.TCPMouse
import jp.android.wirelesspad.remote.mouse.UDPMouse
import jp.android.wirelesspad.remote.mouse.WebSocketMouse

@CompileStatic
public class WiFiSettingsActivity extends ActionBarActivity {
    private static final String TAG = WiFiSettingsActivity.class.simpleName
    private RadioGroup mRadioGroup
    private EditText mEditText

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        contentView = R.layout.activity_wifisettings
        mRadioGroup = (RadioGroup) findViewById(R.id.wifiSettings_connectionType_radioGroup)
        mEditText = (EditText) findViewById(R.id.wifiSettings_host_editText)
    }

    public void onClickConnectButton(View view) {
        def mouse = createMouse()
        def host = mEditText.text.toString()
        Log.d(TAG, "host=${host}")
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
        def id = mRadioGroup.getCheckedRadioButtonId()
        if (id == -1) {
            throw new AssertionError("A radio button must be checked" as Object)
        }
        def radioButton = (RadioButton) findViewById(id)
        switch (radioButton.text) {
            case getString(R.string.wifiSettings_tcp_radioButton):
                MouseFactory.mouse = new TCPMouse()
                break
            case getString(R.string.wifiSettings_udp_radioButton):
                MouseFactory.mouse = new UDPMouse()
                break
            case getString(R.string.wifiSettings_webSocket_radioButton):
                MouseFactory.mouse = new WebSocketMouse()
                break
            default:
                throw new AssertionError("Unknown radioButton: ${radioButton.text}" as Object)
        }
        Log.d(TAG, "createMouse: mouse=" + MouseFactory.mouse)
        return MouseFactory.mouse
    }
}

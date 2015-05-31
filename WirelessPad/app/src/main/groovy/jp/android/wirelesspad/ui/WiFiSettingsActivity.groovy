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
import jp.android.wirelesspad.client.WirelessPadClient

@CompileStatic
public class WiFiSettingsActivity extends ActionBarActivity {
    private static String PROTOCOL = "ws";
    private static int PORT = 7681;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        contentView = R.layout.activity_wifisettings
    }

    public void onClickConnectButton(View view) {
        def editText = (EditText) findViewById(R.id.wifiSettings_host_editText)
        def host = editText.getText().toString()

        def uri = new URI("${PROTOCOL}://${host}:${PORT}")
        try {
            testConnection(uri)
        } catch (e) {
            Toast.makeText(this, "Connection failed", Toast.LENGTH_LONG).show()
            return
        }

        def data = new Intent()
        data.putExtra("uri", uri)
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    private void testConnection(URI uri) {
        def client = new WirelessPadClient(uri)
        client.connectBlocking()
        try {
            client.send("test")
        } finally {
            client.close()
        }
    }
}

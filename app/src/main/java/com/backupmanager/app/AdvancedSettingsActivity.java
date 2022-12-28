package com.backupmanager.app;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.backupmanager.app.utils.CaptureAct;
import com.backupmanager.app.utils.Configuration;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class AdvancedSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_settings);
        ((EditText)findViewById(R.id.textHttp)).setText(Configuration.getConfiguration(getApplicationContext()).getParameterValue("http"));
        ((EditText)findViewById(R.id.textTcp)).setText(Configuration.getConfiguration(getApplicationContext()).getParameterValue("tcp"));
        ((EditText)findViewById(R.id.textIp)).setText(Configuration.getConfiguration(getApplicationContext()).getParameterValue("ip"));
        findViewById(R.id.saveAdvSettings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ip = ((EditText)findViewById(R.id.textIp)).getText().toString();
                String htttpPort = ((EditText)findViewById(R.id.textHttp)).getText().toString();
                String tcpPort = ((EditText)findViewById(R.id.textTcp)).getText().toString();
                Configuration.getConfiguration(getApplicationContext()).addOrModifyConfiguration("ip", ip);
                Configuration.getConfiguration(getApplicationContext()).addOrModifyConfiguration("http", htttpPort);
                Configuration.getConfiguration(getApplicationContext()).addOrModifyConfiguration("tcp", tcpPort);

            }
        });
        findViewById(R.id.qrcode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScanOptions options = new ScanOptions();
                options.setBeepEnabled(true);
                options.setOrientationLocked(true);
                options.setCaptureActivity(CaptureAct.class);
                barLauncher.launch(options);
            }
        });
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if(result.getContents() != null){
            ((EditText)findViewById(R.id.textIp)).setText(result.getContents().split(":")[0]);
            ((EditText)findViewById(R.id.textHttp)).setText(result.getContents().split(":")[1]);
            ((EditText)findViewById(R.id.textTcp)).setText(result.getContents().split(":")[2]);
        }
    });
}
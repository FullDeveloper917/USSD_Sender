package com.david.ussd_sender;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceActivity;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Button btnSetting;
    private Button btnSend;
    private TextView txtResponse;

    private BroadcastReceiver receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        tryToRequestMarshmallowPermission(Manifest.permission.CALL_PHONE);

    }

    private void findViews() {
        btnSetting = findViewById(R.id.btnSetting);
        btnSend = findViewById(R.id.btnSend);
        txtResponse = findViewById(R.id.txtResponse);
    }

    private void setEvents() {

        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startService(new Intent(MainActivity.this, USSDService.class));

                String ussd = "#100#";
                startActivity(new Intent("android.intent.action.CALL",
                        Uri.parse("tel:" + ussd.replaceAll("#", Uri.encode("#")))));
            }
        });

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();
                String message = intent.getStringExtra(Consts.USSD_MESSAGE);
                if(action != null && action.equals(Consts.USSD_CONTENT)) {
                    Log.d(TAG, "ussd message: " + message);
                    txtResponse.setText(message);
                }
            }
        };

        registerReceiver(receiver, new IntentFilter(Consts.USSD_CONTENT));
    }


    public void tryToRequestMarshmallowPermission(String permissionName) {

        if (Build.VERSION.SDK_INT >= 23) {

            final Method checkSelfPermissionMethod = getCheckSelfPermissionMethod();

            if (checkSelfPermissionMethod != null) {

                try {

                    final Integer permissionCheckResult = (Integer) checkSelfPermissionMethod.invoke(this, permissionName);
                    if (permissionCheckResult != PackageManager.PERMISSION_GRANTED) {
                        final Method requestPermissionsMethod = getRequestPermissionsMethod();
                        if (requestPermissionsMethod != null) {
                            requestPermissionsMethod.invoke(this, new String[]{permissionName}, 1);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        setEvents();

    }

    private Method getCheckSelfPermissionMethod() {
        try {
            return Activity.class.getMethod("checkSelfPermission", String.class);
        } catch (Exception e) {
            return null;
        }
    }

    private Method getRequestPermissionsMethod() {
        try {
            final Class[] parameterTypes = {String[].class, int.class};

            return Activity.class.getMethod("requestPermissions", parameterTypes);

        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {

        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setEvents();
        } else {
            finish();
        }
    }


}

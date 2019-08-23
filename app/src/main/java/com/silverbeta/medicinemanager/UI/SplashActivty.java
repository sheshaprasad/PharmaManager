package com.silverbeta.medicinemanager.UI;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.silverbeta.medicinemanager.R;

public class SplashActivty extends AppCompatActivity {

    LinearLayout errorLayout;
    TextView owner;
    public static final String[] permissionsRequired= new String[]{
            Manifest.permission.CALL_PHONE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE

    };
    private SharedPreferences permissionStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_activty);

        if(checkPermission()) {
            initApp();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==101){
            initApp();
        }
    }

    private void initApp() {

        String ts = Context.TELEPHONY_SERVICE;
        errorLayout = findViewById(R.id.errorLayout);
        owner = findViewById(R.id.owner);
        TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(ts);


        final String imei = mTelephonyMgr.getDeviceId();

        new Handler().postDelayed(new Runnable() {
            public void run() {
                // do something...

                if (imei.equalsIgnoreCase(getResources().getString(R.string.appid)) || imei.equalsIgnoreCase(getResources().getString(R.string.appid1))
                        || imei.equalsIgnoreCase(getResources().getString(R.string.appid2))) {
                    Intent wizard = new Intent(SplashActivty.this, MainActivity.class);
                    startActivity(wizard);
                    finish();
                } else {
                    errorLayout.setVisibility(View.VISIBLE);
                    owner.setText(imei);
                }
            }
        }, 3000);
    }


    public boolean checkPermission() {

        if (ActivityCompat.checkSelfPermission(this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, permissionsRequired[2]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, permissionsRequired[3]) != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale((Activity) this, permissionsRequired[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale((Activity) this, permissionsRequired[2])
                    || ActivityCompat.shouldShowRequestPermissionRationale((Activity) this, permissionsRequired[3])) {


            } else {
                ActivityCompat.requestPermissions((Activity) this, permissionsRequired, 101);
            }

            permissionStatus = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(permissionsRequired[0], true);
            editor.putBoolean(permissionsRequired[1], true);
            editor.putBoolean(permissionsRequired[2], true);
            editor.putBoolean(permissionsRequired[3], true);
            editor.apply();
            Log.i("Refresh", "Refresh Activity");


        } else {
            return true;
        }
        return false;
    }


}

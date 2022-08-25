package com.allvideodownloader.inappupdatefirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.developer.kalert.KAlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private FirebaseRemoteConfig remoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView currentversion=findViewById(R.id.tv);
        currentversion.setText("Current version Code: " + getVersionCode());

        HashMap<String,Object> defaultsRate=new HashMap<>();
        defaultsRate.put("new Version Code", String.valueOf(getVersionCode()));

        remoteConfig=FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings=new FirebaseRemoteConfigSettings.Builder()
                .setFetchTimeoutInSeconds(2).build();
        remoteConfig.setConfigSettingsAsync(configSettings);
        remoteConfig.setDefaultsAsync(defaultsRate);
        remoteConfig.fetchAndActivate().addOnCompleteListener(new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {

                if(task.isSuccessful()){
                    final String new_version_code=remoteConfig.getString("new_version_code");
                    if(Integer.parseInt(new_version_code) > getVersionCode())
                        showCustomDialog(new_version_code);
                }

            }
        });




    }
// custom github dialog
    private void showCustomDialog(String versionFromRemoteConfig) {
        KAlertDialog sd = new KAlertDialog(this, 0);
        sd.setTitleText("New Update Available");
        sd.setContentText("Latest version is: " + versionFromRemoteConfig);
        sd.setContentTextAlignment(View.TEXT_ALIGNMENT_CENTER, Gravity.CENTER);
        sd.setConfirmText("UPDATE NOW");

        sd.setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
            @Override
            public void onClick(KAlertDialog kAlertDialog) {

                Uri uri = Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);

                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                }


            }
        });
        sd.setCancelable(false);
        sd.setCanceledOnTouchOutside(false);
        sd.changeAlertType(KAlertDialog.SUCCESS_TYPE);
        sd.show();
    }

    //  google show dialog
    /*private void showDialog(String versionFromRemoteConfig) {
        final AlertDialog dialog=new AlertDialog.Builder(this).setTitle("New Uddate Available")
                .setMessage("Latest version is: "+versionFromRemoteConfig).setPositiveButton("Update",null)
                .show();
        dialog.setCancelable(false);
        Button positivebutton=dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positivebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);

                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                }
            }
        });


    }*/


    public int getVersionCode() {
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
return Objects.requireNonNull(packageInfo).versionCode;

    }
}
package com.sageconger.qop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("preferences", MODE_PRIVATE);
        System.out.println(sharedPreferences.getInt("id", 0));
        if (sharedPreferences.getInt("id", 0) != 0) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }

    }
}

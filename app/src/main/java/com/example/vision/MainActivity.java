package com.example.vision;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        DataBase db = new DataBase(this);
        db.insert("Türkçe");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intentMainPage = new Intent(MainActivity.this, MainPage.class);
                startActivity(intentMainPage);
                finish();
            }
        }, 1000);
    }
}

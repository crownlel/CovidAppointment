package com.example.covidvac;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //LocaleManager.onCreate(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.btnEnter);
        button.setOnClickListener(v -> {

        });

        ImageButton greekBtn = findViewById(R.id.ibGreek);
        greekBtn.setOnClickListener(v ->{
            LocaleManager.setLocale(this,"el");
            this.finish();
            overridePendingTransition(0, 0);
            this.startActivity(getIntent());
            overridePendingTransition(0, 0);
        });
        ImageButton engBtn = findViewById(R.id.ibEnglish);
        engBtn.setOnClickListener(v ->{
            LocaleManager.setLocale(this,"en");
            this.finish();
            overridePendingTransition(0, 0);
            this.startActivity(getIntent());
            overridePendingTransition(0, 0);
        });
    }
}
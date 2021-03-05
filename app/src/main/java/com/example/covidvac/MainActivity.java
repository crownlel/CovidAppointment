package com.example.covidvac;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private final int speechRequest = 105;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleManager.onCreate(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set buttons functionality
        Button button = findViewById(R.id.btnEnter);
        button.setOnClickListener(v -> enter());

        ImageButton greekBtn = findViewById(R.id.ibGreek);
        greekBtn.setOnClickListener(v -> setGreek());

        ImageButton engBtn = findViewById(R.id.ibEnglish);
        engBtn.setOnClickListener(v -> setEnglish());

        Button btnManage = findViewById(R.id.btnManage);
        btnManage.setOnClickListener(v -> manage());

        ImageView ivVoice = findViewById(R.id.ivVoice);
        ivVoice.setOnClickListener(v -> voiceRecognition());
    }

    //changes app language to greek
    private void setGreek(){
        LocaleManager.setLocale(this, "el");
        this.finish();
        overridePendingTransition(0, 0);
        this.startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    //changes app language to english
    private void setEnglish(){
        LocaleManager.setLocale(this, "en");
        this.finish();
        overridePendingTransition(0, 0);
        this.startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    //enters employee login activity
    private void manage(){
        Intent intent = new Intent(this, EmployeeLoginActivity.class);
        startActivity(intent);
    }

    //enters citizen login activity
    private void enter(){

        Intent intent = new Intent(this, CitizenLoginActivity.class);
        startActivity(intent);
    }

    //vaccination center map
    public void mapView(View view) {

        Intent maps = new Intent(this, MapsActivity.class);

        startActivity(maps);
    }

    //prompts user for voice input
    private void voiceRecognition(){
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Toast.makeText(this,getString(R.string.voice_not_supported), Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, LocaleManager.getLanguage(this));
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.voice_prompt));
            this.startActivityForResult(intent, this.speechRequest);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //if request code is speechRequest code result is voice generated string data
        if (requestCode == speechRequest && resultCode == -1){

            ArrayList result;
            if (data != null){

                result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String resultStr = result.get(0).toString();

                if (resultStr.toLowerCase().contains("είσοδος") || resultStr.toLowerCase().contains("enter")){

                    enter();
                }
                else if(resultStr.toLowerCase().contains("διαχείρηση") || resultStr.toLowerCase().contains("manage")){

                    manage();
                }
                else if(resultStr.toLowerCase().contains("χάρτες") || resultStr.toLowerCase().contains("maps")){

                    mapView(null);
                }
                else if(resultStr.toLowerCase().contains("αγγλικά") || resultStr.toLowerCase().contains("english")){

                    setEnglish();
                }
                else if(resultStr.toLowerCase().contains("ελληνικά") || resultStr.toLowerCase().contains("greek")){

                    setGreek();
                }
                else {

                    Toast.makeText(this, getString(R.string.voice_false), Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(this,getString(R.string.voice_false), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
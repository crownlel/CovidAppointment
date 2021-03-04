package com.example.covidvac;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.covidvac.interfaces.LoginCallback;
import com.example.covidvac.models.Citizen;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class CitizenLoginActivity extends AppCompatActivity {

    FirebaseDatabase db;
    SharedPreferences sharedPref;
    final String SOCIAL_SEC_NUM_KEY = "social_security_number";
    final String BIRTHDAY = "birthday";
    final long MIN_AGE = 70;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citizen_login);
        db = FirebaseDatabase.getInstance();
        sharedPref = getApplicationContext().getSharedPreferences("LOGIN_PREFS", Context.MODE_PRIVATE);

        EditText etSocialSecNum = findViewById(R.id.etSocialSecNum);
        EditText etBirthDate = findViewById(R.id.etBirthdate);
        etSocialSecNum.setText(sharedPref.getString(SOCIAL_SEC_NUM_KEY, ""));
        etBirthDate.setText(sharedPref.getString(BIRTHDAY, ""));


        //calendar stuff
        final Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            String myFormat = "dd/MM/yyyy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("el"));

            etBirthDate.setText(sdf.format(myCalendar.getTime()));
        };

        etBirthDate.setOnClickListener(v -> {
          DatePickerDialog datePicker =  new DatePickerDialog(this, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH));
            Calendar c = Calendar.getInstance();
            c.add(Calendar.HOUR,-24);
            Long yesterday = c.getTimeInMillis();
            datePicker.getDatePicker().setMaxDate(yesterday);
            datePicker.show();
        });

        //login
        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(v -> {
            //TODO change this
            String socialSecNum = etSocialSecNum.getText().toString().trim();
            String birthdate = etBirthDate.getText().toString().trim();
            DatabaseReference cref = db.getReference("Citizens");

            Citizen citizen = new Citizen();
            citizen.login(cref, socialSecNum.trim(), birthdate.trim(), new LoginCallback() {
                @Override
                public void loginCalled(boolean success) {

                    if (success) {

                        Date birthdate = citizen.getBirthday();
                        Date today = Calendar.getInstance().getTime();

                        long diffInMillies = Math.abs(today.getTime() - birthdate.getTime());
                        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                        long age = diff/365;
                        if (age >= MIN_AGE){
                            Intent intent = new Intent(getApplicationContext(), CitizenMainActivity.class);
                            Bundle bundle = new Bundle();

                            bundle.putSerializable("citizen", citizen);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                        else {

                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.citizen_login_wrong_age) + " " + MIN_AGE, Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                    else {

                        Toast.makeText(getApplicationContext(),
                                getString(R.string.citizen_login_wrong_credentials), Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            });
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (sharedPref != null) {
            SharedPreferences.Editor editor = sharedPref.edit();

            EditText etSocialSecNum = findViewById(R.id.etSocialSecNum);
            EditText etBirthDate = findViewById(R.id.etBirthdate);
            editor.putString(SOCIAL_SEC_NUM_KEY, etSocialSecNum.getText().toString());
            editor.putString(BIRTHDAY, etBirthDate.getText().toString());
            editor.apply();
        }
    }
}
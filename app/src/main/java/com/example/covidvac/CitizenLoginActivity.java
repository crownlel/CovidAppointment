package com.example.covidvac;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.covidvac.models.Citizen;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CitizenLoginActivity extends AppCompatActivity {

    FirebaseDatabase db;
    SharedPreferences sharedPref;
    final String TAX_ID_KEY = "tax_id";
    final String BIRTHDAY = "birthday";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citizen_login);
        db = FirebaseDatabase.getInstance();
        sharedPref = getApplicationContext().getSharedPreferences("LOGIN_PREFS",Context.MODE_PRIVATE);

        EditText etTaxId = findViewById(R.id.etTaxId);
        EditText etBirthDate = findViewById(R.id.etBirthdate);
        etTaxId.setText(sharedPref.getString(TAX_ID_KEY,""));
        etBirthDate.setText(sharedPref.getString(BIRTHDAY,""));


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
            new DatePickerDialog(this, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        //login
        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(v -> {
            //TODO change this
            String tax_id = etTaxId.getText().toString().trim();
            String birthdate = etBirthDate.getText().toString().trim();
            login(tax_id.trim(), birthdate.trim());

        });

    }

    private Citizen login(String tax_id , String birthdate){

        Intent intent = new Intent(this, CitizenMainActivity.class);
        Bundle bundle = new Bundle();

        try {

            DatabaseReference cref = db.getReference("Citizens");

            cref.orderByKey().addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()){

                        Citizen ciz = snapshot.getValue(Citizen.class);
                        if (ciz != null && tax_id.equals(ciz.getTax_id()) && birthdate.equals(ciz.getBirthdayToString())){

                            bundle.putSerializable("citizen", ciz);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            return;
                        }
                    }
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.citizen_login_wrong_credentials), Toast.LENGTH_SHORT)
                            .show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        catch (Exception ex){
            Toast.makeText(getApplicationContext(),
                    "Oops", Toast.LENGTH_SHORT)
                    .show();

        }


        return null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (sharedPref !=null){
            SharedPreferences.Editor editor = sharedPref.edit();

            EditText etTaxId = findViewById(R.id.etTaxId);
            EditText etBirthDate = findViewById(R.id.etBirthdate);
            editor.putString(TAX_ID_KEY, etTaxId.getText().toString());
            editor.putString(BIRTHDAY,etBirthDate.getText().toString());
            editor.apply();
        }
    }
}
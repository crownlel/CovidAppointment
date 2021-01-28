package com.example.covidvac;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.covidvac.models.Citizen;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.Locale;

public class CitizenLoginActivity extends AppCompatActivity {

    final FirebaseDatabase db = FirebaseDatabase.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citizen_login);


        EditText etTaxId = findViewById(R.id.etTaxId);
        EditText etBirthDate = findViewById(R.id.etBirthdate);


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
            login(tax_id, birthdate);

        });

    }

    private Citizen login(String tax_id , String birthdate){

        Intent intent = new Intent(this, CitizenMainActivity.class);
        Bundle bundle = new Bundle();

        try {
            DatabaseReference cref = db.getReference("Citizens");

            cref.orderByChild("tax_id").addChildEventListener(new ChildEventListener(){


                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Citizen ciz = snapshot.getValue(Citizen.class);
                    System.out.println(ciz.toString());


                    bundle.putSerializable("citizen", null); //Your id
                    intent.putExtras(bundle);
                    // startActivity(intent);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
        catch (Exception x){

        }


        return null;
    }



}
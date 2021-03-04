package com.example.covidvac.models;

import androidx.annotation.NonNull;

import com.example.covidvac.interfaces.AppointmentListCallback;
import com.example.covidvac.interfaces.LoginCallback;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Citizen implements Serializable {
    private int id;
    private String name;
    private String birthday;
    private String id_number;
    private String tax_id;
    private String telephone;


    public Citizen(int id, String name, String birthday, String id_number, String tax_id, String telephone) {
        this.id = id;
        this.name = name;
        this.birthday = birthday;
        this.id_number = id_number;
        this.tax_id = tax_id;
        this.telephone = telephone;
    }

    public Citizen() {

    }

    //region Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getBirthday() {
        DateFormat DF = new SimpleDateFormat("dd/MM/yyyy");
        try {
            return DF.parse(birthday);
        } catch (ParseException ex) {
            return null;
        }
    }

    public String getBirthdayToString() {
        return birthday;
    }

    public String getId_number() {
        return id_number;
    }

    public String getTax_id() {
        return tax_id;
    }

    public String getTelephone() {
        return telephone;
    }

//endregion


    public void login(DatabaseReference citRef, String tax_id, String birthday, final LoginCallback callback) {

        try {

            TaskCompletionSource<Citizen> tcs = new TaskCompletionSource<>();
            citRef.orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            Citizen ciz = snapshot.getValue(Citizen.class);
                            if (ciz != null && tax_id.equals(ciz.getTax_id()) && birthday.equals(ciz.getBirthdayToString())) {

                                String key = snapshot.getKey();
                                ciz.id = Integer.parseInt(key.substring(3)); //skips "id_"
                                tcs.setResult(ciz);

                                return;
                            }

                        }

                        tcs.setResult(null);
                    } catch (Exception ex) {
                        throw ex;
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            Task<Citizen> task = tcs.getTask();
            task.addOnCompleteListener(command -> {
                Citizen ciz = task.getResult();
                if (ciz != null) {

                    this.id = ciz.id;
                    this.name = ciz.name;
                    this.birthday = ciz.birthday;
                    this.id_number = ciz.id_number;
                    this.tax_id = ciz.tax_id;
                    this.telephone = ciz.telephone;
                    callback.loginCalled(true);

                } else {

                    callback.loginCalled(false);
                }

            });

        } catch (Exception ex) {
            throw ex;
        }
    }

    public void getAppointments(DatabaseReference appRef, final AppointmentListCallback callback){

        Appointment.getCitizenAppointments(appRef, id, callback);
    }
}

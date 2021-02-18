package com.example.covidvac.models;

import androidx.annotation.NonNull;

import com.example.covidvac.interfaces.AppointmentCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Appointment implements Serializable {
    private int id;
    private String date;
    private int isApproved;
    private int isCanceled;
    private int centre_id;
    private int citizen_id;
    private String parent_id;

    //region Getters
    public int getId() {
        return id;
    }

    public Date getDate() {
        DateFormat DF = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        try {
            return DF.parse(date);
        } catch (ParseException ex) {
            return null;
        }
    }

    public boolean getIsApproved() {
        return isApproved > 0;
    }

    public boolean getIsCanceled() {
        return isCanceled > 0;
    }

    public int getCentre_id() {
        return centre_id;
    }

    public int getCitizen_id() {
        return citizen_id;
    }

    public Integer getParent_id() {
        if (parent_id.trim() == ""){
            return null;
        } else{
            return Integer.parseInt(parent_id.trim());
        }
    }
    //endregion

    public static void getCitizenAppointments(DatabaseReference appRef, int cit_id, final AppointmentCallback callback){

        appRef.orderByChild("citizen_id").equalTo(cit_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Appointment> list = new ArrayList();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Appointment app = snapshot.getValue(Appointment.class);
                    String key = snapshot.getKey();
                    app.id = Integer.parseInt(key.substring(3)); //skips "id_"
                    list.add(app);
                }
                callback.citizenAppointmentsCalled(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void getCentreAppointments(DatabaseReference appRef, int cen_id, final AppointmentCallback callback){

        appRef.orderByChild("centre_id").equalTo(cen_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Appointment> list = new ArrayList();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Appointment app = snapshot.getValue(Appointment.class);
                    String key = snapshot.getKey();
                    app.id = Integer.parseInt(key.substring(3)); //skips "id_"
                    list.add(app);
                }
                callback.citizenAppointmentsCalled(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

package com.example.covidvac.models;

import androidx.annotation.NonNull;

import com.example.covidvac.interfaces.AppointmentCallback;
import com.example.covidvac.interfaces.CitizenCallback;
import com.example.covidvac.interfaces.CitizenLoginCallback;
import com.example.covidvac.interfaces.VaccinationCentreCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

    public Date getDateAsDate() {
        DateFormat DF = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        try {
            return DF.parse(date);
        } catch (ParseException ex) {
            return null;
        }
    }

    public String getDate(){
        return date;
    }

    public int getIsApproved() {
        return isApproved;
    }

    public boolean getIsApprovedToBool(){
        return isApproved > 0;
    }

    public void setIsApproved(int isApproved){
        if(isApproved>1 || isApproved <0){
            throw new IllegalArgumentException("wrong value on setter");
        }
        this.isApproved = isApproved;
    }
    public void setIsApprovedBool(boolean isApproved){
        if(isApproved){
            this.isCanceled = 1;
        }
        else {
            this.isCanceled = 0;
        }
    }

    public int getIsCanceled() {
        return isCanceled;
    }

    public boolean getIsCanceledToBool(){
        return isCanceled > 0;
    }

    public void setIsCanceledBool(boolean isCanceled){
        if(isCanceled){
            this.isCanceled = 1;
        }
        else {
            this.isCanceled = 0;
        }
    }
    public void setIsCanceled(int isCanceled){
        if(isCanceled>1 || isCanceled <0){
            throw new IllegalArgumentException("wrong value on setter");
        }
        this.isCanceled = isCanceled;
    }


    public int getCentre_id() {
        return centre_id;
    }

    public int getCitizen_id() {
        return citizen_id;
    }

    public String getParent_id() {
        return parent_id;
    }

    //endregion

    public void getCentre(VaccinationCentreCallback callback){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        db.getReference("VaccinationCentre").orderByKey().equalTo("id_" + citizen_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot sn : snapshot.getChildren()){
                    //assume there is only one child
                    VaccinationCentre centre = sn.getValue(VaccinationCentre.class);
                    callback.centreFetched(centre);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setCentre_id(int centre_id) {
        this.centre_id = centre_id;
    }

    public void setCitizen_id(int citizen_id) {
        this.citizen_id = citizen_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public void getCitizen(CitizenCallback callback){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        db.getReference("Citizens").orderByKey().equalTo("id_" + citizen_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot sn : snapshot.getChildren()){
                    //assume there is only one child
                    Citizen cit = sn.getValue(Citizen.class);
                    callback.citizenFetched(cit);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

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

    private Object toObject(){
        return new Object(){
            final public int centre_id = getCentre_id();
            final public int citizen_id = getCitizen_id();
            final public String date = getDate();
            final public int isApproved = getIsApproved();
            final public int isCanceled = getIsCanceled();
            final public String parent_id = getParent_id();
        };
    }
    public void Save(DatabaseReference appRef){
        //save new
        Map<String, Object> appointmentUpdates = new HashMap<>();

        if (this.id == 0){
            FirebaseDatabase.getInstance().getReference("Ids/Appointments").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    id = (int)((long) snapshot.getValue());
                    FirebaseDatabase.getInstance().getReference("Ids/Appointments").setValue(id+1);
                    appointmentUpdates.put("id_" + id, toObject());
                    appRef.child("id_" + id).setValue(toObject());
                    //appRef.setValue(appointmentUpdates);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        //update existing
        else {
            appointmentUpdates.put("id_" + id, toObject());
            appRef.updateChildren(appointmentUpdates);
        }
    }
}

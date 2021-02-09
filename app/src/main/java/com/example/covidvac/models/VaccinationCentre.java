package com.example.covidvac.models;

import androidx.annotation.NonNull;

import com.example.covidvac.interfaces.VaccinationCentreCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class VaccinationCentre {


    static FirebaseDatabase db;


    private int id;
    private String name;
    private String address;
    private String telephone;
    private double latitude;
    private double longitude;


    public VaccinationCentre() {
    }

    public VaccinationCentre(int id, String name, String address, String telephone, double lati, double longi) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.telephone = telephone;
        this.latitude = lati;
        this.longitude = longi;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getTelephone() {
        return telephone;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public static void getVacCe(final VaccinationCentreCallback callback){

        ArrayList<VaccinationCentre> vacCen = new ArrayList<VaccinationCentre>();

        db = FirebaseDatabase.getInstance();
        DatabaseReference vcRef = db.getReference("VaccinationCentre");

        vcRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try{
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                        VaccinationCentre vaccinationCentre = snapshot.getValue(VaccinationCentre.class);
                        String key = snapshot.getKey();
                        vaccinationCentre.id = Integer.parseInt(key.substring(3));
                        vacCen.add(vaccinationCentre);



                    }

                    callback.setVacCeList(vacCen);


                }
                catch(Exception ex){
                    throw ex;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}

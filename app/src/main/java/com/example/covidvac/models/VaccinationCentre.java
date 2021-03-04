package com.example.covidvac.models;

import android.location.Location;

import androidx.annotation.NonNull;

import com.example.covidvac.interfaces.AppointmentListCallback;
import com.example.covidvac.interfaces.VaccinationCentreCallback;
import com.example.covidvac.interfaces.VaccinationCentreListCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;

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

    public static void getCentre(VaccinationCentreCallback callback, int centreId){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        db.getReference("VaccinationCentre").orderByKey().equalTo("id_" + centreId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot sn : snapshot.getChildren()){
                    //assume there is only one child
                    VaccinationCentre centre = sn.getValue(VaccinationCentre.class);

                    String key = sn.getKey();
                    centre.id = Integer.parseInt(key.substring(3)); //skips "id_"
                    callback.centreFetched(centre);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void getVacCe(DatabaseReference vcRef, final VaccinationCentreListCallback callback){

        ArrayList<VaccinationCentre> vacCen = new ArrayList<VaccinationCentre>();


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

    public float getDistance(LatLng latlng){

        Location loc1 = new Location("");
        loc1.setLatitude(latlng.latitude);
        loc1.setLongitude(latlng.longitude);

        Location loc2 = new Location("");
        loc2.setLatitude(this.latitude);
        loc2.setLongitude(this.longitude);

        return loc1.distanceTo(loc2);
    }

    public static ArrayList<Map.Entry<VaccinationCentre,Float>> getSortedDistances(ArrayList<VaccinationCentre> centres, LatLng latlng){
        ArrayList sortedVac = new ArrayList<Map.Entry<VaccinationCentre,Float>>();
        for(VaccinationCentre centre : centres){
            float distance = centre.getDistance(latlng);
            sortedVac.add(new AbstractMap.SimpleEntry<VaccinationCentre,Float>(centre,distance));
        }
        sortedVac.sort(Map.Entry.comparingByValue());

        return sortedVac;

    }

    public void getAppointments(final AppointmentListCallback callback){

        DatabaseReference appRef = FirebaseDatabase.getInstance().getReference("Appointments");
        Appointment.getCentreAppointments(appRef, id, callback);
    }
}

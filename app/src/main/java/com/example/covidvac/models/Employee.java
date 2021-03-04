package com.example.covidvac.models;

import androidx.annotation.NonNull;

import com.example.covidvac.interfaces.EmployeeCallback;
import com.example.covidvac.interfaces.LoginCallback;
import com.example.covidvac.interfaces.VaccinationCentreCallback;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;

public class Employee implements Serializable {

    private int id;
    private String username;
    private String password;
    private String fullname;
    private int centreId;

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public int getCentreId() {
        return centreId;
    }

    public void setCentreId(int centreId) {
        this.centreId = centreId;
    }

    public void login(String username, String password, final LoginCallback callback){

        DatabaseReference empRef = FirebaseDatabase.getInstance().getReference("Employees");
        TaskCompletionSource<Employee> tcs = new TaskCompletionSource<>();
        empRef.orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        Employee emp = snapshot.getValue(Employee.class);
                        if (emp != null && username.equals(emp.getUsername()) && password.equals(emp.getPassword())) {

                            String key = snapshot.getKey();
                            emp.id = Integer.parseInt(key.substring(3)); //skips "id_"
                            tcs.setResult(emp);

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

        Task<Employee> task = tcs.getTask();
        task.addOnCompleteListener(command -> {
            Employee emp = task.getResult();
            if (emp != null) {

                this.id = emp.id;
                this.username = emp.username;
                this.password = emp.password;
                this.fullname = emp.fullname;
                this.centreId = emp.centreId;
                callback.loginCalled(true);

            } else {

                callback.loginCalled(false);
            }

        });
    }

    public void getCentre(VaccinationCentreCallback callback){

        VaccinationCentre.getCentre(callback, centreId);
    }
}

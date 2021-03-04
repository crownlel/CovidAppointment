package com.example.covidvac;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.covidvac.models.Appointment;
import com.example.covidvac.models.Employee;
import com.example.covidvac.ui.main.AppointmentAdapter;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EmployeeMainActivity extends AppCompatActivity {

    Employee employee;
    ArrayList<Appointment> appointments;
    RecyclerView rvAppointments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        employee = (Employee) bundle.getSerializable("employee");
        setContentView(R.layout.activity_employee_main);

        TextView tvUser = findViewById(R.id.tvUser);
        TextView tvCentre = findViewById(R.id.tvCentreName);
        Button btnAccepted = findViewById(R.id.btnAccepted);
        Button btnPending = findViewById(R.id.btnPending);
        Button btnAll = findViewById(R.id.btnAll);
        rvAppointments = findViewById(R.id.rvCentreAppointments);

        //disable buttons before loading appointments
        btnAccepted.setEnabled(false);
        btnPending.setEnabled(false);
        btnAll.setEnabled(false);
        tvUser.setText(employee.getFullname());

        //set buttons functionality
        btnAccepted.setOnClickListener(v -> showConfirmed());
        btnPending.setOnClickListener(v -> showPending());
        btnAll.setOnClickListener(v -> showAll());

        //load appointments
        employee.getCentre(centre -> {

            tvCentre.setText(centre.getName());
            centre.getAppointments(appList -> {

                appointments = appList;

                showAll();
                btnAccepted.setEnabled(true);
                btnPending.setEnabled(true);
                btnAll.setEnabled(true);
            });
        });

    }

    private void showConfirmed() {

        Stream<Appointment> filteredApps = appointments.stream().filter(app -> app.getIsApprovedToBool() && !app.getIsCanceledToBool());
        ArrayList<Appointment> filtered = filteredApps.collect(Collectors.toCollection(ArrayList::new));
        setRecyclerViewData(orderAppointments(filtered));
    }

    private void showPending() {
        Stream<Appointment> filteredApps = appointments.stream().filter(app -> !app.getIsApprovedToBool() && !app.getIsCanceledToBool());
        ArrayList<Appointment> filtered = filteredApps.collect(Collectors.toCollection(ArrayList::new));
        setRecyclerViewData(orderAppointments(filtered));
    }

    private void showAll() {
        setRecyclerViewData(orderAppointments(appointments));
    }

    private ArrayList<Appointment> orderAppointments(ArrayList<Appointment> apps) {

        Stream<Appointment> firstApps = apps.stream().filter(app -> app.getParent_id().trim().equals(""));
        firstApps = firstApps.sorted(Comparator.comparing(Appointment::getDateAsDate));
        ArrayList<Appointment> firstList = firstApps.collect(Collectors.toCollection(ArrayList::new));

        Stream<Appointment> secondApps = apps.stream().filter(app -> !app.getParent_id().trim().equals(""));
        //secondApps = secondApps.sorted(Comparator.comparing(Appointment::getDateAsDate));
        ArrayList<Appointment> secondList = secondApps.collect(Collectors.toCollection(ArrayList::new));

        ArrayList<Appointment> orderedApps = new ArrayList<>();
        for (Appointment app : firstList) {

            orderedApps.add(app);
            for (Appointment sndApp : secondList) {
                if (Integer.parseInt(sndApp.getParent_id()) == app.getId()) {

                    orderedApps.add(sndApp);
                    break;
                }
            }
        }
        return orderedApps;
    }

    private void setRecyclerViewData(ArrayList<Appointment> apps){
        if(rvAppointments != null){

            rvAppointments.setVisibility(View.VISIBLE);
            rvAppointments.setLayoutManager(new LinearLayoutManager(this));
            final AppointmentAdapter adapter = new AppointmentAdapter(this, apps);
            rvAppointments.setAdapter(adapter);
        }

    }
}
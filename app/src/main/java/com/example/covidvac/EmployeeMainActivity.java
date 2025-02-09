package com.example.covidvac;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.covidvac.models.Appointment;
import com.example.covidvac.models.Employee;
import com.example.covidvac.ui.main.AppointmentAdapter;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
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
        Button btnDone = findViewById(R.id.btnDone);
        rvAppointments = findViewById(R.id.rvCentreAppointments);

        //disable buttons before loading appointments
        btnAccepted.setEnabled(false);
        btnPending.setEnabled(false);
        btnAll.setEnabled(false);
        btnDone.setEnabled(false);
        tvUser.setText(employee.getFullname());

        //set buttons functionality
        btnAccepted.setOnClickListener(v -> showConfirmed());
        btnPending.setOnClickListener(v -> showPending());
        btnAll.setOnClickListener(v -> showAll());
        btnDone.setOnClickListener(v -> showDone());

        //load appointments
        employee.getCentre(centre -> {

            tvCentre.setText(centre.getName());
            centre.getAppointments(appList -> {

                appointments = appList;

                showAll();
                btnAccepted.setEnabled(true);
                btnPending.setEnabled(true);
                btnAll.setEnabled(true);
                btnDone.setEnabled(true);
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

    //shows confirmed appointments that are done.
    private void showDone(){
        Date today = Calendar.getInstance().getTime();
        Stream<Appointment> filteredApps = appointments.stream().filter(app -> app.getIsApprovedToBool() && !app.getIsCanceledToBool() && app.getDateAsDate().before(today));
        ArrayList<Appointment> filtered = filteredApps.collect(Collectors.toCollection(ArrayList::new));
        setRecyclerViewData(orderAppointments(filtered));
    }

    //orders appointments in pairs per user/creation
    private ArrayList<Appointment> orderAppointments(ArrayList<Appointment> apps) {

        Stream<Appointment> firstApps = apps.stream().filter(app -> app.getParent_id().trim().equals(""));
        firstApps = firstApps.sorted(Comparator.comparing(Appointment::getDateAsDate));
        ArrayList<Appointment> firstList = firstApps.collect(Collectors.toCollection(ArrayList::new));

        Stream<Appointment> secondApps = apps.stream().filter(app -> !app.getParent_id().trim().equals(""));
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

    //fills recyclerview with data
    private void setRecyclerViewData(ArrayList<Appointment> apps){
        if(rvAppointments != null){

            rvAppointments.setVisibility(View.VISIBLE);
            rvAppointments.setLayoutManager(new LinearLayoutManager(this));
            final AppointmentAdapter adapter = new AppointmentAdapter(this, apps);
            rvAppointments.setAdapter(adapter);
        }

    }

    public void showEditDialog(Appointment appointment){

        Date today = Calendar.getInstance().getTime();
        //if appointment is canceled or has happened it should not be edited;
        if(appointment.getIsCanceledToBool() || appointment.getDateAsDate().before(today))
            return;

        final Dialog filterDialog = new Dialog(this);
        filterDialog.setContentView(R.layout.dialog_edit_appointment);

        //use item layout inside the dialog
        LinearLayout itemLayout = filterDialog.findViewById(R.id.itemLayout);
        LayoutInflater factory = LayoutInflater.from(this);
        View itemView = factory.inflate(R.layout.item_appointment, null);

        TextView tvDate = itemView.findViewById(R.id.tvDate);
        TextView tvTime = itemView.findViewById(R.id.tvTime);
        TextView tvCentre = itemView.findViewById(R.id.tvCentre);
        TextView tvCitName = itemView.findViewById(R.id.tvCitName);
        ImageView ivStatus = itemView.findViewById(R.id.ivStatus);
        Button btnCancel = filterDialog.findViewById(R.id.btnReject);
        Button btnApprove = filterDialog.findViewById(R.id.btnApprove);
        TextView tvSocSecNumber = filterDialog.findViewById(R.id.tvSocSecNumber);
        TextView tvContact = filterDialog.findViewById(R.id.tvContact);

        //buttons functionality
        btnCancel.setOnClickListener(v -> {
            appointment.setIsCanceled(1);
            appointment.save(FirebaseDatabase.getInstance().getReference("Appointments"), app -> { });
            filterDialog.hide();
        });
        btnApprove.setOnClickListener(v -> {
            appointment.setIsApproved(1);
            appointment.save(FirebaseDatabase.getInstance().getReference("Appointments"), app -> { });
            filterDialog.hide();
        });

        //fill views with data
        tvTime.setText(new SimpleDateFormat("hh:mm").format(appointment.getDateAsDate()));
        tvDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(appointment.getDateAsDate()));

        appointment.getCentre(centre -> tvCentre.setText(centre.getName()));
        appointment.getCitizen(citizen -> {
            tvCitName.setText(citizen.getName());
            tvSocSecNumber.setText(getString(R.string.formInsurance) + " " + citizen.getTax_id());
            tvContact.setText(getString(R.string.formTelephone) + " " + citizen.getTelephone());
        });

        //sets dynamic icon per appointment status
        if (appointment.getIsApprovedToBool())
            ivStatus.setImageDrawable(getDrawable(R.drawable.ic_confirmed_foreground));
        if(appointment.getIsCanceledToBool())
            ivStatus.setImageDrawable(getDrawable(R.drawable.ic_canceled_foreground));

        itemLayout.addView(itemView);
        filterDialog.show();
    }
}
package com.example.covidvac;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;

import com.example.covidvac.models.Appointment;
import com.example.covidvac.models.Citizen;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.covidvac.ui.main.SectionsPagerAdapter;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;

public class CitizenMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }
        Bundle bundle = getIntent().getExtras();
        Citizen citizen = (Citizen) bundle.getSerializable("citizen");
        setContentView(R.layout.activity_citizen_main);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), citizen);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        TextView tvName = findViewById(R.id.tvName);
        tvName.setText(citizen.getName());
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void showEditDialog(Appointment appointment){
        final Dialog filterDialog = new Dialog(this);
        filterDialog.setContentView(R.layout.dialog_edit_appointment);

        LinearLayout itemLayout = filterDialog.findViewById(R.id.itemLayout);
        LayoutInflater factory = LayoutInflater.from(this);
        View itemView = factory.inflate(R.layout.item_appointment, null);

        //fill view with data
        TextView tvDate = itemView.findViewById(R.id.tvDate);
        TextView tvTime = itemView.findViewById(R.id.tvTime);
        TextView tvCentre = itemView.findViewById(R.id.tvCentre);
        TextView tvCitName = itemView.findViewById(R.id.tvCitName);
        ImageView ivStatus = itemView.findViewById(R.id.ivStatus);
        Button btnCancel = filterDialog.findViewById(R.id.btnReject);
        Button btnApprove = filterDialog.findViewById(R.id.btnApprove);

        filterDialog.findViewById(R.id.tvSocSecNumber).setVisibility(View.GONE);
        filterDialog.findViewById(R.id.tvContact).setVisibility(View.GONE);

        //change button text for citizen view
        btnApprove.setVisibility(View.GONE);
        btnCancel.setText(R.string.dialog_edit_appointment_btnCancel);
        btnCancel.setOnClickListener(v -> {
            appointment.setIsCanceled(1);
            appointment.save(FirebaseDatabase.getInstance().getReference("Appointments"), app -> { });
            filterDialog.hide();
        });

        tvTime.setText(new SimpleDateFormat("hh:mm").format(appointment.getDateAsDate()));
        tvDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(appointment.getDateAsDate()));

        appointment.getCentre(centre -> tvCentre.setText(centre.getName()));
        appointment.getCitizen(citizen -> tvCitName.setText(citizen.getName()));

        //sets dynamic icon per appointment status
        if (appointment.getIsApprovedToBool())
            ivStatus.setImageDrawable(getDrawable(R.drawable.ic_confirmed_foreground));
        if(appointment.getIsCanceledToBool())
            ivStatus.setImageDrawable(getDrawable(R.drawable.ic_canceled_foreground));

        itemLayout.addView(itemView);
        filterDialog.show();
    }
    public void mapView(View view) {

        Intent maps = new Intent(this, MapsActivity.class);

        startActivity(maps);

    }
}
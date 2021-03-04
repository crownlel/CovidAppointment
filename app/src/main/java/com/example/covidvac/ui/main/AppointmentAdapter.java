package com.example.covidvac.ui.main;

import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.AbstractInputMethodService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covidvac.CitizenMainActivity;
import com.example.covidvac.MapsActivity;
import com.example.covidvac.R;
import com.example.covidvac.interfaces.CitizenCallback;
import com.example.covidvac.models.Appointment;
import com.example.covidvac.models.Citizen;
import com.example.covidvac.models.VaccinationCentre;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Appointment> appointments;

    public AppointmentAdapter(Context context, ArrayList<Appointment> appointments) {
        this.context = context;
        this.appointments = appointments;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_appointment
                        , parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Appointment appointment = appointments.get(position);

        holder.tvTime.setText(new SimpleDateFormat("HH:mm").format(appointment.getDateAsDate()));
        holder.tvDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(appointment.getDateAsDate()));

        appointment.getCitizen(cit -> holder.tvCitName.setText(cit.getName()));
        appointment.getCentre(centre -> holder.tvCentre.setText(centre.getName()));

        holder.layoutAppointmentItem.setOnClickListener(v -> {
            //if pending and on citizen main activity
            if (context instanceof CitizenMainActivity && !appointment.getIsCanceledToBool() && !appointment.getIsApprovedToBool()) {
                ((CitizenMainActivity)context).showEditDialog(appointment);
            }
            //TODO implement this

//                else if (context instanceof CentreMainActivity){
//
//                }
        });
        //sets dynamic icon per appointment status
        if (appointment.getIsApprovedToBool())
            holder.ivStatus.setImageDrawable(context.getDrawable(R.drawable.ic_confirmed_foreground));
        if(appointment.getIsCanceledToBool())
            holder.ivStatus.setImageDrawable(context.getDrawable(R.drawable.ic_canceled_foreground));

        if (position % 2 == 0) {
            holder.layoutAppointmentItem.setBackgroundColor(
                    ContextCompat.getColor(
                            context,
                            R.color.light_gray
                    )
            );
        } else {
            holder.layoutAppointmentItem.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    class ViewHolder extends  RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        final LinearLayout layoutAppointmentItem = itemView.findViewById(R.id.layoutAppointmentItem);
        final TextView tvDate = itemView.findViewById(R.id.tvDate);
        final TextView tvTime = itemView.findViewById(R.id.tvTime);
        final TextView tvCentre = itemView.findViewById(R.id.tvCentre);
        final TextView tvCitName = itemView.findViewById(R.id.tvCitName);
        final ImageView ivStatus = itemView.findViewById(R.id.ivStatus);
    }
}

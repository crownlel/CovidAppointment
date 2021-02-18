package com.example.covidvac.ui.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covidvac.R;
import com.example.covidvac.models.Appointment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

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

        holder.tvTime.setText(new SimpleDateFormat("hh:mm").format(appointment.getDate()));
        holder.tvDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(appointment.getDate()));
        // TODO get centre and citizen display names
        holder.tvCentre.setText("appointment.getCentre_id()");
        holder.tvCitName.setText("appointment.getCitizen_id()");

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
    }
}

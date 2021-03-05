package com.example.covidvac.ui.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.covidvac.R;
import com.example.covidvac.models.Citizen;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AppointmentsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AppointmentsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_CITIZEN = "citizen";

    // TODO: Rename and change types of parameters
    private Citizen citizen;
    private RecyclerView rvAppointments;

    public AppointmentsFragment() {
        // Required empty public constructor
    }

    public static AppointmentsFragment newInstance(Citizen cit) {
        AppointmentsFragment fragment = new AppointmentsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CITIZEN, cit);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            citizen = (Citizen) getArguments().getSerializable(ARG_CITIZEN);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_appointments, container, false);


        rvAppointments = view.findViewById(R.id.rvAppointments);
        rvAppointments.setVisibility(View.VISIBLE);
        rvAppointments.setLayoutManager(new LinearLayoutManager(view.getContext()));
        //fills recyclerview with data
        citizen.getAppointments(
                FirebaseDatabase.getInstance().getReference("Appointments"),
                appointments -> {
                    final AppointmentAdapter adapter = new AppointmentAdapter(view.getContext(), appointments);
                    rvAppointments.setAdapter(adapter);
                });
        return view;
    }
}
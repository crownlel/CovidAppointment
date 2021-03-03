package com.example.covidvac.interfaces;

import com.example.covidvac.models.Appointment;

import java.util.ArrayList;

public interface AppointmentListCallback {
    void citizenAppointmentsCalled(ArrayList<Appointment> appointments);
}

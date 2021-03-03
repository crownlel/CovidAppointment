package com.example.covidvac.interfaces;

import com.example.covidvac.models.Appointment;

public interface AppointmentCallback {
    void appointmentFetched(Appointment appointment);
}

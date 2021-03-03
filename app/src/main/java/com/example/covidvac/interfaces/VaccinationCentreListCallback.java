package com.example.covidvac.interfaces;

import com.example.covidvac.models.VaccinationCentre;

import java.util.ArrayList;

public interface VaccinationCentreListCallback {
    void setVacCeList(ArrayList<VaccinationCentre> centres);
}

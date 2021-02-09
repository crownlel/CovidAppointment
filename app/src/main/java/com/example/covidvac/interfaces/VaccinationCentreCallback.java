package com.example.covidvac.interfaces;

import com.example.covidvac.models.VaccinationCentre;

import java.util.ArrayList;

public interface VaccinationCentreCallback {
    void setVacCeList(ArrayList<VaccinationCentre> centres);
}

package com.example.covidvac.ui.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.covidvac.LocaleManager;
import com.example.covidvac.R;
import com.example.covidvac.interfaces.LocationCallback;
import com.example.covidvac.interfaces.VaccinationCentreListCallback;
import com.example.covidvac.models.Appointment;
import com.example.covidvac.models.Citizen;
import com.example.covidvac.models.VaccinationCentre;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class NewAppointmentFragment extends Fragment {

    private static final String ARG_CITIZEN = "citizen";
    private Citizen citizen ;
    private View view;
    private int ACCESS_LOCATION_REQUEST_CODE = 10001;
    FusedLocationProviderClient fusedLocationProviderClient;
    FirebaseDatabase db;
    TextView formNameIn;
    EditText formVCentre;
    TextView formIncuranceIn;
    TextView formBDateIn;
    TextView formIDIn;
    TextView formTelephoneIn;
    EditText formFirstAppointmentDate;
    TextView formSecondAppointmentDate;
    EditText formDateHour;
    Button formMakeAppointment;
    ArrayList<VaccinationCentre> vacCeList;
    ArrayList<String> datehours;


    public static NewAppointmentFragment newInstance(Citizen citizen) {
        NewAppointmentFragment fragment = new NewAppointmentFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_CITIZEN, citizen );
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseDatabase.getInstance();
        citizen = (Citizen) getArguments().getSerializable(ARG_CITIZEN);

        datehours = new ArrayList<String>();
        datehours.add("10:00");
        datehours.add("10:30");
        datehours.add("11:00");
        datehours.add("11:30");
        datehours.add("12:00");
        datehours.add("12:30");
        datehours.add("13:00");
        datehours.add("13:30");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_appointment_form, container, false);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(view.getContext());

        formVCentre = view.findViewById(R.id.etFormVCentreIn);

        if(ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
            locationTask.addOnSuccessListener(location -> {

                if(location != null){

                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                    //loads vaccination centres and hints for nearest centre
                    VaccinationCentre.getVacCe(db.getReference("VaccinationCentre"), centres -> {

                        ArrayList<Map.Entry<VaccinationCentre,Float>> sortedVCentres = VaccinationCentre.getSortedDistances(centres, latLng);
                        vacCeList = centres;
                        VaccinationCentre nearest = sortedVCentres.get(0).getKey();
                        formVCentre.setHint(nearest.getName() + " \n" + getResources().getString(R.string.nearestVacCentre));
                    });
                }
                else{
                    Toast.makeText(view.getContext(),"Failed to get Location", Toast.LENGTH_SHORT).show();
                }

            });
            locationTask.addOnFailureListener(new OnFailureListener() {

                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(view.getContext(),"Failed to get Location", Toast.LENGTH_SHORT).show();
                }
            });
        } else {

            requestPermissions( new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE);
        }

        formFirstAppointmentDate = view.findViewById(R.id.edFormFirstAppointmentDateIn);
        formSecondAppointmentDate = view.findViewById(R.id.formSecondAppointmentDateIn);
        //
        formNameIn = view.findViewById(R.id.tvFormNameIn);
        formNameIn.setText(citizen.getName());
        //
        formIncuranceIn = view.findViewById(R.id.tvFormInsuranceIn);
        formIncuranceIn.setText(citizen.getTax_id());
        //
        formBDateIn = view.findViewById(R.id.tvFormBDateIn);
        formBDateIn.setText(citizen.getBirthdayToString());
        //
        formIDIn = view.findViewById(R.id.tvFormIDIn);
        formIDIn.setText(citizen.getId_number());
        //
        formTelephoneIn = view.findViewById(R.id.tvFormTelephoneIn);
        formTelephoneIn.setText(citizen.getTelephone());
        //
        formMakeAppointment = view.findViewById(R.id.btMakeAppointment);
        formDateHour = view.findViewById(R.id.etFormDateHoursIn);

        //sets a dialog to pick centres from
        formVCentre.setOnClickListener(v -> {

            Dialog dCentres = new Dialog(view.getContext());
            dCentres.setContentView(R.layout.dialog_vcentres);
            dCentres.show();

            //checks for location permission
            if(ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
                locationTask.addOnSuccessListener(location -> {

                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    VaccinationCentre.getVacCe(db.getReference("VaccinationCentre"), centres -> {

                        //gets vaccination centres in nearest order
                        ArrayList<Map.Entry<VaccinationCentre,Float>> sortedVCentres = VaccinationCentre.getSortedDistances(centres, latLng);
                        RadioGroup rgCentres = dCentres.findViewById(R.id.rgVCentres);
                        for (Map.Entry<VaccinationCentre,Float> centre : sortedVCentres){

                            RadioButton rb = new RadioButton(view.getContext());
                            //pretify distance value
                            if(centre.getValue()>=1000){

                                rb.setText(centre.getKey().getName() + "\n" + round((double)Math.round(centre.getValue())/(double)1000,1) + " km");
                                rgCentres.addView(rb);
                            }else{

                                rb.setText(centre.getKey().getName() + "\n" + Math.round(centre.getValue()) + " m");
                                rgCentres.addView(rb);
                            }

                            //set text on formVCentre EditText
                            rb.setOnClickListener(v1 -> {

                                formVCentre.setText(rb.getText().toString().split("\n")[0]); //Removes meters after new line!
                                dCentres.hide();
                            });
                        }
                    });
                });
            } else {

                requestPermissions( new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE);

                //Opens dialog with no distance sorting!
                VaccinationCentre.getVacCe(db.getReference("VaccinationCentre"), centres -> {

                    RadioGroup rgCentres = dCentres.findViewById(R.id.rgVCentres);
                    for (VaccinationCentre centre : centres){

                        RadioButton rb = new RadioButton(view.getContext());
                        rb.setText(centre.getName());
                        rgCentres.addView(rb);

                        //set text on formVCentre EditText
                        rb.setOnClickListener(v12 -> {

                            formVCentre.setText(rb.getText());
                            dCentres.hide();
                        });
                    }
                });
            }
        });

        String date_n = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        formFirstAppointmentDate.setText(date_n);
        final Calendar myCalendar = Calendar.getInstance();

        //when a date is picked on the DatePickerDialog,
        //fills both appointment dates according to the 28 days in between rule
        DatePickerDialog.OnDateSetListener date = (v, year, monthOfYear, dayOfMonth) -> {

            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String myFormat = "dd/MM/yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("el"));
            formFirstAppointmentDate.setText(sdf.format(myCalendar.getTime()));
            try {

                myCalendar.setTime(sdf.parse(String.valueOf(formFirstAppointmentDate)));
            } catch (ParseException e) {

                e.printStackTrace();
            }
            myCalendar.add(Calendar.DATE, 28);
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
            formSecondAppointmentDate.setText(sdf1.format(myCalendar.getTime()));
            myCalendar.add(Calendar.DATE, -28);
        };

        //opens DatePickerDialog
        formFirstAppointmentDate.setOnClickListener(v -> {

            DatePickerDialog datePicker = new DatePickerDialog(view.getContext(), date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH));

            //sets datepicker min date
            Calendar c = Calendar.getInstance();
            c.add(Calendar.HOUR,24);
            Long tomorrow = c.getTimeInMillis();
            datePicker.getDatePicker().setMinDate(tomorrow);
            datePicker.show();
        });

        //opens dialog to pick appointment time
        formDateHour.setOnClickListener(v -> {

            Dialog dHours = new Dialog(view.getContext());
            dHours.setContentView(R.layout.dialog_date_hours);
            dHours.show();
            RadioGroup rgDateHours = dHours.findViewById(R.id.rgDateHours);
            for(String dhours : datehours){

                RadioButton rb = new RadioButton(view.getContext());
                rb.setText(dhours);
                rgDateHours.addView(rb);

                //sets time on EditText
                rb.setOnClickListener(v13 -> {

                    formDateHour.setText(rb.getText());
                    dHours.hide();
                });
            }
        });

        //Checking if the form is completed
        formFirstAppointmentDate.addTextChangedListener(submitEnalbed);
        formSecondAppointmentDate.addTextChangedListener(submitEnalbed);
        formVCentre.addTextChangedListener(submitEnalbed);
        formDateHour.addTextChangedListener(submitEnalbed);

        //creates both appointments
        formMakeAppointment.setOnClickListener(v -> {

            if (vacCeList == null)
                return;

            DatabaseReference appointmentRef = db.getReference().child("Appointments");
            ArrayList<VaccinationCentre> centres = vacCeList;
            int isApproved = 0;
            int isCanceled = 0;
            String parent_id = "";
            String date1 = (formFirstAppointmentDate.getText().toString() + " " + formDateHour.getText().toString());
            int citizen_id = citizen.getId();
            int centre_id = 0;

            for (VaccinationCentre centre : centres) {

                if (centre.getName().equals(formVCentre.getText().toString())) {

                    centre_id = centre.getId();
                    break;
                }
            }
            if (centre_id == 0) {

                Toast.makeText(view.getContext(),"Failed to get Vaccination Center", Toast.LENGTH_SHORT).show();
                return;
            }

            int finalCentre_id = centre_id;
            citizen.getAppointmentsSingle(appointmentRef, appointments -> {

                //checks if citizen has already made an appointment
                for(Appointment app : appointments){

                    if(!app.getIsCanceledToBool()){

                        Toast.makeText(view.getContext(),getResources().getString(R.string.appAlreadyExist), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                Appointment newAppointment = new Appointment(date1, isApproved, isCanceled, finalCentre_id, citizen_id, parent_id);
                //saves first appointment
                newAppointment.save(appointmentRef, firstAppSaved -> {

                    //save return null if the date/time is not available
                    if(firstAppSaved == null){

                        Toast.makeText(view.getContext(),getResources().getString(R.string.notAvailableDate), Toast.LENGTH_SHORT).show();
                    }
                    else{

                        //saves second appointment
                        Appointment secondApp = makeAppointment(Integer.toString(firstAppSaved.getId()));
                        secondApp.save(appointmentRef, secAppSaved -> {

                            //save return null if the date/time is not available
                            if (secAppSaved == null) {

                                firstAppSaved.delete(appointmentRef);
                                Toast.makeText(view.getContext(),getResources().getString(R.string.notAvailableDate), Toast.LENGTH_SHORT).show();
                            }
                            else {

                                //success toast
                                Toast.makeText(view.getContext(),getResources().getString(R.string.submitSuccess) ,Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            });
        });

        return view;
    }

    //created second appointment
    private Appointment makeAppointment(String p_id) {

        int isApproved = 0;
        int isCanceled = 0;
        String date2 = (formSecondAppointmentDate.getText().toString() + " " + formDateHour.getText().toString());
        int citizen_id = citizen.getId();
        int centre_id = 0;

        for (VaccinationCentre centre : vacCeList) {

            if (centre.getName().equals(formVCentre.getText().toString())) {

                centre_id = centre.getId();
                break;
            }
        }
        return new Appointment(date2, isApproved, isCanceled, centre_id, citizen_id, p_id);
    }

    //Submit button enabled when the form is complete
    private TextWatcher submitEnalbed = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            String firstApp = formFirstAppointmentDate.getText().toString().trim();
            String seccondApp = formSecondAppointmentDate.getText().toString().trim();
            String hour = formDateHour.getText().toString().trim();
            String centre = formVCentre.getText().toString().trim();

            formMakeAppointment.setEnabled(!firstApp.isEmpty() && !seccondApp.isEmpty() && !hour.isEmpty() && !centre.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    //custom round method
    private static double round (double value, int precision) {

        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }
}

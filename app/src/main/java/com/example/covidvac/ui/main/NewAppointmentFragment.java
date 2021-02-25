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
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.covidvac.R;
import com.example.covidvac.interfaces.LocationCallback;
import com.example.covidvac.interfaces.VaccinationCentreCallback;
import com.example.covidvac.models.Citizen;
import com.example.covidvac.models.VaccinationCentre;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
    SharedPreferences sharedPref;
    FusedLocationProviderClient fusedLocationProviderClient;
    FirebaseDatabase db;

    //private PageViewModel pageViewModel;

    public static NewAppointmentFragment newInstance(Citizen citizen) {
        NewAppointmentFragment fragment = new NewAppointmentFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_CITIZEN, citizen );



        //bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
//        int index = 1;
//        if (getArguments() != null) {
//            index = getArguments().getInt(ARG_SECTION_NUMBER);
//        }
        //pageViewModel.setIndex(index);
        db = FirebaseDatabase.getInstance();
        citizen = (Citizen) getArguments().getSerializable(ARG_CITIZEN);
        sharedPref = getActivity().getApplicationContext().getSharedPreferences("FORM_PREFS", Context.MODE_PRIVATE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_appointment_form, container, false);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(view.getContext());

        EditText formVCentre = view.findViewById(R.id.etFormVCentreIn);

        if(ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
            locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    VaccinationCentre.getVacCe(db.getReference("VaccinationCentre"), new VaccinationCentreCallback(){
                        @Override
                        public void setVacCeList(ArrayList<VaccinationCentre> centres) {
                            ArrayList<Map.Entry<VaccinationCentre,Float>> sortedVCentres = VaccinationCentre.getSortedDistances(centres, latLng);
                            VaccinationCentre nearest = sortedVCentres.get(0).getKey();
                            formVCentre.setText(nearest.getName() + " " + getResources().getString(R.string.nearestVacCentre));


                        }
                    });

                }
            });


        } else {
            requestPermissions( new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE);
        }


        EditText formFirstAppointmentDate = view.findViewById(R.id.formFirstAppointmentDateIn);
        TextView formSecondAppointmentDate = view.findViewById(R.id.formSecondAppointmentDateIn);

        //
        TextView formNameIn = view.findViewById(R.id.tvFormNameIn);
        formNameIn.setText(citizen.getName());
        //
        TextView formIncuranceIn = view.findViewById(R.id.tvFormInsuranceIn);
        formIncuranceIn.setText(citizen.getTax_id());
        //
        TextView formBDateIn = view.findViewById(R.id.tvFormBDateIn);
        formBDateIn.setText(citizen.getBirthdayToString());
        //
        TextView formIDIn = view.findViewById(R.id.tvFormIDIn);
        formIDIn.setText(citizen.getId_number());
        //
        TextView formTelephoneIn = view.findViewById(R.id.tvFormTelephoneIn);
        formTelephoneIn.setText(citizen.getTelephone());
        //



        formVCentre.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                    Dialog dCentres = new Dialog(view.getContext());
                    dCentres.setContentView(R.layout.dialog_vcentres);
                    dCentres.show();

                if(ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
                    locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            VaccinationCentre.getVacCe(db.getReference("VaccinationCentre"), new VaccinationCentreCallback(){
                                @Override
                                public void setVacCeList(ArrayList<VaccinationCentre> centres) {
                                    ArrayList<Map.Entry<VaccinationCentre,Float>> sortedVCentres = VaccinationCentre.getSortedDistances(centres, latLng);
                                    RadioGroup rgCentres = dCentres.findViewById(R.id.rgVCentres);
                                    for (Map.Entry<VaccinationCentre,Float> centre : sortedVCentres){
                                        RadioButton rb = new RadioButton(view.getContext());
                                        rb.setText(centre.getKey().getName() + "\n" + Math.round(centre.getValue()) + "m" );
                                        rgCentres.addView(rb);
                                        rb.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                formVCentre.setText(rb.getText().toString().split("\n")[0]); //Removes meters after new line!
                                                dCentres.hide();
                                            }
                                        });
                                    }



                                }
                            });
                        }
                    });
                } else {
                    requestPermissions( new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE);
                    //Opens dialog with no distance sorting!
                    VaccinationCentre.getVacCe(db.getReference("VaccinationCentre"), new VaccinationCentreCallback(){
                        @Override
                        public void setVacCeList(ArrayList<VaccinationCentre> centres) {
                            RadioGroup rgCentres = dCentres.findViewById(R.id.rgVCentres);
                            for (VaccinationCentre centre : centres){
                                RadioButton rb = new RadioButton(view.getContext());
                                rb.setText(centre.getName());
                                rgCentres.addView(rb);
                                rb.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        formVCentre.setText(rb.getText());
                                        dCentres.hide();
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });




        String date_n = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        formFirstAppointmentDate.setText(date_n);

        final Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = (v, year, monthOfYear, dayOfMonth) -> {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            String myFormat = "dd/MM/yyyy"; //In which you need put here
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


        };

        formFirstAppointmentDate.setOnClickListener(v -> {
            new DatePickerDialog(view.getContext(), date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        return view;
    }

    private void formVacCelist (ArrayList<VaccinationCentre> vacCe, final LocationCallback callback){
        ArrayList<Integer> distance = new ArrayList<Integer>();

        LatLng myLocation = new LatLng(0,0);

        @SuppressLint("MissingPermission")
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                callback.getMyLocation(new LatLng(location.getLatitude(), location.getLongitude()));


            }
        });
    }
}

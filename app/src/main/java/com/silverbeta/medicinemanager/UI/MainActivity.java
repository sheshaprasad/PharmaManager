package com.silverbeta.medicinemanager.UI;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.silverbeta.medicinemanager.Adapters.DoctorsRecyclerViewAdapter;
import com.silverbeta.medicinemanager.DBManager.LocalDatabase;
import com.silverbeta.medicinemanager.Interfaces.DeleteDoctorListener;
import com.silverbeta.medicinemanager.Models.DoctorsList;
import com.silverbeta.medicinemanager.R;

import java.util.ArrayList;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity implements DeleteDoctorListener {

    FloatingActionButton addDoctor;
    RecyclerView doctorsListView;
    LinearLayout nodataLayout;
    DoctorsRecyclerViewAdapter recyclerViewAdapter;
    TextInputEditText searchBar;
    ConstraintLayout parentLayout;
    CardView searchBarLayout;
    ArrayList<DoctorsList> doctorsListArrayList = new ArrayList<>();
    private AlertDialog alertDialog;
    private LocalDatabase localDatabase;
    private android.support.v7.app.AlertDialog alert;
    public static final String[] permissionsRequired= new String[]{
            Manifest.permission.CALL_PHONE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE

    };
    private SharedPreferences permissionStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        localDatabase = new LocalDatabase(MainActivity.this);

        checkPermission();

        initViews();


    }

    private void initViews() {

        addDoctor = findViewById(R.id.addDoctor);
        doctorsListView = findViewById(R.id.doctorsListView);
        nodataLayout = findViewById(R.id.nodataLayout);
        parentLayout = findViewById(R.id.parentLayout);
        searchBar = findViewById(R.id.searchBar);
        searchBarLayout = findViewById(R.id.searchBarLayout);


        initClickListeners();

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length() >3) {
                    Searchfilter(s.toString());
                }else{
                    if(s.toString().length()<3){
                        updateList();
                    }
                }
            }
        });

        getDoctors();
    }

    private void initClickListeners() {

        addDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDoctorAddDialog();
            }
        });
    }

    public void showDoctorAddDialog(){


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_doctor, null);
        dialogBuilder.setView(dialogView);

        final TextInputEditText doctorName,doctorNumber,clinicName;
        Button add;

        doctorName = dialogView.findViewById(R.id.doctorName);
        doctorNumber = dialogView.findViewById(R.id.doctorNumber);
        clinicName = dialogView.findViewById(R.id.clinicName);
        add = dialogView.findViewById(R.id.add);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(doctorName.getText().toString().trim()) && !TextUtils.isEmpty(doctorNumber.getText().toString().trim())
                && !TextUtils.isEmpty(clinicName.getText().toString().trim())){

                    DoctorsList dl= new DoctorsList();
                    dl.setDoctorName(doctorName.getText().toString().trim());
                    dl.setDoctorNumber(doctorNumber.getText().toString().trim());
                    dl.setDoctorClinic(clinicName.getText().toString().trim());
                    localDatabase.insertDoctor(dl);
                    alertDialog.dismiss();
                    getDoctors();
                }else{

                    if(TextUtils.isEmpty(doctorName.getText().toString().trim())){
                        doctorName.setError("Enter Doctor's Name");
                    }
                    if(TextUtils.isEmpty(doctorNumber.getText().toString().trim())){
                        doctorNumber.setError("Enter Doctor's Number");
                    }
                    if(TextUtils.isEmpty(clinicName.getText().toString().trim())){
                        clinicName.setError("Enter Clinic's Name");
                    }
                    Snackbar.make(parentLayout,"Check Details entered",Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        alertDialog = dialogBuilder.create();
        alertDialog.show();

    }

    private void getDoctors() {

        doctorsListArrayList.clear();

        doctorsListArrayList = localDatabase.getDoctors();
        if(doctorsListArrayList.size()!=0) {
            searchBarLayout.setVisibility(View.VISIBLE);
            nodataLayout.setVisibility(GONE);
            doctorsListView.setVisibility(View.VISIBLE);
            updateList();
        }else{
            searchBarLayout.setVisibility(GONE);
            nodataLayout.setVisibility(View.VISIBLE);
            doctorsListView.setVisibility(GONE);
        }
    }

    void Searchfilter(String text){

        ArrayList<DoctorsList> doctorsListArrayList1 = new ArrayList<>();
        for(DoctorsList d: doctorsListArrayList) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches

            String foodTypecomp = d.getDoctorName().toLowerCase();

            if(foodTypecomp.contains(text)){
                doctorsListArrayList1.add(d);
            }
        }
        //update recyclerview
        updateList(doctorsListArrayList1);
    }

    private void updateList() {
        recyclerViewAdapter = new DoctorsRecyclerViewAdapter(this, doctorsListArrayList,this);
        doctorsListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        doctorsListView.setAdapter(recyclerViewAdapter);
    }

    private void updateList(ArrayList<DoctorsList> doctorsListArrayList1) {
        recyclerViewAdapter = new DoctorsRecyclerViewAdapter(this, doctorsListArrayList1,this);
        doctorsListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        doctorsListView.setAdapter(recyclerViewAdapter);
    }

    @Override
    public void onBackPressed() {
        LayoutInflater myLayout = LayoutInflater.from(this);
        View dialogView = myLayout.inflate(R.layout.exit_alert, null);
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setCancelable(false);

        TextView cancel = dialogView.findViewById(R.id.cancel);
        TextView exit = dialogView.findViewById(R.id.exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });
        alert = builder.create();
        alert.show();
    }

    public boolean checkPermission() {

        if (ActivityCompat.checkSelfPermission(this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, permissionsRequired[2]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, permissionsRequired[3]) != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale((Activity) this, permissionsRequired[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale((Activity) this, permissionsRequired[2])
                    || ActivityCompat.shouldShowRequestPermissionRationale((Activity) this, permissionsRequired[3])) {


            } else {
                ActivityCompat.requestPermissions((Activity) this, permissionsRequired, 101);
            }

            permissionStatus = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(permissionsRequired[0], true);
            editor.putBoolean(permissionsRequired[1], true);
            editor.putBoolean(permissionsRequired[2], true);
            editor.putBoolean(permissionsRequired[3], true);
            editor.apply();
            Log.i("Refresh", "Refresh Activity");


        } else {
            return true;
        }
        return false;
    }

    @Override
    public void doctorDeleted() {
        getDoctors();
    }
}

package com.silverbeta.medicinemanager.UI;

import android.Manifest;
import android.app.Activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import io.apptik.multiview.layoutmanagers.ViewPagerLayoutManager;

import com.fxn.pix.Pix;
import com.silverbeta.medicinemanager.Adapters.FilesRecyclerViewAdapter;
import com.silverbeta.medicinemanager.DBManager.LocalDatabase;
import com.silverbeta.medicinemanager.Interfaces.FileDeleteListener;
import com.silverbeta.medicinemanager.Models.FilesModel;
import com.silverbeta.medicinemanager.R;

import java.util.ArrayList;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class FilesViewActivity extends AppCompatActivity implements FileDeleteListener {

    RecyclerView imagesViewList;
    FloatingActionButton addFiles,presentationMode;
    LinearLayout titleView,nodataLayout;
    TextView doctorNameTV,clinicNameTV;
    boolean presentationOn = false;
    LocalDatabase localDatabase;
    ArrayList<FilesModel> filesModelArrayList = new ArrayList<>();
    FilesRecyclerViewAdapter filesRecyclerViewAdapter;
    String doctorName,clinicName,id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files_view);

        localDatabase = new LocalDatabase(this);
        Bundle bundle = getIntent().getExtras();
        assert bundle!=null;
        doctorName = bundle.getString("dn");
        clinicName = bundle.getString("cn");
        id = bundle.getString("id");
        initViews();
    }

    private void initViews() {


        imagesViewList = findViewById(R.id.imageViewList);
        addFiles = findViewById(R.id.addFiles);
        nodataLayout = findViewById(R.id.nodataLayout);
        doctorNameTV = findViewById(R.id.doctorName);
        clinicNameTV = findViewById(R.id.clinicName);
        presentationMode = findViewById(R.id.presentationMode);
        titleView = findViewById(R.id.titleView);
        imagesViewList.setLayoutManager(new ViewPagerLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        imagesViewList.setItemViewCacheSize(0);
        initClickListeners();

        doctorNameTV.setText(doctorName);
        clinicNameTV.setText(clinicName);

        getFilesList();

    }

    private void getFilesList() {

        filesModelArrayList.clear();
        filesModelArrayList = localDatabase.getFiles(id);
        if(filesModelArrayList.size()!=0) {
            nodataLayout.setVisibility(GONE);
            presentationMode.show();
            imagesViewList.setVisibility(VISIBLE);
            filesRecyclerViewAdapter = new FilesRecyclerViewAdapter(this, filesModelArrayList,this);
            imagesViewList.setAdapter(filesRecyclerViewAdapter);
        }else{
            presentationMode.hide();
            titleView.setVisibility(VISIBLE);
            nodataLayout.setVisibility(VISIBLE);
            imagesViewList.setVisibility(GONE);
        }

    }

    private void initClickListeners() {

        presentationMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presentationOn = true;
                titleView.setVisibility(GONE);
                presentationMode.hide();
                addFiles.hide();
            }
        });

        addFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(FilesViewActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(FilesViewActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            FilesViewActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE},
                            100);
                }else {
                    /*Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    getIntent.setType("image/*");

                    Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    pickIntent.setType("image/*");

                    Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                    startActivityForResult(chooserIntent, 101   );*/


                    Pix.start(FilesViewActivity.this, 101);

                }

            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 101 && resultCode==RESULT_OK){
           /* Uri selectedImage = data.getData();
            getRealPathFromURI(selectedImage,
                    this);*/

            ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            for(int i=0;i<returnValue.size();i++){
                localDatabase.addFile(id,returnValue.get(i));
            }
            getFilesList();
        }
    }

    public void getRealPathFromURI(Uri contentURI, Activity context) {
        String[] projection = { MediaStore.Images.Media.DATA };
        @SuppressWarnings("deprecation")
        Cursor cursor = context.managedQuery(contentURI, projection, null,
                null, null);
        if (cursor == null)
            return;
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        if (cursor.moveToFirst()) {
            String s = cursor.getString(column_index);
            // cursor.close();
            localDatabase.addFile(id,s);
            getFilesList();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==100){
            Pix.start(FilesViewActivity.this, 101);
        }
    }

    @Override
    public void onBackPressed() {
        if(presentationOn){
            presentationOn = false;
            titleView.setVisibility(VISIBLE);
            presentationMode.show();
            addFiles.show();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public void fileDeleted() {
        getFilesList();
    }
}

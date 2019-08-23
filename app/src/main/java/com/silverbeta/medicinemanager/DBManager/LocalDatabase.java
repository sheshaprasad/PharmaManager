package com.silverbeta.medicinemanager.DBManager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.silverbeta.medicinemanager.Models.DoctorsList;
import com.silverbeta.medicinemanager.Models.FilesModel;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class LocalDatabase extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;

    private static final String DB_NAME = "MedicineManager";

    private static final String DOCOTRS_TABLE = "Doctors";
    private static final String FILES_TABLE = "Files";

    private static final String DOCTOR_ID = "Sl_No";
    private static final String DOCTOR_NAME = "NAME";
    private static final String DOCTOR_CLINIC = "CLINIC";
    private static final String DOCTOR_NUMBER = "NUMBER";

    private static final String KEY_SL_NO = "slNo";
    private static final String FILE_PATH = "file_path";


    private SQLiteDatabase dbase;


    public LocalDatabase(Context context) {

        super(context, DB_NAME, null, DB_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        dbase =sqLiteDatabase;
        String createCartTable = String.format("CREATE TABLE IF NOT EXISTS %s ( %s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT)",
                DOCOTRS_TABLE, DOCTOR_ID, DOCTOR_NAME,DOCTOR_NUMBER,DOCTOR_CLINIC);
        dbase.execSQL(createCartTable);
        String createExtraTable = String.format("CREATE TABLE IF NOT EXISTS %s ( %s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT)",
                FILES_TABLE , KEY_SL_NO,DOCTOR_ID,FILE_PATH);
        dbase.execSQL(createExtraTable);
    }

    public void insertDoctor(DoctorsList doctorsList){

        dbase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DOCTOR_NAME,doctorsList.getDoctorName());
        contentValues.put(DOCTOR_NUMBER,doctorsList.getDoctorNumber());
        contentValues.put(DOCTOR_CLINIC,doctorsList.getDoctorClinic());
        dbase.insert(DOCOTRS_TABLE,null,contentValues);

    }

    public ArrayList<DoctorsList> getDoctors(){

        ArrayList<DoctorsList> doctorsListArrayList = new ArrayList<>();
        dbase = getReadableDatabase();
        String Query = "SELECT * FROM "+DOCOTRS_TABLE;
        Cursor cursor = dbase.rawQuery(Query,null);
        if(cursor.getCount()!=0){
            while(cursor.moveToNext()){
                DoctorsList dl = new DoctorsList();
                dl.setDoctorId(cursor.getString(cursor.getColumnIndex(DOCTOR_ID)));
                dl.setDoctorName(cursor.getString(cursor.getColumnIndex(DOCTOR_NAME)));
                dl.setDoctorNumber(cursor.getString(cursor.getColumnIndex(DOCTOR_NUMBER)));
                dl.setDoctorClinic(cursor.getString(cursor.getColumnIndex(DOCTOR_CLINIC)));
                doctorsListArrayList.add(dl);
            }
        }
        return doctorsListArrayList;
    }

    public ArrayList<FilesModel> getFiles(String docId){
        ArrayList<FilesModel> filesModelArrayList = new ArrayList<>();
        dbase = getReadableDatabase();
        String Query = "SELECT * FROM "+FILES_TABLE +" WHERE "+DOCTOR_ID+" ='"+docId+"'";
        Cursor cursor = dbase.rawQuery(Query,null);
        if(cursor.getCount()!=0){
            while (cursor.moveToNext()){
                FilesModel filesModel = new FilesModel();
                filesModel.setFileUri(cursor.getString(cursor.getColumnIndex(FILE_PATH)));
                filesModel.setId(cursor.getString(cursor.getColumnIndex(DOCTOR_ID)));
                filesModelArrayList.add(filesModel);
            }
        }
        return filesModelArrayList;
    }

    public void addFile(String docId, String filePath){

        dbase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DOCTOR_ID, docId);
        contentValues.put(FILE_PATH,filePath);
        dbase.insert(FILES_TABLE,null,contentValues);

    }

    public void deleteFile(String docId,String filePath){

        dbase = getWritableDatabase();
        String Query = "DELETE FROM "+FILES_TABLE+" WHERE "+DOCTOR_ID+"='"+docId+"' AND "+FILE_PATH+"='"+filePath+"'";
        dbase.execSQL(Query);

    }

    public void deleteDoctor(String docId){
        dbase = getWritableDatabase();
        String Query = "DELETE FROM "+DOCOTRS_TABLE+" WHERE "+DOCTOR_ID+"='"+docId+"'";
        String Query1 = "DELETE FROM "+FILES_TABLE+" WHERE "+DOCTOR_ID+"='"+docId+"'";

        dbase.execSQL(Query);
        dbase.execSQL(Query1);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

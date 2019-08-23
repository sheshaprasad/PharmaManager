package com.silverbeta.medicinemanager.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.silverbeta.medicinemanager.DBManager.LocalDatabase;
import com.silverbeta.medicinemanager.Interfaces.DeleteDoctorListener;
import com.silverbeta.medicinemanager.Models.DoctorsList;
import com.silverbeta.medicinemanager.R;
import com.silverbeta.medicinemanager.UI.FilesViewActivity;

import java.util.ArrayList;

public class DoctorsRecyclerViewAdapter extends RecyclerView.Adapter<DoctorsRecyclerViewAdapter.ViewHolder> {

    ArrayList<DoctorsList> mDoctorsListArrayList = new ArrayList<>();
    Context mContext;
    private AlertDialog alert;
    LocalDatabase localDatabase;
    DeleteDoctorListener mDeleteDoctorListener;
    public DoctorsRecyclerViewAdapter(Context context, ArrayList<DoctorsList> doctorsLists, DeleteDoctorListener deleteDoctorListener){
        mContext = context;
        mDoctorsListArrayList = doctorsLists;
        localDatabase = new LocalDatabase(mContext);
        mDeleteDoctorListener = deleteDoctorListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(mContext).inflate(R.layout.doctor_list_view_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        if(mDoctorsListArrayList.size()!=0) {
            final DoctorsList dl = mDoctorsListArrayList.get(i);

            viewHolder.doctorName.setText(dl.getDoctorName());
            viewHolder.ClinicName.setText(dl.getDoctorClinic());
            viewHolder.doctorNumber.setText(dl.getDoctorNumber());

            viewHolder.call.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("MissingPermission")
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + dl.getDoctorNumber()));
                    mContext.startActivity(intent);
                }
            });

            viewHolder.files.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent files = new Intent(mContext, FilesViewActivity.class);
                    files.putExtra("id", dl.getDoctorId());
                    files.putExtra("dn", dl.getDoctorName());
                    files.putExtra("cn", dl.getDoctorClinic());
                    mContext.startActivity(files);
                }
            });

            viewHolder.deleteDoctor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LayoutInflater myLayout = LayoutInflater.from(mContext);
                    View dialogView = myLayout.inflate(R.layout.delete_alert, null);
                    final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext);
                    builder.setView(dialogView)
                            .setCancelable(false);

                    TextView cancel = dialogView.findViewById(R.id.cancel);
                    TextView message = dialogView.findViewById(R.id.message);
                    TextView delete = dialogView.findViewById(R.id.deleteDoctor);

                    message.setText("You're about to remove "+dl.getDoctorName()+" from this list");

                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            localDatabase.deleteDoctor(dl.getDoctorId());
                            mDeleteDoctorListener.doctorDeleted();
                            alert.dismiss();
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
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDoctorsListArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView navigation,call,files,deleteDoctor;
        TextView doctorName,ClinicName,doctorNumber;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            navigation = itemView.findViewById(R.id.navigation);
            call = itemView.findViewById(R.id.call);
            deleteDoctor = itemView.findViewById(R.id.deleteDoctor);
            files = itemView.findViewById(R.id.files);
            doctorName = itemView.findViewById(R.id.doctorName);
            ClinicName = itemView.findViewById(R.id.ClinicName);
            doctorNumber = itemView.findViewById(R.id.doctorNumber);

        }
    }
}

package com.silverbeta.medicinemanager.Adapters;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.silverbeta.medicinemanager.DBManager.LocalDatabase;
import com.silverbeta.medicinemanager.Interfaces.FileDeleteListener;
import com.silverbeta.medicinemanager.Models.FilesModel;
import com.silverbeta.medicinemanager.R;
import com.silverbeta.medicinemanager.Utils.TouchImageView;

import java.util.ArrayList;

public class FilesRecyclerViewAdapter extends RecyclerView.Adapter<FilesRecyclerViewAdapter.ViewHolder> {

    Context mContext;
    ArrayList<FilesModel> filesModelArrayList = new ArrayList<>();
    LocalDatabase localDatabase;
    private AlertDialog alert;
    FileDeleteListener mFileDeleteListener;
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    // We can be in one of these 3 states
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    // Remember some things for zooming
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;
    String savedItemClicked;

    public FilesRecyclerViewAdapter(Context context, ArrayList<FilesModel> filesModels, FileDeleteListener fileDeleteListener){
        mContext = context;
        filesModelArrayList = filesModels;
        mFileDeleteListener = fileDeleteListener;
        localDatabase = new LocalDatabase(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.file_list_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        final FilesModel filesModel = filesModelArrayList.get(i);
        viewHolder.file.setImageURI(Uri.parse(filesModel.getFileUri()));
        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater myLayout = LayoutInflater.from(mContext);
                View dialogView = myLayout.inflate(R.layout.file_delete_alert, null);
                final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext);
                builder.setView(dialogView)
                        .setCancelable(false);

                TextView cancel = dialogView.findViewById(R.id.cancel);
                TextView delete = dialogView.findViewById(R.id.deleteDoctor);

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        localDatabase.deleteFile(filesModel.getId(),filesModel.getFileUri());
                        mFileDeleteListener.fileDeleted();
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

        viewHolder.file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.deleteLayout.animate().translationY(-120);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        // do something...
                        viewHolder.deleteLayout.animate().translationY(0);

                    }
                }, 3000);
            }
        });


        viewHolder.left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewHolder.file.getRotation()==0){
                    viewHolder.file.setRotation(270);
                }else if(viewHolder.file.getRotation()==270){
                    viewHolder.file.setRotation(180);
                }else if(viewHolder.file.getRotation()==180){
                    viewHolder.file.setRotation(90);
                }else{
                    viewHolder.file.setRotation(0);
                }
            }
        });
        viewHolder.right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewHolder.file.getRotation()==0){
                    viewHolder.file.setRotation(90);
                }else if(viewHolder.file.getRotation()==90){
                    viewHolder.file.setRotation(180);
                }else if(viewHolder.file.getRotation()==180){
                    viewHolder.file.setRotation(270);
                }else{
                    viewHolder.file.setRotation(0);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return filesModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
       ImageView delete,right,left;
        TouchImageView file;
       LinearLayout deleteLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            file = itemView.findViewById(R.id.file);
            delete = itemView.findViewById(R.id.delete);
            deleteLayout = itemView.findViewById(R.id.deleteLayout);
            right = itemView.findViewById(R.id.right);
            left = itemView.findViewById(R.id.left);
        }
    }

}

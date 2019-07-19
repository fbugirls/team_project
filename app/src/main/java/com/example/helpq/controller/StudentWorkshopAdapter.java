package com.example.helpq.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.example.helpq.R;
import com.example.helpq.model.Workshop;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;

import java.util.List;

public class StudentWorkshopAdapter extends
        RecyclerView.Adapter<StudentWorkshopAdapter.ViewHolder> {

    public static final String TAG = "StudentWorkshopAdapter";
    private Context mContext;
    private List<Workshop> mWorkshops;

    public StudentWorkshopAdapter(Context context, List<Workshop> workshops) {
        this.mContext = context;
        this.mWorkshops = workshops;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(mContext).
                inflate(R.layout.item_student_workshop, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.bind(mWorkshops.get(i));
    }

    @Override
    public int getItemCount() {
        return mWorkshops.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvWorkshopName;
        private TextView tvWorkshopDate;
        private TextView tvWorkshopLocation;
        private TextView tvWorkshopAttendanceCount;
        private Button btnSignUp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWorkshopName = itemView.findViewById(R.id.tvStudentWorkshopName);
            tvWorkshopDate = itemView.findViewById(R.id.tvStudentWorkshopDate);
            tvWorkshopLocation = itemView.findViewById(R.id.tvStudentWorkshopLocation);
            tvWorkshopAttendanceCount = itemView.findViewById(R.id.tvStudentWorkshopSignedUpCount);
            btnSignUp = itemView.findViewById(R.id.btnSignUp);
        }

        public void bind(final Workshop workshop) {
            tvWorkshopName.setText(workshop.getTitle());
            tvWorkshopDate.setText(workshop.getDate());
            tvWorkshopLocation.setText(workshop.getLocation());
            JSONArray attendees = workshop.getAttendees();
            tvWorkshopAttendanceCount.setText(attendees.length() + " attendees");

            btnSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    workshop.setAttendee(ParseUser.getCurrentUser());
                    workshop.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.e(TAG, "error while saving to parse");
                                e.printStackTrace();
                                return;
                            }
                            Log.d(TAG, "Success!");
                        }
                    });
                }
            });
        }
    }
}


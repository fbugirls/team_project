package com.example.helpq.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.helpq.R;
import com.example.helpq.model.Question;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class QueueAdapter extends RecyclerView.Adapter<QueueAdapter.ViewHolder> {

    private static final String TAG = "QueueAdapter";
    private Context mContext;
    private List<Question> mQuestions;

    // Constructor
    public QueueAdapter(Context context, List<Question> questions) {
        this.mContext = context;
        this.mQuestions = questions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_question, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.bind(mQuestions.get(i));
    }

    @Override
    public int getItemCount() {
        return mQuestions.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        mQuestions.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Question> list) {
        mQuestions.addAll(list);
        notifyDataSetChanged();
    }

    //archives question
    private void archiveQuestion(int adapterPosition) {
        Question question = mQuestions.get(adapterPosition);
        question.setIsArchived(true);
        question.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    Toast.makeText(mContext, "Question archived", Toast.LENGTH_LONG).show();
                } else {
                    Log.d(TAG, "failed");
                    e.printStackTrace();
                }
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        // Layout fields of item_question
        private TextView tvStudentName;
        private TextView tvPriorityEmoji;
        private TextView tvDescription;


        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvPriorityEmoji = itemView.findViewById(R.id.tvPriorityEmoji);
            tvDescription = itemView.findViewById(R.id.tvDescription);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ParseUser currentUser = ParseUser.getCurrentUser();
                    if(currentUser.getBoolean("isInstructor") ||
                            currentUser.getString("fullName")
                                    .equals(tvStudentName.getText().toString())) {
                        showFilterPopup(v);
                    }
                    return false;
                }
            });
        }

        // Displays anchored popup menu based on view selected
        private void showFilterPopup(View v) {
            PopupMenu popup = new PopupMenu(mContext, v);
            popup.inflate(R.menu.popup_filters);
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.menu_delete:
                            archiveQuestion(getAdapterPosition());
                            return true;
                        default:
                            return false;
                    }
                }
            });
            popup.show();
        }


        // Bind the view elements to the Question.
        public void bind(Question question) {
            String name = null;
            try {
                name = question.getAsker().fetchIfNeeded().getString("fullName");
            } catch (com.parse.ParseException e) {
                e.printStackTrace();
            }
            tvStudentName.setText(name);
            tvPriorityEmoji.setText(question.getPriority());
            tvDescription.setText(question.getText());
        }
    }

}

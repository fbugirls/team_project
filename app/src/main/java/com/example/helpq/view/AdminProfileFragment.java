package com.example.helpq.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.helpq.R;
import com.example.helpq.model.User;
import com.parse.ParseUser;

public class AdminProfileFragment extends Fragment {

    public static final String TAG = "AdminProfileFragment";

    private ImageView ivProfileImage;
    private TextView tvFullName;
    private  TextView tvUsername;
    private Button btnLogout;

    public static AdminProfileFragment newInstance() {
        return new AdminProfileFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvFullName = view.findViewById(R.id.tvFullName);
        btnLogout = view.findViewById(R.id.btnLogout);
        ivProfileImage = (ImageView) view.findViewById(R.id.ivProfileImage);
        tvFullName.setText(User.getFullName(ParseUser.getCurrentUser()));
        tvUsername.setText(ParseUser.getCurrentUser().getUsername());
        setupLogout();
    }

    // Handle logic for logging out.
    private void setupLogout() {
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                ((MainActivity) getActivity()).finish();
            }
        });
    }
}

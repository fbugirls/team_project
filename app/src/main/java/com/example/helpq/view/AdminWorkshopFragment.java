package com.example.helpq.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.helpq.R;
import com.example.helpq.controller.AdminWorkshopAdapter;
import com.example.helpq.model.DialogDismissListener;
import com.example.helpq.model.Workshop;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdminWorkshopFragment extends Fragment implements DialogDismissListener {
    public static final String TAG = "AdminWorkshopFragment";
    private TextView tvNotice;
    private RecyclerView rvAdminWorkshops;
    private List<Workshop> mWorkshops;
    private AdminWorkshopAdapter adapter;
    private FloatingActionButton fabAddWorkshop;
    private SwipeRefreshLayout swipeContainer;
    private FragmentManager fm;

    public static AdminWorkshopFragment newInstance() {
        return new AdminWorkshopFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_workshop, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tvNotice = view.findViewById(R.id.tvNotice);
        tvNotice.setVisibility(View.GONE);
        rvAdminWorkshops = view.findViewById(R.id.rvAdminWorkshops);
        mWorkshops = new ArrayList<>();
        adapter = new AdminWorkshopAdapter(getContext(), mWorkshops);
        rvAdminWorkshops.setAdapter(adapter);
        rvAdminWorkshops.setLayoutManager(new LinearLayoutManager(getContext()));
        fabAddWorkshop = view.findViewById(R.id.fabAddWorkshop);
        fm = getFragmentManager();
        fabAddWorkshop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateWorkshopFragment createWorkshopFragment =
                        com.example.helpq.view.CreateWorkshopFragment.newInstance("Some Title");
                createWorkshopFragment.setTargetFragment(AdminWorkshopFragment.this,
                        300);
                createWorkshopFragment.show(fm, createWorkshopFragment.TAG);

            }
        });
        queryWorkshops();
        setupSwipeRefreshing(view);
    }

    // Handle logic for Swipe to Refresh.
    private void setupSwipeRefreshing(@NonNull View view) {
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchQueueAsync();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    // Refresh the queue, and load workshops.
    protected void fetchQueueAsync() {
        adapter.clear();
        queryWorkshops();
        swipeContainer.setRefreshing(false);
    }

    private void queryWorkshops() {
        ParseQuery<Workshop> query = ParseQuery.getQuery("Workshop");
        query.whereEqualTo("creator", ParseUser.getCurrentUser());
        query.whereGreaterThan("startTime", new Date(System.currentTimeMillis()));
        query.addAscendingOrder("startTime");
        query.findInBackground(new FindCallback<Workshop>() {
            @Override
            public void done(List<Workshop> objects, ParseException e) {
                if(e == null) {
                    mWorkshops.addAll(objects);
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "adapter notified");
                } else {
                    e.printStackTrace();
                }
                isPageEmpty();
            }
        });
    }

    private void isPageEmpty() {
        if(mWorkshops.size() == 0) {
            tvNotice.setVisibility(View.VISIBLE);
        } else {
            tvNotice.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDismiss() {
        fetchQueueAsync();
    }
}

package com.example.helpq.view;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.helpq.R;
import com.example.helpq.controller.QueueAdapter;
import com.example.helpq.model.DialogDismissListener;
import com.example.helpq.model.QueryFactory;
import com.example.helpq.model.Question;
import com.example.helpq.model.Search;
import com.example.helpq.model.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QueueFragment extends Fragment implements DialogDismissListener {

    public static final String TAG = "QueueFragment";
    private RecyclerView rvQuestions;
    private List<Question> mQuestions;
    private QueueAdapter mAdapter;
    private SwipeRefreshLayout mSwipeContainer;
    private TextView tvNotice;
    private SearchView svQueueSearch;

    public static QueueFragment newInstance() {
        return new QueueFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setRetainInstance(true);
        return inflater.inflate(R.layout.fragment_queue, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvNotice = view.findViewById(R.id.tvNotice);
        tvNotice.setVisibility(View.GONE);
        // Create data source, adapter, and layout manager
        mQuestions = new ArrayList<>();
        mAdapter = new QueueAdapter(getContext(), mQuestions, this);
        rvQuestions = view.findViewById(R.id.rvQuestions);
        rvQuestions.setAdapter(mAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvQuestions.setLayoutManager(layoutManager);
        svQueueSearch = view.findViewById(R.id.svQueueSearch);
        Search.setSearchUi(svQueueSearch, getContext());

        queryQuestions();
        setupSwipeRefreshing(view);
        search();

        mAdapter.setOnItemClickListener(new QueueAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Log.d(TAG, "onItemClick position: " + position);
            }
            @Override
            public void onItemLongClick(int position, View v) {
                Log.d(TAG, "onItemLongClick position: " + position);
            }
        });
    }

    // Handle logic for Swipe to Refresh.
    private void setupSwipeRefreshing(@NonNull View view) {
        mSwipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);

        // Setup refresh listener which triggers new data loading
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchQueueAsync();
            }
        });
        // Configure the refreshing colors
        mSwipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    // Refresh the queue, and load questions.
    public void fetchQueueAsync() {
        mAdapter.clear();
        queryQuestions();
        mSwipeContainer.setRefreshing(false);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getRetainInstance();
    }

    private void queryQuestions() {
        final ParseQuery<Question> query = QueryFactory.QuestionQuery.getQuestionsForQueue();
        query.findInBackground(new FindCallback<Question>() {
            @Override
            public void done(List<Question> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "error with query");
                    e.printStackTrace();
                    return;
                }
                addQuestionsToAdapter(objects);
            }
        });
    }

    private void addQuestionsToAdapter(List<Question> objects) {
        tvNotice.setVisibility(View.GONE);
        mQuestions.addAll(getQueueQuestions(objects));
        Collections.sort(mQuestions);
        mAdapter.notifyDataSetChanged();
        if(mQuestions.size() == 0) {
            tvNotice.setText(getResources().getString(R.string.empty_queue));
            tvNotice.setVisibility(View.VISIBLE);
        }
    }

    // Return the list of questions that should appear on the current user's queue
    // from the given list of objects.
    private List<Question> getQueueQuestions(List<Question> objects) {
        List<Question> questions = new ArrayList<>();
        for (Question question : objects) {
            // who asked the question
            ParseUser asker = question.getAsker();
            // user of who is currently logged in
            String currUser = ParseUser.getCurrentUser().getUsername();
            String currUserAdmin = "";
            if (!User.isAdmin(ParseUser.getCurrentUser())) {
                currUserAdmin = User.getAdminName(ParseUser.getCurrentUser());
            }
            // admin of asker
            String askerAdmin = User.getAdminName(asker);
            if (currUser.equals(askerAdmin) || askerAdmin.equals(currUserAdmin)) {
                questions.add(question);
            }
        }
        return questions;
    }

    @Override
    public void onDismiss() {
        fetchQueueAsync();
    }

    //created at, asker, text, priority, help type, set archived to false
    public void createSnackbar(final int adapterpos, final Question q){

        View.OnClickListener myOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                q.setIsArchived(false);
                q.saveInBackground();
                mQuestions.add(q);
                Collections.sort(mQuestions);
                mAdapter.notifyItemInserted(adapterpos);
                rvQuestions.scrollToPosition(adapterpos);
            }
        };

        Snackbar.make(getView(), R.string.snackbar_text, Snackbar.LENGTH_LONG)
                .setAction(R.string.snackbar_action, myOnClickListener)
                .addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        if(event == DISMISS_EVENT_SWIPE || event == DISMISS_EVENT_TIMEOUT) {
                            mAdapter.deleteQuestion(q);
                        }
                    }
                })
                .show();
    }

    protected void search() {
        svQueueSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                tvNotice.setVisibility(View.GONE);
                final ParseQuery<Question> queueQuestions =
                        QueryFactory.QuestionQuery.getQuestionsForQueue();
                queueQuestions.findInBackground(new FindCallback<Question>() {
                    @Override
                    public void done(List<Question> objects, ParseException e) {
                        List<Question> result = Search.mSearch(getQueueQuestions(objects), query);
                        mQuestions.clear();
                        mQuestions.addAll(result);
                        mAdapter.notifyDataSetChanged();
                        if(mQuestions.isEmpty()) {
                            tvNotice.setText(getResources().getString(R.string.search_notice));
                            tvNotice.setVisibility(View.VISIBLE);
                        }
                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                tvNotice.setVisibility(View.GONE);
                if (newText.isEmpty()) {
                    mQuestions.clear();
                    queryQuestions();
                }
                return false;
            }
        });
    }
}

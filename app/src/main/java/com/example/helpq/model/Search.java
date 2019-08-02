package com.example.helpq.model;

import android.content.Context;
import android.content.res.Resources;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.helpq.R;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;

public class Search {

    private static final int MAX_RESULTS = 10;
    private static final int MIN_SCORE = 50;

    public static List<Question> mSearch(List<Question> objects, String query) {
        List<Question> result = new ArrayList<>();
        List<String> boardQuestionText = new ArrayList<>();
        Hashtable<String, Question> map = new Hashtable<>();
        for(Question question : objects) {
            boardQuestionText.add(question.getText());
            map.put(question.getText(), question);
        }
        List<ExtractedResult> extractList =
                FuzzySearch.extractTop(query, boardQuestionText, MAX_RESULTS);
        for(ExtractedResult extracted : extractList) {
            if (extracted.getScore() > MIN_SCORE) {
                result.add(map.get(extracted.getString()));
            }
        }
        return result;
    }

    public static void setSearchUi(SearchView svSearch, Context context) {
        Resources resource = svSearch.getContext().getResources();
        ImageView closeBtn = ((ImageView) svSearch.findViewById(resource.getIdentifier
                ("android:id/search_close_btn", null, null)));
        closeBtn.setColorFilter(context.getResources().getColor(R.color.colorFaded));
        ((ImageView) svSearch.findViewById(resource
                .getIdentifier("android:id/search_button", null, null)))
                .setColorFilter(context.getResources().getColor(R.color.colorFaded));
        TextView searchText = (TextView) svSearch.findViewById(resource
                .getIdentifier("android:id/search_src_text", null, null));
        searchText.setTextColor(context.getResources().getColor(R.color.colorFaded));
        searchText.setHint("");
    }
}

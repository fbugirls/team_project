package com.example.helpq.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

@ParseClassName("Question")
public class Question extends ParseObject implements Comparable<Question>{

    public static final String KEY_TEXT = "questionText";
    public static final String KEY_ASKER = "student";
    public static final String KEY_PRIORITY = "priorityEmoji";
    public static final String KEY_ARCHIVED = "isArchived";

    //stretch keys
    private static final String KEY_ANSWER = "answerText";
    private static final String KEY_ANSWERED_AT = "answeredAt";

    public String getText() {
        return getString(KEY_TEXT);
    }

    public void setText(String text) {
        put(KEY_TEXT, text);
    }

    public ParseUser getAsker() {
        return getParseUser(KEY_ASKER);
    }

    public void setAsker(ParseUser asker) {
        put(KEY_ASKER, asker);
    }

    public String getPriority() {
        return getString(KEY_PRIORITY);
    }

    public void setPriority(String priority) {
        put(KEY_PRIORITY, priority);
    }

    public boolean getIsArchived () {
        return getBoolean(KEY_ARCHIVED);
    }

    public void setIsArchived (boolean archived) {
        put(KEY_ARCHIVED, archived);
    }


    //stretch getters/setters
    public String getAnswer() {
        return getString(KEY_ANSWER);
    }

    public void setAnswer(String answer) {
        put(KEY_ANSWER, answer);
    }

    public Date getAnsweredAt() {
        return getDate(KEY_ANSWERED_AT);
    }

    public void setAnsweredAt(Date date) {
        put(KEY_ANSWERED_AT, date);
    }

    // Compare questions by their priority.
    @Override
    public int compareTo(Question o) {
        String priority1 = this.getPriority();
        String priority2 = o.getPriority();
        return (priority2.compareTo(priority1));
    }

}

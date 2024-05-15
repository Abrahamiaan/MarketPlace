package com.example.marketplace.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataProvider implements Serializable {
    private static DataProvider instance;
    private List<String> recentQueries;
    private final SharedPreferences sharedPreferences;

    private static final String PREF_NAME = "MyPreferences";
    private static final String RECENT_QUERIES_KEY = "RECENT_QUERIES";

    private DataProvider(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        recentQueries = getSavedRecentQueries();
    }

    public static synchronized DataProvider getInstance(Context context) {
        if (instance == null) {
            instance = new DataProvider(context);
        }
        return instance;
    }

    private List<String> getSavedRecentQueries() {
        List<String> savedQueries = new ArrayList<>();
        String savedQueriesString = sharedPreferences.getString(RECENT_QUERIES_KEY, "");
        if (!savedQueriesString.isEmpty()) {
            String[] queriesArray = savedQueriesString.split(",");
            Collections.addAll(savedQueries, queriesArray);
        }
        return savedQueries;
    }

    public List<String> getRecentQueries() {
        return recentQueries;
    }

    public List<String> getSuggestionQuery(String queryChars) {
        List<String> suggestQueries = new ArrayList<>();
        List<String> savedQueries = getSavedRecentQueries();

        for (String query : savedQueries) {
            if (query.toLowerCase().contains(queryChars.toLowerCase())) {
                suggestQueries.add(query);
            }
        }

        return suggestQueries;
    }

    public void saveSearchQuery(String searchQuery) {
        recentQueries = getSavedRecentQueries();
        recentQueries.remove(searchQuery);
        recentQueries.add(0, searchQuery);
        saveRecentQueriesToPrefs();
    }

    public void removeSearchQuery(String searchQuery) {
        recentQueries.remove(searchQuery);
        saveRecentQueriesToPrefs();
    }

    private void saveRecentQueriesToPrefs() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        StringBuilder queriesStringBuilder = new StringBuilder();
        for (String query : recentQueries) {
            queriesStringBuilder.append(query).append(",");
        }
        editor.putString(RECENT_QUERIES_KEY, queriesStringBuilder.toString());
        editor.apply();
    }
}

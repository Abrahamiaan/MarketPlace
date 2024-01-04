package com.example.marketplace.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;

import java.util.Locale;

public class LocaleHelper {
    public static void setAppLanguage(Context context) {
        String languageCode = getSelectedLanguageCode(context);
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Configuration configuration = new Configuration();
        configuration.setLocale(locale);

        context.getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());
    }

    public static String getSelectedLanguageCode(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return preferences.getString("Selected-Language", "en");
    }
}

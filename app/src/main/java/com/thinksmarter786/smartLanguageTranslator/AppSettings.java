package com.thinksmarter786.smartLanguageTranslator;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.thinksmarter786.smartLanguageTranslator.Utils.SharedPrefs;

public class AppSettings extends AppCompatActivity {
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        toolbar = findViewById(R.id.appbarsettingsToolBar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        private SharedPrefs sharedPrefs;
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            sharedPrefs = SharedPrefs.getInstance(getActivity());
            Preference preferenceTranslate = findPreference("translateText");
            preferenceTranslate.setSummary(sharedPrefs.getString("languageSelected"));
            Preference preferenceshare = findPreference("share");
            preferenceshare.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    ApplicationInfo apinfo = getContext().getApplicationInfo();
                    String apkStr = apinfo.sourceDir;
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    String shareSub = "Hai, I am using smart language translator application, Translate any text to your native language. Which completely helpful. Use the below link and download it now.  \n";
                    shareSub = shareSub + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n";
                    shareIntent.putExtra(Intent.EXTRA_TEXT,shareSub);
                    startActivity(Intent.createChooser(shareIntent,"Share Using"));
                    return true;
                }
            });
            Preference preferencerate = findPreference("feedback");
            preferencerate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    try{
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+getContext().getPackageName())));
                    }catch (ActivityNotFoundException ex){
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id="+getContext().getPackageName())));
                    }
                    return true;
                }
            });
        }
    }
}
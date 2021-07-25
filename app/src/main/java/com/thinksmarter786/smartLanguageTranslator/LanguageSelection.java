package com.thinksmarter786.smartLanguageTranslator;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thinksmarter786.smartLanguageTranslator.Adapters.LanguageAdapter;
import com.thinksmarter786.smartLanguageTranslator.Utils.GetLanguageList;
import com.thinksmarter786.smartLanguageTranslator.Utils.SharedPrefs;
import com.thinksmarter786.smartLanguageTranslator.Utils.TranslateUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.nl.translate.TranslateRemoteModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class LanguageSelection extends AppCompatActivity {
    GetLanguageList getLanguageList;
    RecyclerView recyclerView;
    EditText searchView;
    Button downloadLang;
    AlertDialog dialog = null;
    private SharedPrefs sharedPrefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_selection);
        sharedPrefs = SharedPrefs.getInstance(this);
        recyclerView = findViewById(R.id.languageRecyclerView);
        searchView = findViewById(R.id.searchLanguage);
        downloadLang = findViewById(R.id.downloadLang);
        checkInternetConnectivity();
        getLanguageList = new GetLanguageList();
        List<TranslateUtils.Language> getLangList = getLanguageList.getAvailablelanguages();

        new TranslateUtils(RemoteModelManager.getInstance()).fetchDownloadedModels().addOnSuccessListener(new OnSuccessListener<Set<TranslateRemoteModel>>() {
            @Override
            public void onSuccess(Set<TranslateRemoteModel> translateRemoteModels) {
                Set<String> downloadedLanguage = new HashSet<>();
                List<TranslateUtils.Language> languages = new ArrayList<>();
                Iterator<TranslateRemoteModel> itr = translateRemoteModels.iterator();
                while (itr.hasNext()){
                    downloadedLanguage.add(itr.next().getLanguage());
                }
                for(TranslateUtils.Language languageId : getLangList){
                    if(!downloadedLanguage.contains(languageId.getCode()))
                    languages.add(languageId);
                }
                GridLayoutManager mGridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
                recyclerView.setLayoutManager(mGridLayoutManager);
                LanguageAdapter adapter = new LanguageAdapter(getApplicationContext(),languages);
                recyclerView.setAdapter(adapter);
                searchView.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adapter.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
            }
        });
        downloadLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<TranslateUtils.Language> languages = new ArrayList<>();
                for (TranslateUtils.Language language : getLangList){
                    if(language.isSelected()){
                        languages.add(language);
                    }
                }
                if(languages.isEmpty()){
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.languageSelectionLayout),"Please select atleast one language model",Snackbar.LENGTH_LONG);
                    snackbar.show();
                }else{
                    boolean splashDone = sharedPrefs.getBoolean("splashDone");
                     new TranslateUtils(RemoteModelManager.getInstance()).downloadMultipleLanguage(languages,LanguageSelection.this,splashDone);
                }

            }
        });
    }

    private void checkInternetConnectivity() {
        if (dialog!=null)
            dialog.dismiss();
        if(!isConnected(this)){
            showCustomDialog();
        }
    }

    private void showCustomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LanguageSelection.this);
        builder.setMessage("Please connect to internet.")
                .setCancelable(false)
                .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        dialog = builder.create();
        dialog.show();

    }

    private boolean isConnected(LanguageSelection languageSelection) {
        ConnectivityManager connectivityManager = (ConnectivityManager) languageSelection.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if((wifiInfo!=null && wifiInfo.isConnected()) || (mobileInfo!=null && mobileInfo.isConnected()))
            return true;
        else
            return false;

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkInternetConnectivity();
    }
}
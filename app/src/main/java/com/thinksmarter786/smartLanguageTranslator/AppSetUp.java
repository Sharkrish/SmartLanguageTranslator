package com.thinksmarter786.smartLanguageTranslator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.TranslateRemoteModel;
import com.thinksmarter786.smartLanguageTranslator.Adapters.BottomSheetAdapterModel;
import com.thinksmarter786.smartLanguageTranslator.Utils.SharedPrefs;
import com.thinksmarter786.smartLanguageTranslator.Utils.TranslateUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class AppSetUp extends AppCompatActivity {
    MaterialButton setUpLanguage,setUpDone;
    SharedPrefs sharedPrefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_set_up);
        sharedPrefs = SharedPrefs.getInstance(this);
        setUpLanguage = findViewById(R.id.setUpLanguage);
        setUpDone = findViewById(R.id.setUpDone);
        setUpLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callChangePrimarylanguage();
            }
        });
        setUpDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAppSetUpDone();
            }
        });
    }

    private void isAppSetUpDone() {
        String langSelected = sharedPrefs.getString("languageSelected");
        String langFrom = sharedPrefs.getString("languageFrom");
        if(langFrom.equalsIgnoreCase(langSelected)){
            Snackbar snackbar = Snackbar.make(findViewById(R.id.appsetupRootLayout),"Please select any downloaded language",Snackbar.LENGTH_LONG);
            snackbar.show();
        }
        else
        {
            Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(mainIntent);
        }
    }

    private void callChangePrimarylanguage() {
        String langSelected = sharedPrefs.getString("languageSelected");
        String langFrom = sharedPrefs.getString("languageFrom");
        if(langFrom.equalsIgnoreCase(langSelected)){
            String langFromCode = sharedPrefs.getCode("speechLanguageCode");
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                    AppSetUp.this,R.style.bottomsheetDialog
            );
            View bottonsheetView = LayoutInflater.from(this).inflate(R.layout.speech_bottom_sheet,
                    (LinearLayout)findViewById(R.id.speech_bottom_sheet_layout));
            MaterialTextView bottomSheetTitle = bottonsheetView.findViewById(R.id.bottomSheetTitle);
            RecyclerView recyclerView = bottonsheetView.findViewById(R.id.speechrecyclerView);
            bottomSheetTitle.setText("Choose the primary translate language");
            new TranslateUtils(RemoteModelManager.getInstance()).fetchDownloadedModels().addOnSuccessListener(new OnSuccessListener<Set<TranslateRemoteModel>>() {
                @Override
                public void onSuccess(Set<TranslateRemoteModel> translateRemoteModels) {
                    List<TranslateUtils.Language> modelLanguages = new ArrayList<>();
                    Iterator<TranslateRemoteModel> itr = translateRemoteModels.iterator();
                    while (itr.hasNext()){
                        String lng = itr.next().getLanguage();
                        if (!lng.equalsIgnoreCase(langFromCode)){
                            modelLanguages.add(
                                    new TranslateUtils.Language(TranslateLanguage.fromLanguageTag(lng))
                            );
                        }
                    }
                    Collections.sort(modelLanguages);

                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                        recyclerView.setLayoutManager(linearLayoutManager);
                        BottomSheetAdapterModel adapter = new BottomSheetAdapterModel(AppSetUp.this,modelLanguages,0);
                        recyclerView.setAdapter(adapter);

                        adapter.setOnItemClickListener(new BottomSheetAdapterModel.onItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                for(int i=0;i<modelLanguages.size();i++){
                                    if(i!=position){
                                        recyclerView.getChildAt(i).findViewById(R.id.speech_card_badge).setVisibility(View.INVISIBLE);
                                    }
                                    else{
                                        recyclerView.getChildAt(i).findViewById(R.id.speech_card_badge).setVisibility(View.VISIBLE);
                                    }

                                }
                                sharedPrefs.putString("languageSelected",modelLanguages.get(position).getDisplayName());
                                sharedPrefs.putString("LanguageCode",modelLanguages.get(position).getCode());
                                setUpLanguage.setText(modelLanguages.get(position).getDisplayName());
                            }
                        });
                        bottomSheetDialog.setContentView(bottonsheetView);
                        bottomSheetDialog.show();


                }
            });


        }
    }
}
package com.thinksmarter786.smartLanguageTranslator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thinksmarter786.smartLanguageTranslator.Adapters.BottomSheetAdapterModel;
import com.thinksmarter786.smartLanguageTranslator.Adapters.SpeechDataModelAdapter;
import com.thinksmarter786.smartLanguageTranslator.Models.SpeechLanguageDataModel;
import com.thinksmarter786.smartLanguageTranslator.Utils.SharedPrefs;
import com.thinksmarter786.smartLanguageTranslator.Utils.TranslateSingleton;
import com.thinksmarter786.smartLanguageTranslator.Utils.TranslateUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.TranslateRemoteModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class SpeechTranslate extends Fragment implements View.OnClickListener {
    private static final int RESULT_PERMISSION_From = 786;
    private static final int RESULT_PERMISSION_to = 123;
    MaterialTextView translateFrom,translatedTo;
    ImageView startRecordingFrom,startRecordingTO;
    SharedPrefs sharedPrefs;
    TextView fromLanguage,toLanguage;
    RelativeLayout speakfromLayout,speakToLayout;
    String languageSelected="",languageFrom="";
    TranslateSingleton translateSingleton;
    RecyclerView speech_recyclerView;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.speech_translate,container,false);
        sharedPrefs = SharedPrefs.getInstance(getContext());
        translateSingleton = TranslateSingleton.getInstance();
        speech_recyclerView = view.findViewById(R.id.speech_recyclerView);
        languageSelected = sharedPrefs.getString("languageSelected");
        languageFrom = sharedPrefs.getString("languageFrom");
        fromLanguage = view.findViewById(R.id.fromLanguage);
        toLanguage = view.findViewById(R.id.toLanguage);
        translatedTo = view.findViewById(R.id.translatedTo);
        startRecordingFrom = view.findViewById(R.id.startRecordingFrom);
        startRecordingTO = view.findViewById(R.id.startRecordingTO);
        speakfromLayout = view.findViewById(R.id.speakfromLayout);
        speakToLayout = view.findViewById(R.id.speakToLayout);
        translateFrom = view.findViewById(R.id.translateFrom);
        fromLanguage.setText(languageFrom);
        toLanguage.setText(languageSelected);
        translatedTo.setText(languageSelected);
        translateFrom.setText(languageFrom);
        translateFrom.setOnClickListener(this);
        translatedTo.setOnClickListener(this);
        speakfromLayout.setOnClickListener( this);
        speakToLayout.setOnClickListener( this);
        Set<String> getResponse = sharedPrefs.getObject("speechJson");
        if(getResponse != null){
            ArrayList<String> savedList = new ArrayList<>();
            savedList.addAll(getResponse);
            Collections.sort(savedList,new SortData());
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            speech_recyclerView.setLayoutManager(linearLayoutManager);
            SpeechDataModelAdapter adapter = new SpeechDataModelAdapter(getActivity(),savedList);
            speech_recyclerView.setAdapter(adapter);
            speech_recyclerView.scrollToPosition(savedList.size() -1);
            adapter.setOnItemClickListener(new SpeechDataModelAdapter.onSpeechItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    savedList.remove(position);
                    Collections.sort(savedList,new SortData());
                    Set<String> datatosave = new LinkedHashSet<>();
                    if(getResponse != null)
                        datatosave.addAll(savedList);
                    sharedPrefs.putObject("speechJson", datatosave);
                    adapter.notifyDataSetChanged();
                }
            });

        }

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case RESULT_PERMISSION_From:
                if(resultCode== Activity.RESULT_OK && data != null){
                   ArrayList<String> resultfrom =  data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                   String getDataFrom = resultfrom.get(0).toString();
                   String from = sharedPrefs.getCode("speechLanguageCode");
                   String to = sharedPrefs.getCode("LanguageCode");
                   String langSelected = sharedPrefs.getString("languageSelected");
                   String langFrom = sharedPrefs.getString("languageFrom");
                   translateCall(from,to,getDataFrom,langFrom,langSelected);
                }
                break;
            case RESULT_PERMISSION_to:
                if (resultCode != Activity.RESULT_OK || data == null) {
                } else {
                    ArrayList<String> resultTO =  data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String getDataFrom = resultTO.get(0).toString();
                    String from = sharedPrefs.getCode("LanguageCode");
                    String to = sharedPrefs.getCode("speechLanguageCode");
                    String langSelected = sharedPrefs.getString("languageSelected");
                    String langFrom = sharedPrefs.getString("languageFrom");
                    translateCall(from,to,getDataFrom,langSelected,langFrom);
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + requestCode);
        }
    }

    private void translateCall(String from, String to, String data,String fromName, String toName) {
        Task<String> translatedTextValue = translateSingleton.translate(data,from,to);
        translatedTextValue.addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String convertedResult) {
                Gson gson = new Gson();
                SpeechLanguageDataModel speechLanguageDataModel = new SpeechLanguageDataModel(
                        fromName,
                        toName,
                        from,
                        to,
                        data,
                        convertedResult,
                        new Date()
                );
                String saveJson = gson.toJson(speechLanguageDataModel);
                Set<String> saveJsonObj = sharedPrefs.getObject("speechJson");
                Set<String> datatosave = new LinkedHashSet<>();
                if(saveJsonObj != null){
                  /*  ArrayList<String> resultSort = new ArrayList<>();
                    resultSort.addAll(saveJsonObj);
                    if (resultSort.size() >=5){
                        resultSort.remove(0);
                    }
                    Collections.sort(resultSort,new SortData());*/
                    datatosave.addAll(saveJsonObj);
                }

                datatosave.add(saveJson);
                sharedPrefs.putObject("speechJson", datatosave);
                ArrayList<String> savedList = new ArrayList<>();
                savedList.addAll(datatosave);
                Collections.sort(savedList,new SortData());

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                speech_recyclerView.setLayoutManager(linearLayoutManager);
                SpeechDataModelAdapter adapter = new SpeechDataModelAdapter(getActivity(),savedList);
                speech_recyclerView.setAdapter(adapter);
                speech_recyclerView.scrollToPosition(savedList.size() -1);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View v) {
        boolean adLoadedPause = sharedPrefs.getBoolean("LoadedRewardedAdsfromPaude");
        if(adLoadedPause)
            sharedPrefs.putBoolean("LoadedRewardedAdsfromPaude", false);
        switch (v.getId()){
            case R.id.translateFrom:
                callTranslateFromBottomSheet();
                break;
            case R.id.translatedTo:
                callTranslateToBottomSheet();
                break;
            case R.id.speakfromLayout:
                callRecordingSpeechFrom();
                break;
            case R.id.speakToLayout:
                callRecordingSpeechTo();
                break;
        }
    }
    private void callTranslateToBottomSheet(){
        String lngFrom = sharedPrefs.getCode("speechLanguageCode");
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                getActivity(),R.style.bottomsheetDialog
        );
        View bottonsheetView = LayoutInflater.from(getActivity()).inflate(R.layout.speech_bottom_sheet,
                (LinearLayout)getView().findViewById(R.id.speech_bottom_sheet_layout));
        MaterialTextView bottomSheetTitle = bottonsheetView.findViewById(R.id.bottomSheetTitle);
        RecyclerView recyclerView = bottonsheetView.findViewById(R.id.speechrecyclerView);
        bottomSheetTitle.setText("Choose the primary translate language");

        /**
         * setting the recycler adapter
         */
        new TranslateUtils(RemoteModelManager.getInstance()).fetchDownloadedModels().addOnSuccessListener(new OnSuccessListener<Set<TranslateRemoteModel>>() {
            @Override
            public void onSuccess(Set<TranslateRemoteModel> translateRemoteModels) {
                List<TranslateUtils.Language> modelLanguages = new ArrayList<>();
                Iterator<TranslateRemoteModel> itr = translateRemoteModels.iterator();
                while (itr.hasNext()){
                    String lng = itr.next().getLanguage();

                        modelLanguages.add(
                                new TranslateUtils.Language(TranslateLanguage.fromLanguageTag(lng))
                        );


                }
                Collections.sort(modelLanguages);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(linearLayoutManager);
                BottomSheetAdapterModel adapter = new BottomSheetAdapterModel(getActivity(),modelLanguages,0);
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
                        translatedTo.setText(modelLanguages.get(position).getDisplayName());
                        toLanguage.setText(modelLanguages.get(position).getDisplayName());
                    }
                });
            }
        });

        bottomSheetDialog.setContentView(bottonsheetView);
        bottomSheetDialog.show();
    }
    private void callTranslateFromBottomSheet(){
        String lngSelected = sharedPrefs.getCode("LanguageCode");
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                getActivity(),R.style.bottomsheetDialog
        );
        View bottonsheetView = LayoutInflater.from(getActivity()).inflate(R.layout.speech_bottom_sheet,
                (LinearLayout)getView().findViewById(R.id.speech_bottom_sheet_layout));
        MaterialTextView bottomSheetTitle = bottonsheetView.findViewById(R.id.bottomSheetTitle);
        RecyclerView recyclerViewSpeak = bottonsheetView.findViewById(R.id.speechrecyclerView);
        bottomSheetTitle.setText("Select speak language");
        new TranslateUtils(RemoteModelManager.getInstance()).fetchDownloadedModels().addOnSuccessListener(new OnSuccessListener<Set<TranslateRemoteModel>>() {
            @Override
            public void onSuccess(Set<TranslateRemoteModel> translateRemoteModels) {
                List<TranslateUtils.Language> modelLanguages = new ArrayList<>();
                Iterator<TranslateRemoteModel> itr = translateRemoteModels.iterator();
                while (itr.hasNext()){
                    String lng = itr.next().getLanguage();
                        modelLanguages.add(
                                new TranslateUtils.Language(TranslateLanguage.fromLanguageTag(lng))
                        );
                }
                Collections.sort(modelLanguages);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                recyclerViewSpeak.setLayoutManager(linearLayoutManager);
                BottomSheetAdapterModel adapter = new BottomSheetAdapterModel(getActivity(),modelLanguages,1);
                recyclerViewSpeak.setAdapter(adapter);
                adapter.setOnItemClickListener(new BottomSheetAdapterModel.onItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        for(int i=0;i<modelLanguages.size();i++){
                            if(i!=position){
                                recyclerViewSpeak.getChildAt(i).findViewById(R.id.speech_card_badge).setVisibility(View.INVISIBLE);
                            }
                            else{
                                recyclerViewSpeak.getChildAt(i).findViewById(R.id.speech_card_badge).setVisibility(View.VISIBLE);
                            }

                        }
                        sharedPrefs.putString("languageFrom",modelLanguages.get(position).getDisplayName());
                        sharedPrefs.putString("speechLanguageCode",modelLanguages.get(position).getCode());
                        translateFrom.setText(modelLanguages.get(position).getDisplayName());
                        fromLanguage.setText(modelLanguages.get(position).getDisplayName());
                    }
                });


            }
        });
        bottomSheetDialog.setContentView(bottonsheetView);
        bottomSheetDialog.show();
    }

    private void callRecordingSpeechFrom() {
        String speechLanguageCode = sharedPrefs.getCode("speechLanguageCode");
        Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,new Locale(speechLanguageCode).toString());
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,new Locale(speechLanguageCode).toString());
        if (speechIntent.resolveActivity(getActivity().getPackageManager()) != null){
            startActivityForResult(speechIntent,RESULT_PERMISSION_From);
        }else {
            Toast.makeText(getActivity(),"Your device does not support speech input",Toast.LENGTH_LONG).show();
        }
    }
    private void callRecordingSpeechTo() {
        String speechToLanguageCode = sharedPrefs.getCode("LanguageCode");
        Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,new Locale(speechToLanguageCode).toString());
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,new Locale(speechToLanguageCode).toString());
        if (speechIntent.resolveActivity(getActivity().getPackageManager()) != null){
            startActivityForResult(speechIntent,RESULT_PERMISSION_to);
        }else {
            Toast.makeText(getActivity(),"Your device does not support speech input",Toast.LENGTH_LONG).show();
        }
    }

    private class SortData implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            Gson gson = new Gson();
            SpeechLanguageDataModel s1 = gson.fromJson(o1,SpeechLanguageDataModel.class);
            SpeechLanguageDataModel s2 = gson.fromJson(o2,SpeechLanguageDataModel.class);
            return s1.getDate().compareTo(s2.getDate());
        }
    }
}

package com.thinksmarter786.smartLanguageTranslator;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.thinksmarter786.smartLanguageTranslator.Adapters.BottomSheetAdapterModel;
import com.thinksmarter786.smartLanguageTranslator.Utils.SharedPrefs;
import com.thinksmarter786.smartLanguageTranslator.Utils.TranslateSingleton;
import com.thinksmarter786.smartLanguageTranslator.Utils.TranslateUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.nl.languageid.LanguageIdentification;
import com.google.mlkit.nl.languageid.LanguageIdentifier;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.TranslateRemoteModel;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;


public class TextTranslate extends Fragment {
    private TextToSpeech textToSpeech;
    private LinearLayout translatedLayout;
    private TextView translatedText;
    private ImageButton volumeTheText,playTheTranslation,translatedTextShare,textSpeak,cameraText;
    private MaterialButton translateButton;
    private TextInputEditText translateText;
    TranslateSingleton translateSingleton;
    SharedPrefs sharedPrefs;
    private static final int RESULT_PERMISSION_toTEXT = 548;
    private static final int STORAGE_PERMISSION_CODE = 458;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.text_translate,container,false);
        translateSingleton = TranslateSingleton.getInstance();
        sharedPrefs = SharedPrefs.getInstance(getActivity());
        translateText = view.findViewById(R.id.translateText);
        translateButton = view.findViewById(R.id.translateButton);
        translatedText = view.findViewById(R.id.translatedText);
        translatedLayout = view.findViewById(R.id.translatedLayout);
        playTheTranslation = view.findViewById(R.id.playTheTranslation);
        volumeTheText = view.findViewById(R.id.volumeTheText);
        cameraText = view.findViewById(R.id.cameraText);
        textSpeak = view.findViewById(R.id.textSpeak);
        translatedTextShare = view.findViewById(R.id.translatedTextShare);
        translatedTextShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String shareSub = translatedText.getText().toString()+"\n\n";
                shareSub = shareSub + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
                shareIntent.putExtra(Intent.EXTRA_TEXT,shareSub);
                startActivity(Intent.createChooser(shareIntent,"Share Using"));
            }
        });
        textToSpeech = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i!=TextToSpeech.ERROR){
                    textToSpeech.setLanguage(Locale.ENGLISH);
                }
            }
        });
        volumeTheText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = translateText.getText().toString();
                callLanguageDetector(value,0);
            }
        });
        playTheTranslation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = sharedPrefs.getCode("LanguageCode");
                textToSpeech.setLanguage(new Locale(code));
                textToSpeech.speak(translatedText.getText().toString(),TextToSpeech.QUEUE_FLUSH,null);
            }
        });
        translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = translateText.getText().toString();
                if(!value.isEmpty()){
                    translateButton.setText("Translating");
                    Log.d("TexttoTranslate",""+value);
                    callLanguageDetector(value,1);
                }
            }
        });
        textSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                        getActivity(),R.style.bottomsheetDialog
                );
                View bottonsheetView = LayoutInflater.from(getActivity()).inflate(R.layout.speech_bottom_sheet,
                        (LinearLayout)getView().findViewById(R.id.speech_bottom_sheet_layout));
                MaterialTextView bottomSheetTitle = bottonsheetView.findViewById(R.id.bottomSheetTitle);
                RecyclerView recyclerViewSpeak = bottonsheetView.findViewById(R.id.speechrecyclerView);
                bottomSheetTitle.setText("Select speak language");
                bottomSheetDialog.setContentView(bottonsheetView);
                bottomSheetDialog.show();
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
                                bottomSheetDialog.dismiss();
                                callSpeak(modelLanguages.get(position).getCode());

                            }
                        });


                    }
                });

            }
        });
        cameraText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean dontShow = sharedPrefs.getBoolean("dontshow");

              boolean permissionGrant = checkForPermission();
              if(permissionGrant){
                  if(!dontShow){
                      Dialog dialog = new Dialog(getActivity());
                      dialog.setContentView(R.layout.alert_dialog_lang);
                      dialog.getWindow().setBackgroundDrawableResource(
                              android.R.color.transparent
                      );
                      dialog.setCancelable(false);
                      dialog.show();
                      MaterialButton okButton = dialog.findViewById(R.id.okDismiss);
                      MaterialCheckBox dnCheck = dialog.findViewById(R.id.dntShow);
                      okButton.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View v) {
                              sharedPrefs.putBoolean("dontshow",dnCheck.isChecked());
                              dialog.dismiss();
                              callCameraandgallery();
                          }
                      });

                  }else{
                      callCameraandgallery();
                  }

              }

            }
        });

        return view;
    }
    private void callCameraandgallery() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(getContext(), this);
    }
    private boolean checkForPermission() {
        List<String> listPermissionsNeeded = new ArrayList<>();
        listPermissionsNeeded.clear();
        int writeStorage = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readStorage = ContextCompat.checkSelfPermission( getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int internet = ContextCompat.checkSelfPermission( getActivity(), Manifest.permission.INTERNET);
        int internetAccess = ContextCompat.checkSelfPermission( getActivity(), Manifest.permission.ACCESS_NETWORK_STATE);
        int wifiAccess = ContextCompat.checkSelfPermission( getActivity(), Manifest.permission.ACCESS_WIFI_STATE);
        int cameraAccess = ContextCompat.checkSelfPermission( getActivity(), Manifest.permission.CAMERA);
        if (writeStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (readStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (internet != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.INTERNET);
        }
        if (internetAccess != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_NETWORK_STATE);
        }
        if (wifiAccess != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_WIFI_STATE);
        }
        if (cameraAccess != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (!listPermissionsNeeded.isEmpty())
        {
            requestPermissions(listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]),STORAGE_PERMISSION_CODE);
            return false;
        }
        return true;
    }

    private void callSpeak(String langCode){
        boolean adLoadedPause = sharedPrefs.getBoolean("LoadedRewardedAdsfromPaude");
        if(adLoadedPause)
            sharedPrefs.putBoolean("LoadedRewardedAdsfromPaude", false);
        Intent speechIntentText = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntentText.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntentText.putExtra(RecognizerIntent.EXTRA_LANGUAGE,new Locale(langCode).toString());
        speechIntentText.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,new Locale(langCode).toString());
        if (speechIntentText.resolveActivity(getActivity().getPackageManager()) != null){
            startActivityForResult(speechIntentText,RESULT_PERMISSION_toTEXT);
        }else {
            Toast.makeText(getActivity(),"Your device does not support speech input",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case STORAGE_PERMISSION_CODE:
                int deniedCount =0;
                for(int i=0; i< grantResults.length;i++){
                    if(grantResults[i] == PackageManager.PERMISSION_DENIED)
                        deniedCount++;
                }
                if(deniedCount ==0)
                    callCameraandgallery();
                else{
                    checkForPermission();
                }
            break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case RESULT_PERMISSION_toTEXT:
                if (resultCode != Activity.RESULT_OK || data == null) {
                } else {
                    ArrayList<String> resultTO =  data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String getDataFrom = resultTO.get(0).toString();
                    translateText.setText(getDataFrom);
                    translateButton.setText("Translating");
                    callLanguageDetector(getDataFrom,1);

                }
                break;
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == Activity.RESULT_OK) {
                    Uri resultUri = result.getUri();
                    Bitmap resultBitmap = null;
                    try {
                        resultBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),resultUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(resultBitmap != null)
                        callTextRecognizer(resultBitmap);
                    else
                        Toast.makeText(getContext(),"Unable to parse the image",Toast.LENGTH_LONG).show();
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
                break;

        }
    }

    private void callTextRecognizer(Bitmap bitmap) {
        TextRecognizer txtRecognizer = new TextRecognizer.Builder(getActivity()).build();
        if (!txtRecognizer.isOperational()) {
        }else{
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray items = txtRecognizer.detect(frame);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < items.size(); i++) {
                TextBlock item = (TextBlock) items.valueAt(i);
                sb.append(item.getValue());
            }
            if(!sb.toString().isEmpty()){
                translateText.setText(sb.toString());
                /*translateButton.setText("Translating");
                callLanguageDetector(sb.toString(),1);*/
            }
        }
    }

    private void callLanguageDetector(String selectedContent, int mode) {
        if(!selectedContent.isEmpty()){
            Log.d("TexttoTranslate",""+selectedContent);
            LanguageIdentifier languageIdentifier = LanguageIdentification.getClient();
            Log.d("TexttoTranslate","1");
            languageIdentifier.identifyLanguage(selectedContent)
                    .addOnSuccessListener(
                            new OnSuccessListener<String>() {
                                @Override
                                public void onSuccess(@Nullable String languageCode) {
                                    if (languageCode.equals("und")) {
                                        translatedLayout.setVisibility(View.INVISIBLE);
                                        translateButton.setText("Translate");
                                        callSnackbar();
                                    } else {
                                        if(mode == 1){
                                            Log.d("TexttoTranslate",""+languageCode);
                                            callTranslateText(selectedContent,languageCode);
                                        }

                                        else{
                                            textToSpeech.setLanguage(new Locale(languageCode));
                                            textToSpeech.speak(selectedContent,TextToSpeech.QUEUE_FLUSH,null);
                                        }

                                    }
                                }

                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("Error detecting", e.getMessage());
                                    Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.textRootLayout),"Something went wrong...",Snackbar.LENGTH_LONG);
                                    snackbar.show();
                                }
                            }
                    );
        }
    }

    private void callSnackbar() {
        Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.textRootLayout),"Unable to detect the language. Please use the Voice translation",Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void callTranslateText(String selectedContent, String languageCode) {
        boolean adLoadedPause = sharedPrefs.getBoolean("LoadedRewardedAdsfromPaude");
        if(adLoadedPause)
            sharedPrefs.putBoolean("LoadedRewardedAdsfromPaude", false);

        String to = sharedPrefs.getCode("LanguageCode");
        Log.d("TexttoTranslate: ",""+to);
        Task<String> translatedTextValue = translateSingleton.translate(selectedContent,languageCode,to);
        translatedTextValue.addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String convertedResult) {
                translatedLayout.setVisibility(View.VISIBLE);
                translatedText.setText(convertedResult);
                translateButton.setText("Translate");
            }
        });
    }
}

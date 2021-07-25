package com.thinksmarter786.smartLanguageTranslator.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.LruCache;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.thinksmarter786.smartLanguageTranslator.AppSetUp;
import com.thinksmarter786.smartLanguageTranslator.MainActivity;
import com.thinksmarter786.smartLanguageTranslator.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.TranslateRemoteModel;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class TranslateUtils extends ViewModel {
    private static final int Num_translator = 3;

     private ProgressDialog progressDialog;
    private final RemoteModelManager modelManager;
    private final LruCache<TranslatorOptions, Translator> translator
            = new LruCache<TranslatorOptions, Translator>(Num_translator){
        @Override
        protected Translator create(TranslatorOptions key) {
            return Translation.getClient(key);
        }

        @Override
        protected void entryRemoved(boolean evicted, TranslatorOptions key, Translator oldValue, Translator newValue) {
            oldValue.close();
        }
    };
    MutableLiveData<TranslateLanguage.Language> sourceLang = new MutableLiveData<>();
    MutableLiveData<TranslateLanguage.Language> targetLang = new MutableLiveData<>();
    MutableLiveData<String> sourceText = new MutableLiveData<>();
    MediatorLiveData<ResultOrError> translatedText = new MediatorLiveData<ResultOrError>();
    public MutableLiveData<List<String>> availableModel = new MutableLiveData<>();
    public TranslateUtils(RemoteModelManager modelManager) {
        this.modelManager = RemoteModelManager.getInstance();
        /*final OnCompleteListener<String> processTranslation = new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()){
                    translatedText.setValue(new ResultOrError(task.getResult(),null));
                }else
                {
                    translatedText.setValue(new ResultOrError(null,task.getException()));
                }
            }

        };*/
        /*translatedText.addSource(sourceText, new Observer<TranslateLanguage.Language>() {
            @Override
            public void onChanged(String s) {
                translate().addOnCompleteListener(processTranslation);
            }
        });*/

        /*Observer<Language> languageObserver = new Observer<Language>() {
            @Override
            public void onChanged(Language language) {
                translate().addOnCompleteListener(processTranslation);
            }
        };*/
        /*translatedText.addSource(sourceLang,languageObserver);
        translatedText.addSource(targetLang,languageObserver);*/
        //fetchDownloadedModels();
    }

    private TranslateRemoteModel getModel(String languageCode){
        return new TranslateRemoteModel.Builder(languageCode).build();
    }

    public void downloadMultipleLanguage(ArrayList<Language> languages, Context context,boolean splashDone) {
        progressDialog = new ProgressDialog(context);
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );
        int i =0;
        int size = languages.size();
        while (i < size){
            Language language = languages.get(i);
            downloadLanguage(language,size,i,context,splashDone);
            i++;
        }

    }

    public void downloadLanguage(Language language, int size, int i, Context context,boolean splashDone){
        TranslateRemoteModel model =
                getModel(TranslateLanguage.fromLanguageTag(language.getCode()));
         modelManager.download(model, new DownloadConditions.Builder().build())
                 .addOnCompleteListener(new OnCompleteListener<Void>() {
                     @Override
                     public void onComplete(@NonNull Task<Void> task) {
                         if((size-1) == i)
                         {
                             progressDialog.dismiss();
                             if(splashDone){
                                 Intent mainIntent = new Intent(context, MainActivity.class);
                                 context.startActivity(mainIntent);
                             }else{
                                 Intent mainAppSetUpIntent = new Intent(context, AppSetUp.class);
                                 context.startActivity(mainAppSetUpIntent);

                             }

                         }
                     }
                 });
    }
    public Task<Void> deleteLanguage(Language language){
        TranslateRemoteModel model =
                getModel(TranslateLanguage.fromLanguageTag(language.getCode()));
        return modelManager.deleteDownloadedModel(model).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            }
        });
    }

    /*public Task<String> translate(){
        final String text = sourceText.getValue();
        final Language source = sourceLang.getValue();
        final Language target = targetLang.getValue();
        if(source == null || target == null || text == null || text.isEmpty()){
            return Tasks.forResult("");
        }
        String sourceLanguageCode = TranslateLanguage.fromLanguageTag(source.getCode());
        String targetLanguageCode = TranslateLanguage.fromLanguageTag(target.getCode());
        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(sourceLanguageCode)
                .setTargetLanguage(targetLanguageCode)
                .build();
        return translator.get(options).downloadModelIfNeeded().continueWithTask(
                new Continuation<Void, Task<String>>() {
                    @Override
                    public Task<String> then(@NonNull Task<Void> task) throws Exception {
                        if(task.isSuccessful()){
                            return translator.get(options).translate(text);
                        }
                        else{
                            Exception e = task.getException();
                            if(e == null){
                                e = new Exception("Encounter an exception")
                            }
                            return Tasks.forException(e);
                        }
                        return null;
                    }
                }
        );
    }*/

    public Task<Set<TranslateRemoteModel>> fetchDownloadedModels(){
        return modelManager.getDownloadedModels(TranslateRemoteModel.class).addOnSuccessListener(
                new OnSuccessListener<Set<TranslateRemoteModel>>() {
                    @Override
                    public void onSuccess(Set<TranslateRemoteModel> translateRemoteModels) {
                        List<String> languageDownloaded = new ArrayList<>(translateRemoteModels.size());
                        for (TranslateRemoteModel model : translateRemoteModels){
                            languageDownloaded.add(model.getLanguage());
                        }
                        Collections.sort(languageDownloaded);
                    }
                }
        );
    }



    static class ResultOrError {
        final @Nullable String result;
        final @Nullable Exception error;

        ResultOrError(@Nullable String result, @Nullable Exception error){
            this.result = result;
            this.error = error;
        }

    }

    public static class Language implements Comparable<Language>{
        private String code;
        private boolean isSelected = false;
        public Language(String code){
            this.code = code;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }

        public String getDisplayName(){
            return new Locale(code).getDisplayName();
        }
        public String getCode(){
            return code;
        }
        public boolean equals(Object o){
            if(o == this){
                return  true;
            }
            if(!(o instanceof Language)){
                return false;
            }
            Language otherLang = (Language) o;
            return otherLang.code.equals(code);
        }

        @NonNull
        @Override
        public String toString() {
            return code+"-"+getDisplayName();
        }

        @Override
        public int hashCode() {
            return code.hashCode();
        }

        @Override
        public int compareTo(Language o) {
            return this.getDisplayName().compareTo(o.getDisplayName());
        }
    }

}

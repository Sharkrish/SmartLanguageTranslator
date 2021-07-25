package com.thinksmarter786.smartLanguageTranslator.Utils;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

public class TranslateSingleton {

    private RemoteModelManager modelManager;
    private TranslateSingleton(){
        modelManager = RemoteModelManager.getInstance();
    }

    public static TranslateSingleton getInstance(){
        return new TranslateSingleton();
    }

    public Task<String> translate(String sourceText, String sourceLang,String targetlang){
        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(sourceLang)
                        .setTargetLanguage(targetlang)
                        .build();
        final Translator englishGermanTranslator =
                Translation.getClient(options);
       return englishGermanTranslator.downloadModelIfNeeded().continueWithTask(
                new Continuation<Void, Task<String>>() {
                    @Override
                    public Task<String> then(@NonNull Task<Void> task) throws Exception {
                        if(task.isSuccessful()){
                            return englishGermanTranslator.translate(sourceText);
                        }
                        else{
                            Exception e = task.getException();
                            if(e == null){
                                e = new Exception("Encounter an exception");
                            }
                            return Tasks.forException(e);
                        }
                    }
                }
        );
    }

}

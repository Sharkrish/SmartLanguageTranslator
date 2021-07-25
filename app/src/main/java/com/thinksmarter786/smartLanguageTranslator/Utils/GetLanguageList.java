package com.thinksmarter786.smartLanguageTranslator.Utils;

import com.google.mlkit.nl.translate.TranslateLanguage;

import java.util.ArrayList;
import java.util.List;

public class GetLanguageList {

    public List<TranslateUtils.Language> getAvailablelanguages(){
        List<TranslateUtils.Language> languages = new ArrayList<>();
        List<String> languageIds = TranslateLanguage.getAllLanguages();
        for(String languageId : languageIds){
            languages.add(
                    new TranslateUtils.Language(TranslateLanguage.fromLanguageTag(languageId))
            );
        }
        return languages;
    }
}

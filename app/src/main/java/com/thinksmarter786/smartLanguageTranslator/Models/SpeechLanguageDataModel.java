package com.thinksmarter786.smartLanguageTranslator.Models;

import java.util.Date;

public class SpeechLanguageDataModel {
    private String fromLanguage,toLanguage,fromContent,toContent,fromCode,toCode;
    private Date date;

    public SpeechLanguageDataModel(String fromLanguage, String toLanguage, String fromCode, String toCode, String fromContent, String toContent, Date date) {
        this.fromLanguage = fromLanguage;
        this.toLanguage = toLanguage;
        this.fromContent = fromContent;
        this.toContent = toContent;
        this.fromCode = fromCode;
        this.toCode = toCode;
        this.date = date;
    }

    public String getFromLanguage() {
        return fromLanguage;
    }

    public void setFromLanguage(String fromLanguage) {
        this.fromLanguage = fromLanguage;
    }

    public String getToLanguage() {
        return toLanguage;
    }

    public void setToLanguage(String toLanguage) {
        this.toLanguage = toLanguage;
    }

    public String getFromContent() {
        return fromContent;
    }

    public void setFromContent(String fromContent) {
        this.fromContent = fromContent;
    }

    public String getToContent() {
        return toContent;
    }

    public void setToContent(String toContent) {
        this.toContent = toContent;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getFromCode() {
        return fromCode;
    }

    public void setFromCode(String fromCode) {
        this.fromCode = fromCode;
    }

    public String getToCode() {
        return toCode;
    }

    public void setToCode(String toCode) {
        this.toCode = toCode;
    }
}

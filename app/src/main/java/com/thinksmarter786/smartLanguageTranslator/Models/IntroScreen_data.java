package com.thinksmarter786.smartLanguageTranslator.Models;

public class IntroScreen_data {
    private String title;
    private String desc;

    public IntroScreen_data(String title,String desc) {
        this.title = title;
        this.desc = desc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}

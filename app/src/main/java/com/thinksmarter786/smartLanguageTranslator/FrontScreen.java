package com.thinksmarter786.smartLanguageTranslator;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.thinksmarter786.smartLanguageTranslator.Adapters.IntroViewPageAdapter;
import com.thinksmarter786.smartLanguageTranslator.Models.IntroScreen_data;
import com.thinksmarter786.smartLanguageTranslator.Utils.SharedPrefs;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class FrontScreen extends AppCompatActivity {
    ViewPager viewPager;
    TabLayout tabLayout;
    Button  nextButton;
    Button getStarted;
    int position = 0;
    Animation btnAnimate;
    SharedPrefs sharedPrefs;
    RelativeLayout splashIconScreen,splaceInitial;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_screen);
        splashSetup();

    }

    private void splashSetup() {
        sharedPrefs = SharedPrefs.getInstance(this);
        splaceInitial = findViewById(R.id.splaceInitial);
        splashIconScreen = findViewById(R.id.splashIconScreen);
        boolean splashDone = sharedPrefs.getBoolean("splashDone");
        if(splashDone)
        {
            // call splash screen
            splashIconScreen.setVisibility(View.VISIBLE);
            splaceInitial.setVisibility(View.GONE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    String langSelected = sharedPrefs.getString("languageSelected");
                    String langFrom = sharedPrefs.getString("languageFrom");
                    if(langFrom.equalsIgnoreCase(langSelected)){
                        Intent mainIntent = new Intent(getApplicationContext(), AppSetUp.class);
                        startActivity(mainIntent);
                        finish();
                    }else{
                        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(mainIntent);
                        finish();
                    }

                }
            },3000);

        }else{
            splashIconScreen.setVisibility(View.GONE);
            splaceInitial.setVisibility(View.VISIBLE);
            init();
        }
    }

    private void init() {
        viewPager =findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabIndicator);
        nextButton = findViewById(R.id.nextButton);
        getStarted = findViewById(R.id.getStarted);
        btnAnimate = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.button_animation);
        ArrayList<IntroScreen_data> introScreen_data = new ArrayList<>();
        introScreen_data.add(new IntroScreen_data("Smart language translator","Translate any language to your native/primary language. It supports 100+ languages. Input/Copied/Voice text will automatically detect the language will translate to your native language."));
        introScreen_data.add(new IntroScreen_data("Choose language","We recommend to download the language model to detect and translate the language in offline mode. The language model may take some while to download depends upon your network connectivity."));
        IntroViewPageAdapter viewPageAdapter = new IntroViewPageAdapter(this,introScreen_data);
        viewPager.setAdapter(viewPageAdapter);
        tabLayout.setupWithViewPager(viewPager);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position = viewPager.getCurrentItem();
                if(position < introScreen_data.size()){
                    position++;
                    viewPager.setCurrentItem(position);
                    nextButton.setVisibility(View.VISIBLE);
                    tabLayout.setVisibility(View.INVISIBLE);
                }

                if(position == introScreen_data.size()-1){
                    loadLastScreen();
                }
            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == introScreen_data.size()-1){
                    loadLastScreen();
                }
                else {
                    viewPager.setCurrentItem(tab.getPosition());
                    nextButton.setVisibility(View.VISIBLE);
                    tabLayout.setVisibility(View.INVISIBLE);
                    getStarted.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent langIntent = new Intent(getApplicationContext(),LanguageSelection.class);
                startActivity(langIntent);
            }
        });
    }


    private void loadLastScreen() {
        nextButton.setVisibility(View.INVISIBLE);
        tabLayout.setVisibility(View.INVISIBLE);
        getStarted.setVisibility(View.VISIBLE);
        getStarted.setAnimation(btnAnimate);
    }

    @Override
    protected void onResume() {
        super.onResume();
        splashSetup();
    }
}
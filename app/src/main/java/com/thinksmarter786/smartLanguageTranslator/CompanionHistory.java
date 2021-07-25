package com.thinksmarter786.smartLanguageTranslator;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.thinksmarter786.smartLanguageTranslator.Adapters.TranslateAdapter;
import com.google.android.material.tabs.TabLayout;
import com.thinksmarter786.smartLanguageTranslator.Service.BackgroundService;

public class CompanionHistory extends Fragment {
    private RecyclerView recyclerView;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public static Fragment getInstance() {
        return new CompanionHistory();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.translate_view,container,false);

        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.translate_viewpager);
        TranslateAdapter translateAdapter = new TranslateAdapter(getChildFragmentManager(),getActivity());
        translateAdapter.addFragement(new TextTranslate(),"Text");
        translateAdapter.addFragement(new SpeechTranslate(),"Voice");
        //translateAdapter.addFragement(new ImageTranslate(),"Image");
        viewPager.setAdapter(translateAdapter);
        tabLayout.setupWithViewPager(viewPager);
        Intent svc = new Intent(getActivity(), BackgroundService.class);
        getActivity().startService(svc);
        return view;
    }
}

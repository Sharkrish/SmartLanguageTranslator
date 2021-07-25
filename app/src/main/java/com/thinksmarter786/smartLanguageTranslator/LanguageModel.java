package com.thinksmarter786.smartLanguageTranslator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thinksmarter786.smartLanguageTranslator.Adapters.LanguageModelAdapter;
import com.thinksmarter786.smartLanguageTranslator.Utils.SharedPrefs;
import com.thinksmarter786.smartLanguageTranslator.Utils.TranslateUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.TranslateRemoteModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class LanguageModel extends Fragment {
    private RecyclerView recyclerView;
    private LinearProgressIndicator progressBar;
    private SharedPrefs sharedPrefs;
    private TextView primarylanguage;
    public static Fragment getInstance() {
        return new LanguageModel();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.language_model,container,false);
        recyclerView = view.findViewById(R.id.languageModelRecyclerView);
        sharedPrefs = SharedPrefs.getInstance(getContext());
        String languageSelected = sharedPrefs.getString("languageSelected");
        progressBar = view.findViewById(R.id.progress_horizontal);
        primarylanguage = view.findViewById(R.id.languageSelected);
        primarylanguage.setText(languageSelected);
        new TranslateUtils(RemoteModelManager.getInstance()).fetchDownloadedModels().addOnSuccessListener(new OnSuccessListener<Set<TranslateRemoteModel>>() {

            @Override
            public void onSuccess(Set<TranslateRemoteModel> translateRemoteModels) {
                progressBar.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                List<TranslateUtils.Language> modelLanguages = new ArrayList<>();
                Iterator<TranslateRemoteModel> itr = translateRemoteModels.iterator();
                while (itr.hasNext()){
                    modelLanguages.add(
                            new TranslateUtils.Language(TranslateLanguage.fromLanguageTag(itr.next().getLanguage()))
                    );
                }
                Collections.sort(modelLanguages);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(linearLayoutManager);
                LanguageModelAdapter adapter = new LanguageModelAdapter(getActivity(),modelLanguages);
                recyclerView.setAdapter(adapter);
                adapter.setOnItemClickListener(new LanguageModelAdapter.onItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        for(int i=0;i<modelLanguages.size();i++){
                            if(i!=position){
                                recyclerView.getChildAt(i).findViewById(R.id.primaryLanguageSelection).setVisibility(View.INVISIBLE);
                                recyclerView.getChildAt(i).findViewById(R.id.delete_badge).setVisibility(View.VISIBLE);
                            }
                            else{
                                recyclerView.getChildAt(i).findViewById(R.id.primaryLanguageSelection).setVisibility(View.VISIBLE);
                                recyclerView.getChildAt(i).findViewById(R.id.delete_badge).setVisibility(View.INVISIBLE);
                            }

                        }
                        sharedPrefs.putString("languageSelected",modelLanguages.get(position).getDisplayName());
                        sharedPrefs.putString("LanguageCode",modelLanguages.get(position).getCode());
                        primarylanguage.setText(modelLanguages.get(position).getDisplayName());
                    }
                });
            }
        });

        return view;
    }
}

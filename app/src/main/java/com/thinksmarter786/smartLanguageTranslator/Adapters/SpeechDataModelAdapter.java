package com.thinksmarter786.smartLanguageTranslator.Adapters;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thinksmarter786.smartLanguageTranslator.Models.SpeechLanguageDataModel;
import com.thinksmarter786.smartLanguageTranslator.R;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Locale;

public class SpeechDataModelAdapter extends RecyclerView.Adapter<SpeechDataModelAdapter.SpeechDataModelView> {

    private Context context;
    private ArrayList<String> speechDataSaved;
    private TextToSpeech textToSpeech;
    private onSpeechItemClickListener speechListener;
    public SpeechDataModelAdapter(Context context, ArrayList<String> speechDataSaved){
        this.context = context;
        this.speechDataSaved = speechDataSaved;
            textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int i) {
                    if(i!=TextToSpeech.ERROR){
                        textToSpeech.setLanguage(Locale.ENGLISH);
                    }
                }
            });
    }


    @NonNull
    @Override
    public SpeechDataModelView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.speech_translate_data, parent, false);
        return new SpeechDataModelView(mView,speechListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SpeechDataModelView holder, int position) {
        Gson gson = new Gson();
        String json_data = speechDataSaved.get(position);
        SpeechLanguageDataModel speechLanguageDataModel = gson.fromJson(json_data,SpeechLanguageDataModel.class);
        holder.fromTo.setText(speechLanguageDataModel.getFromLanguage() +" to " + speechLanguageDataModel.getToLanguage());
        holder.fromtext.setText(speechLanguageDataModel.getFromContent());
        holder.totext.setText(speechLanguageDataModel.getToContent());
        holder.fromVolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textToSpeech.setLanguage(new Locale(speechLanguageDataModel.getFromCode()));
                textToSpeech.speak(speechLanguageDataModel.getFromContent(),TextToSpeech.QUEUE_FLUSH,null);
            }
        });
        holder.toVolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textToSpeech.setLanguage(new Locale(speechLanguageDataModel.getToCode()));
                textToSpeech.speak(speechLanguageDataModel.getToContent(),TextToSpeech.QUEUE_FLUSH,null);
            }
        });
    }

    @Override
    public int getItemCount() {
        return speechDataSaved==null?0:speechDataSaved.size();
    }
    public interface onSpeechItemClickListener{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(onSpeechItemClickListener listener){
        speechListener = listener;
    }
    public class SpeechDataModelView extends RecyclerView.ViewHolder{
        MaterialTextView fromTo,fromtext, totext,deleteSaved;
        ImageButton fromVolume,toVolume;
        public SpeechDataModelView(@NonNull View itemView, onSpeechItemClickListener listener) {
            super(itemView);
            fromTo = itemView.findViewById(R.id.fromTo);
            fromtext = itemView.findViewById(R.id.fromtext);
            totext = itemView.findViewById(R.id.totext);
            fromVolume = itemView.findViewById(R.id.fromVolume);
            toVolume = itemView.findViewById(R.id.toVolume);
            deleteSaved = itemView.findViewById(R.id.deleteSaved);
            deleteSaved.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null){
                        int position = getAbsoluteAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });

        }
    }
}

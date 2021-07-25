package com.thinksmarter786.smartLanguageTranslator.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thinksmarter786.smartLanguageTranslator.R;
import com.thinksmarter786.smartLanguageTranslator.Utils.SharedPrefs;
import com.thinksmarter786.smartLanguageTranslator.Utils.TranslateUtils;

import java.util.List;

public class BottomSheetAdapterModel extends RecyclerView.Adapter<BottomSheetAdapterModel.BottomSheetAdapterModelViewHolder> {
    private Activity activity;
    private List<TranslateUtils.Language> languageModelList;
    private SharedPrefs sharedPrefs;
    private onItemClickListener mListener;
    private int mode = 0;
    public BottomSheetAdapterModel(Activity activity, List<TranslateUtils.Language> languageModelList,int mode) {
        this.activity = activity;
        this.languageModelList = languageModelList;
        this.sharedPrefs = SharedPrefs.getInstance(activity);
        this.mode = mode;
    }

    @NonNull
    @Override
    public BottomSheetAdapterModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bottom_sheet_card, parent, false);
        return new BottomSheetAdapterModelViewHolder(mView,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull BottomSheetAdapterModelViewHolder holder, int position) {
        TranslateUtils.Language language = languageModelList.get(position);
        if(!language.getDisplayName().trim().isEmpty()){
            holder.speakModelName.setText(language.getDisplayName());
            holder.speakModelCode.setText(language.getCode());
            String languageSelected="";
            if(mode==0)
                languageSelected = sharedPrefs.getString("languageSelected");
             else
                languageSelected= sharedPrefs.getString("languageFrom");

             if(!languageSelected.isEmpty()){
                 if(languageSelected.equalsIgnoreCase(language.getDisplayName()))
                     holder.speech_card_badge.setVisibility(View.VISIBLE);
                 else
                     holder.speech_card_badge.setVisibility(View.INVISIBLE);
             }
        }
    }

    @Override
    public int getItemCount() {
        return languageModelList == null?0:languageModelList.size();
    }
    public interface onItemClickListener{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(onItemClickListener listener){
        mListener = listener;
    }
    public class BottomSheetAdapterModelViewHolder extends RecyclerView.ViewHolder {
        TextView speakModelName,speakModelCode,speech_card_badge;
        public BottomSheetAdapterModelViewHolder(@NonNull View itemView, onItemClickListener listener) {
            super(itemView);
            speakModelName = itemView.findViewById(R.id.speakModelName);
            speakModelCode = itemView.findViewById(R.id.speakModelCode);
            speech_card_badge = itemView.findViewById(R.id.speech_card_badge);
            itemView.setOnClickListener(new View.OnClickListener() {
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

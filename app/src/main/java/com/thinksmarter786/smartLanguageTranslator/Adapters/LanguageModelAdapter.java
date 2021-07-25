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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.RemoteModelManager;

import java.util.List;

public class LanguageModelAdapter extends RecyclerView.Adapter<LanguageModelAdapter.LanguageModelViewHolder> {
    private Activity activity;
    private List<TranslateUtils.Language> languageModelList;
    private SharedPrefs sharedPrefs;
    private onItemClickListener mListener;
    public LanguageModelAdapter(Activity activity, List<TranslateUtils.Language> languageModelList) {
        this.activity = activity;
        this.languageModelList = languageModelList;
        this.sharedPrefs = SharedPrefs.getInstance(activity);
    }

    @NonNull
    @Override
    public LanguageModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.language_model_cardview, parent, false);
        return new LanguageModelViewHolder(mView,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull LanguageModelViewHolder holder, int position) {
        TranslateUtils.Language language = languageModelList.get(position);
        if(!language.getDisplayName().trim().isEmpty()){
            holder.languageModelName.setText(language.getDisplayName());
            holder.languageModelCode.setText(language.getCode());
            String languageSelected = sharedPrefs.getString("languageSelected");
            if(languageSelected.equalsIgnoreCase(language.getDisplayName())){
                holder.primaryLanguageSelection.setVisibility(View.VISIBLE);
                holder.languageDelete.setVisibility(View.INVISIBLE);
            }else{
                holder.primaryLanguageSelection.setVisibility(View.INVISIBLE);
                holder.languageDelete.setVisibility(View.VISIBLE);
            }
            if(holder.primaryLanguageSelection.getVisibility() != View.VISIBLE){
                holder.languageDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new TranslateUtils(RemoteModelManager.getInstance()).deleteLanguage(language).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                languageModelList.remove(position);
                                notifyDataSetChanged();
                            }
                        });
                    }
                });
            }

        }
    }


    @Override
    public int getItemCount() {
        return languageModelList==null?0:languageModelList.size();
    }

    public interface onItemClickListener{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(onItemClickListener listener){
        mListener = listener;
    }

    public static class LanguageModelViewHolder extends RecyclerView.ViewHolder{
        private TextView languageModelName,languageModelCode,languageDelete,primaryLanguageSelection;
        View view;
        public LanguageModelViewHolder(@NonNull View itemView, onItemClickListener listener) {
            super(itemView);
            view=itemView;
            languageModelName = itemView.findViewById(R.id.languageModelName);
            languageModelCode = itemView.findViewById(R.id.languageModelCode);
            languageDelete = itemView.findViewById(R.id.delete_badge);
            primaryLanguageSelection = itemView.findViewById(R.id.primaryLanguageSelection);
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

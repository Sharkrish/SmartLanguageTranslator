package com.thinksmarter786.smartLanguageTranslator.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.thinksmarter786.smartLanguageTranslator.R;
import com.thinksmarter786.smartLanguageTranslator.Utils.TranslateUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder> implements Filterable {

    private Context context;
    private List<TranslateUtils.Language> languageList;
    private List<TranslateUtils.Language> languageListAll;
    public LanguageAdapter(Context context, List<TranslateUtils.Language> languageList) {
        this.context = context;
        this.languageList = languageList;
        this.languageListAll = new ArrayList<>();
        this.languageListAll.addAll(this.languageList);
    }

    @NonNull
    @Override
    public LanguageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lanaguage_cardview, parent, false);
        return new LanguageViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull LanguageViewHolder holder, int position) {
        TranslateUtils.Language language = languageList.get(position);
        if(!language.getDisplayName().trim().isEmpty()){
            holder.languageName.setText(language.getDisplayName().trim());
            holder.card_badge.setVisibility(language.isSelected() ? View.VISIBLE : View.INVISIBLE);
            holder.languageCode.setText("("+language.getCode().trim()+")");
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    language.setSelected(!language.isSelected());
                    holder.card_badge.setVisibility(language.isSelected() ? View.VISIBLE : View.INVISIBLE);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return languageList == null ? 0:languageList.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence cseq) {
            List<TranslateUtils.Language> filStringList = new ArrayList<>();
            if(cseq.toString().isEmpty()){
                filStringList.addAll(languageListAll);
            }else{
                for(TranslateUtils.Language language : languageListAll){
                    if(language.getDisplayName().toLowerCase().contains(cseq.toString().toLowerCase())){
                        filStringList.add(language);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filStringList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            languageList.clear();
            languageList.addAll((Collection<? extends TranslateUtils.Language>) results.values);
            notifyDataSetChanged();
        }
    };

    class LanguageViewHolder extends RecyclerView.ViewHolder{
        private TextView languageName,languageCode,card_badge;
        private View view;
        public LanguageViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            languageName = itemView.findViewById(R.id.languageName);
            languageCode = itemView.findViewById(R.id.languageCode);
            card_badge = itemView.findViewById(R.id.card_badge);

        }
    }
}

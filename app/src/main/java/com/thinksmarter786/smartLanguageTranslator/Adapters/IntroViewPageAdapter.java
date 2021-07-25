package com.thinksmarter786.smartLanguageTranslator.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.thinksmarter786.smartLanguageTranslator.Models.IntroScreen_data;
import com.thinksmarter786.smartLanguageTranslator.R;

import java.util.List;

public class IntroViewPageAdapter extends PagerAdapter {
    Context mContext;
    List<IntroScreen_data> introScreen_data;

    public IntroViewPageAdapter(Context mContext, List<IntroScreen_data> introScreen_data) {
        this.mContext = mContext;
        this.introScreen_data = introScreen_data;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layoutScreen = inflater.inflate(R.layout.layout_screen,null);
        TextView title = layoutScreen.findViewById(R.id.intro_title);
        TextView desc = layoutScreen.findViewById(R.id.intro_desc);
        title.setText(introScreen_data.get(position).getTitle());
        desc.setText(introScreen_data.get(position).getDesc());
        container.addView(layoutScreen);
        return layoutScreen;
    }

    @Override
    public int getCount() {
        return introScreen_data.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}

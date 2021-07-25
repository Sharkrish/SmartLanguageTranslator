package com.thinksmarter786.smartLanguageTranslator.Adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class TranslateAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    private ArrayList<String> titleList = new ArrayList<>();
    private FragmentActivity activity;

    public TranslateAdapter(@NonNull FragmentManager fm, FragmentActivity activity) {
        super(fm);
        this.activity = activity;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentArrayList.get(position);
    }


    @Override
    public int getCount() {
        return titleList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
       /* SpannableStringBuilder sb = new SpannableStringBuilder("   " + titleList.get(position));
        Drawable myDrawable = activity.getResources().getDrawable(R.drawable.ic_baseline_mic_24);
        myDrawable.setBounds(2, 2, myDrawable.getIntrinsicWidth(), myDrawable.getIntrinsicHeight());
        ImageSpan span = new ImageSpan(myDrawable, DynamicDrawableSpan.ALIGN_BASELINE);
        sb.setSpan(span, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);*/
        return titleList.get(position);
    }
    public void addFragement(Fragment fragment,String title){
        fragmentArrayList.add(fragment);
        titleList.add(title);
    }
}

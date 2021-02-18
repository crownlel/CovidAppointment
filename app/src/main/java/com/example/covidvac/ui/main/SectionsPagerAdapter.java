package com.example.covidvac.ui.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.covidvac.R;
import com.example.covidvac.models.Citizen;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.citizen_main_tab_text_1, R.string.citizen_main_tab_text_2};
    private final Context mContext;
    private final Citizen citizen;

    public SectionsPagerAdapter(Context context, FragmentManager fm, Citizen citizen) {
        super(fm);
        mContext = context;
        this.citizen = citizen;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        //return PlaceholderFragment.newInstance(position + 1);
        switch (position){
            case 0:
                return NewAppointmentFragment.newInstance(citizen);
            case 1:
                return AppointmentsFragment.newInstance(citizen);
        }
        return null;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 2;
    }
}
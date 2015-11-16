package com.eugene.fithealth;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.eugene.fithealth.Activities.FragmentMeals;

public class MyPagerAdapter extends FragmentPagerAdapter {
    Context context;

    public MyPagerAdapter(Context ctx, FragmentManager fm) {
        super(fm);
        context = ctx;
    }

    private Fragment f = null;

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                f = new FragmentMeals();
                break;
            case 1:
                f = new FragmentMeals();
                break;
            case 2:
                f = new FragmentMeals();
                break;
            case 3:
                f = new FragmentMeals();
                break;

        }
        return f;
    }

    @Override
    public int getCount() { // Return the number of pages
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) { // Set the tab text
        if (position == 0) {
            return "TODAY'S MEALS";
        }
        if (position == 1) {
            return "GET ACTIVE";
        }
        if (position == 2) {
            return "POPULAR";
        }
        if (position == 3) {
            return "Social";
        }

        return getPageTitle(position);
    }
}
